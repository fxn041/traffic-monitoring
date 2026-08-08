#!/bin/bash

# Configuration
DOCKER_USERNAME="bigdataa9"
IMAGE_TAG=$(git rev-parse --short HEAD)
PROJECT_BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/../.."

push_image() {
    local image_directory="$1"
    local repository_name="$2"

    # Build the image
    docker build -t "${DOCKER_USERNAME}/${repository_name}:${IMAGE_TAG}" -f ${image_directory}/Dockerfile-full .
    if [ $? -ne 0 ]; then
        echo "Failed to build ${repository_name} image."
        docker logout
        exit 1
    fi
    docker tag "${DOCKER_USERNAME}/${repository_name}:${IMAGE_TAG}" "${DOCKER_USERNAME}/${repository_name}:latest"

    # Push the images
    echo "Pushing ${repository_name} image with tag ${IMAGE_TAG}..."
    docker push "${DOCKER_USERNAME}/${repository_name}:${IMAGE_TAG}"
    if [ $? -ne 0 ]; then
        echo "[ERROR] Failed to push ${repository_name} image with tag ${IMAGE_TAG}."
        docker logout
        exit 1
    fi
    echo "Pushed ${repository_name} image with tag ${IMAGE_TAG}."
    echo "Pushing ${repository_name} image with latest tag..."
    docker push "${DOCKER_USERNAME}/${repository_name}:latest"
    if [ $? -ne 0 ]; then
        echo "[ERROR] Failed to push ${repository_name} image with latest tag."
        docker logout
        exit 1
    fi
    echo "Pushed ${repository_name} image with latest tag successfully."
}

cleanup() {
    local repository_name="$1"
    echo "Cleaning up local images for ${repository_name}..."
    docker rmi "${DOCKER_USERNAME}/${repository_name}:${IMAGE_TAG}"
    docker rmi "${DOCKER_USERNAME}/${repository_name}:latest"
    if [ $? -ne 0 ]; then
        echo "[WARNING] Failed to remove local images for ${repository_name}."
    else
        echo "Local images cleaned up successfully for ${repository_name}."
    fi
}

# Login to Dockerhub
docker login -u "${DOCKER_USERNAME}"
if [ $? -ne 0 ]; then
    echo "[ERROR] Docker login failed."
    exit 1
fi

# Push images
IMAGES=(
    "${PROJECT_BASE_DIR}/kafka/:kafka"
    "${PROJECT_BASE_DIR}/kafka_producer/:kafka-producer"
    "${PROJECT_BASE_DIR}/flink/taxi-job/:flink-taxi-job"
    "${PROJECT_BASE_DIR}/flink/taxi-active-job/:flink-taxi-active-job"
    "${PROJECT_BASE_DIR}/grafana/:grafana"
    "${PROJECT_BASE_DIR}/prometheus/:prometheus"
)

for entry in "${IMAGES[@]}"; do
    IFS=":" read -r dir repo <<< "$entry"
    push_image "$dir" "$repo"
done

# Cleanup images
#for entry in "${IMAGES[@]}"; do
#    IFS=":" read -r dir repo <<< "$entry"
#    cleanup "$repo"
#done

# Logout from Dockerhub
#docker logout
#if [ $? -ne 0 ]; then
#    echo "[ERROR] Docker logout failed."
#    exit 1
#fi
