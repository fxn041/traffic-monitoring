# Use an official Python runtime as a parent image
FROM docker.io/python:3.12-slim

# Set the working directory in the container
WORKDIR /app

# Copy the requirements.txt file from its location relative to the Dockerfile's directory (producer_dir)
COPY ./producer.py .

# copy producer.py to the container at /app
COPY ./requirements.txt .

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

ENTRYPOINT ["python", "producer.py"]
