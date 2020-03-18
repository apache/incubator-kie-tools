# S2I image to build builder image based on ubi8

This S2I is considered image builder that is equipped with build tools

- OpenJDK 1.8
- Apache Maven
- JDK

So it is equipped with all required tools to build a runnable fat jar of
Kogito type of projects.

### Usage:

docker run -it quay.io/kiegroup/kogito-springboot-ubi8-s2i:latest /home/kogito/kogito-app-launch.sh -h

