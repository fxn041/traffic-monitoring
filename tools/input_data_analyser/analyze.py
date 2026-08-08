import matplotlib.pyplot as plt
import numpy as np
import os
import pandas as pd
import sys

def analyze_data(directory):
    taxi_count = 0
    total_events = 0
    max_events = 0
    min_events = float('inf')
    event_counts = []

    # Iterate over all taxis
    for file_name in os.listdir(directory):
        if file_name.endswith(".txt"):
            taxi_count += 1
            file_path = os.path.join(directory, file_name)
            
            # Check whether the file contains events
            if os.path.getsize(file_path) > 0:
                # Read the file into a DataFrame
                data = pd.read_csv(file_path, header=None)
                row_count = len(data)
            else:
                row_count = 0
            
            # Update statistics
            total_events += row_count
            max_events = max(max_events, row_count)
            min_events = min(min_events, row_count)
            event_counts.append(row_count)

    # Calculate statistics
    avg_rows = total_events / taxi_count if taxi_count > 0 else 0
    std_dev = np.std(event_counts) if event_counts else 0
    median = np.median(event_counts) if event_counts else 0

    # Print the results
    print(f"Number of taxis: {taxi_count}")
    print(f"Total number of events: {total_events}")
    print(f"Maximum events for a single taxi: {max_events}")
    print(f"Minimum events for a single taxi: {min_events}")
    print(f"Average events per taxi: {avg_rows:.2f}")
    print(f"Standard deviation of events: {std_dev:.2f}")
    print(f"Median events per taxi: {median}")

    # Plot the histogram
    if event_counts:
        plt.hist(event_counts, bins=range(0, max(event_counts) + 10, 10), edgecolor='black')
        plt.title("Distribution of Events per Taxi")
        plt.xlabel("Events")
        plt.ylabel("Taxis")
        plt.grid(axis='y', linestyle='--', alpha=0.8)
        plt.savefig("events_distribution.png")

if __name__ == "__main__":
    # Check if a directory is provided as a command-line argument
    if len(sys.argv) > 1:
        data_directory = sys.argv[1]
    else:
        # Fallback to the default directory
        data_directory = "../../data/taxi_log_2008_by_id/"
    
    # Analyze the data
    print("Analyzing data in directory:", data_directory, "(this may take a while)")
    analyze_data(data_directory)
    print("Analysis complete.")
