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

# Build partitioning

This script uses `bun` as runtime and has the purpose of splitting the monorepo build in N partitions that, when built, will ensure the entire monorepo has been covered.

Each partition is completely independent from each other, and partition builds will be assigned a mode:

- `none`: No source code changes or none of the changes affect this partition.
- `full`: All packages in the partition need to be built and tested.
- `partial`: Only selected packages of the partition were affected by the changes, so only this subset needs to be rebuilt and tested.

### Usage

At the root of the monorepo, you can run

```bash
npx bun .github/supporting-files/ci/build-partitioning/build_partitioning.ts \
    --outputPath='/tmp/partitions.json' \
    --forceFull="false" \
    --baseSha="$(git rev-parse HEAD~1)" \
    --headSha="$(git rev-parse HEAD~0)" \
    --graphJsonPath='./repo/graph.json' \
    --partition='.github/supporting-files/ci/partitions/partition0.txt' \
    --partition='.github/supporting-files/ci/partitions/partition1.txt'
```

- `--outputPath`: Where the resulting JSON should be written to.
- `--forceFull`: Wheter or not to force `full` mode
- `--baseSha`: Base Git commit SHA.
- `--headSha`: HEAD Git commit SHA.
- `--graphJsonPath`: Where to find the `graph.json` of the monorepo in Datavis graph format.
- `--partition`: Files containing new-line-separated leaf package names that define a partition. Accepts multiple values.
