# S2I image to build runtime image based on ubi8

This S2I is considered a runtime image builder as it relies on another image
(built with `kogito-quarkus-ubi8-s2i`) that actually produced the binaries to be executed
(the native image based on Quarkus runtime). So this one will only get the binaries
and place into the container.

With that it produces lightweight and compact application container image.
