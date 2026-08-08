# created to read the csv data from the T-Drive dataset into a file-based sqlite database
from pathlib import Path
import sqlite3
from datetime import datetime
import pandas as pd

pragmas = [
    "PRAGMA journal_mode = OFF;",           # disable journaling
    "PRAGMA synchronous = OFF;",            # disable syncing
    "PRAGMA temp_store = MEMORY",           # store temporary data in memory 
]

# Creates a SQLite database with a taxi_data table
def create_database(db_path: str):

    conn = sqlite3.connect(db_path)
    cur = conn.cursor()

    for pragma in pragmas:
        cur.execute(pragma)
    
    # If the program is executed a second time, we don't want to insert the same
    # data a second time. So we drop the table if it already exists.
    cur.execute("""
        DROP TABLE IF EXISTS taxi_data
    """)

    cur.execute("""
        CREATE TABLE IF NOT EXISTS taxi_data (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            taxi_id INTEGER,
            datetime TEXT,
            timestamp INTEGER,
            longitude REAL,
            latitude REAL
        )
    """)

    conn.commit()

    return conn

def fill_timestamp(con: sqlite3.Connection):
    cur = con.cursor()
    cur.execute("""UPDATE taxi_data SET timestamp = unixepoch(datetime)""")
    con.commit()

def add_index_on_timestamp(con: sqlite3.Connection):
    cur = con.cursor()
    cur.execute("""CREATE INDEX IF NOT EXISTS idx_timestamp ON taxi_data(timestamp);""")
    con.commit()

def process_file(file_path: Path, conn: sqlite3.Connection):

    cur = conn.cursor()
    dtype = {
        'taxi_id': 'int', 
        'timestamp': 'str',
        'longitude': float,
        'latitude': float,
    }
    df = pd.read_csv(file_path, header=0, names=['taxi_id', 'datetime', 'longitude', 'latitude'], dtype=dtype)
    df.to_sql('taxi_data', conn, if_exists='append', index=False)

# walks through all the files in the given dir
def process_all_files(input_dir: str, conn: sqlite3.Connection):

    base = Path(input_dir)
    for file_path in base.iterdir():
        #  Only process regular files 
        if not file_path.is_file():
            continue

        #  Paths passed directly str(file_path)
        process_file(str(file_path), conn)

def main():
    #  Path to the SQLite database file
    db_path: Path = Path('output/database.db')
    #  Directory containing  input files
    input_dir: Path = Path('../../data/taxi_log_2008_by_id')

    conn = create_database(db_path)
    process_all_files(input_dir, conn)
    fill_timestamp(conn)
    add_index_on_timestamp(conn)
    conn.close()

if __name__ == '__main__':
    main()

