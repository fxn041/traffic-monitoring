# database_creator.py

## Requirements
1. Download the T-Drive trajectory data from [here](https://www.microsoft.com/en-us/research/publicationt-drive-trajectory-data-sample/).
2. extract the `T-drive Taxi Trajectories.zip` archive.
3. move the folder `taxi_log_2008_by_id` from the extracted archive into the `data` folder of this repository.
4. install the python package `pandas` using the command `pip3 install pandas`

## Usage
```bash
cd tools/database_creator/
python3 database_creator.py
```

The tool will create a `database.db`file in the `output`folder.

## Database Structure
```sql
CREATE TABLE IF NOT EXISTS taxi_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    taxi_id INTEGER,
    timestamp TEXT,
    longitude REAL,
    latitude REAL
)
```
| taxi_id | timestamp         | longitude | latitude |
|---------|-------------------|-----------|----------|
|        1|2008-02-02 15:36:08|  116.51172|  39.92123|
|        2|2008-02-02 13:37:16|  116.37481|  39.88782|
|    10196|2008-02-02 15:12:15|  116.60467|  40.13715|
