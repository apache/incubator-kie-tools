## @kie-tools/maven-config-setup-helper

This package helps to write `.mvn/maven.config` file idempotently without loosing its previous value.

It achieves that by creating a copy of the original file at `.mvn/maven.config.original`, so when writing to `.mvn/maven.config`, it combines the original file with the new contents.
