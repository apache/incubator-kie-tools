# Build partitioning

This script uses `bun` as runtime and has the purpose of splitting the monorepo build in 2 partitions that, when built, will ensure the entire monorepo has been covered.

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
    --graphJsonPath='./repo/graph.json'
```

- `--outputPath`: Where the resulting JSON should be written to.
- `--forceFull`: Wheter or not to force `full` mode
- `--baseSha`: Base Git commit SHA.
- `--headSha`: HEAD Git commit SHA.
- `--graphJsonPath`: Where to find the `graph.json` of the monorepo in Datavis graph format.
