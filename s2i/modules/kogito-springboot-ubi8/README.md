# S2I image to build runtime image based on ubi8

This S2I is considered a runtime image builder as it relies on another image
(built with `kogito-springboot-ubi8-s2i`) that actually produced the binaries to be executed
(the fat jar based on SpringBoot 2.1.x runtime). So this one will only get the binaries
and place into the container.

On top of the OS it has JRE installed (OpenJDK 1.8) to be able to execute fat jar.
