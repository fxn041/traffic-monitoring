# Pushing Docker Images to Docker Hub
This short guide explains how to push Docker images to the [bigdataa9 Docker Hub account](https://hub.docker.com/u/bigdataa9).

## Prerequisites
- **Git**: Ensure Git is installed, the script uses Git commit hashes for the image tags.
- **Docker**: Install Docker and ensure the Docker daemon is running.
- **Docker Hub Account**: You must have access to the `bigdataa9` user on Docker Hub.

## Images to Push
The following images are maintained:
- `kafka-producer`
- `flink-taskmanager`
- `flink-taxi-speed-job`
- `flink-taxi-distance-job`
- `flink-taxi-active-job`
- `grafana`

## Pushing Images
A script is provided to automate the image push process:
```bash
../tools/push_images/push_images.sh
```

The script will build and push the images listed above to Docker Hub.
For the image tag, git commit hashes will be used and the latest tag is set to the newly uploaded image.
The script will automatically prompt you to log in to Docker Hub. You will need to enter the Docker Hub account password when prompted.
