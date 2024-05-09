<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

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
