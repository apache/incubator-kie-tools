## YARD validator

###### Module for YARD (Yet Another Rule Definition) validation.

### Description

This module contains a standalone J2CL/Java module that can be used to validate YARD file. J2CL version is recommended
to be ran in a webworker due to the heavy load.

There is a static test page at demo/demo.html. Build the project before running it.

### Validation items currently covered

- Subsumption, when one row "eats" another by covering the same constraints as the subsumed row.
- Redundant rows. Either the same result is duplicated or one row is useless.

### Validation item ideas for future

- Masked rows, when a row subsumes another, but also blocks the subsumed row from getting ever activated.
- Conflicting rows. Two rows return different results with overlapping or subsuming constraints.
- Overlapping rows. Two rows can be activated by the same data, but the rows do not subsume each other.
- Gaps between the rows
