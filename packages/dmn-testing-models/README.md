## DMN testing models

This package is meant to contain all the DMN models published inside `org.kie:kie-dmn-test-resources` to make them available for testing purposes.

Simply issue `mvn clean generated-resources` to download the jar and extract the models under `target/generated-resources/valid_models`.

Models are separated between < 1.5 version and 1.5 version.
For future DMN versions there will be version specific folders.

The original `org.kie:kie-dmn-test-resources` also contains invalid models, but for the moment being we use only the valid ones to verify round-trip validation.
