# S2I image to build builder image based on ubi8

This S2I is considered image builder that is equipped with build tools

- GraalVM
- Apache Maven
- JDK

To perform non native builds set the env **NATIVE** to **false**.

So it is equipped with all required tools to build a runnable native image of
Kogito type of projects.


### Usage:

docker run -it quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest /home/kogito/kogito-app-launch.sh -h

