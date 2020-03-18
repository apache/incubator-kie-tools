# Native Runtime image for Kogito apps

This Runtime image is considered a runtime image as it only contains the needed
files used to execute a pre built Kogito application.

This is a lightweight and compact application container image.

This image can be used without the image builder, this image has the ability to copy
built artifacts and files into the image so you can perform faster image builds to test your application.

### Usage information:

docker run -it quay.io/kiegroup/kogito-quarkus-ubi8:latest /home/kogito/kogito-app-launch.sh -h

