#!/usr/bin/env bash

GRADLE_CACHE_VOLUME_NAME="iakunin-java-libraries-gradle-cache"
GRADLE_DOCKER_IMAGE="gradle:7.6.1-jdk11-alpine"

docker run \
    --tty \
    --interactive \
    --privileged \
    --rm \
    --volume=${PWD}:/home/gradle/project \
    --volume=${HOME}/.m2:/root/.m2 \
    --volume=${GRADLE_CACHE_VOLUME_NAME}:/home/gradle/.gradle \
    --volume=/var/run/docker.sock:/var/run/docker.sock \
    --workdir=/home/gradle/project \
    ${GRADLE_DOCKER_IMAGE} \
    gradle "$@"

docker run \
    --tty \
    --interactive \
    --privileged \
    --rm \
    --volume=${PWD}:/home/gradle/project \
    --volume=${HOME}/.m2:/root/.m2 \
    --volume=${GRADLE_CACHE_VOLUME_NAME}:/home/gradle/.gradle \
    --volume=/var/run/docker.sock:/var/run/docker.sock \
    --workdir=/home/gradle/project \
    ${GRADLE_DOCKER_IMAGE} \
    chown -R $(id -u):$(id -g) .
