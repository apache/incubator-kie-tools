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

# Apache KIE Tools Release Guide

The canonical release documentation and single source of truth for the Apache KIE release procedure is maintained on the Apache KIE community website:

👉 **[Apache KIE Release Procedure](https://kie.apache.org/community/)** (hosted in the [`apache/incubator-kie-website`](https://github.com/apache/incubator-kie-website) repository).

---

## Repository-Specific Release Tooling

This repository (`incubator-kie-tools`) provides a single unified release script in [`scripts/release/`](../scripts/release/):

- **[`scripts/release/README.md`](../scripts/release/README.md)**: Detailed documentation for the release script.
- **[`scripts/release/release-all.sh`](../scripts/release/release-all.sh)**: The release script — builds all components and optionally publishes them.

All components are built and released together in one script under one build. There are no separate per-component release scripts.

### Common Commands:

```bash
# 1. Update version across all packages
pnpm update-version-to <version>

# 2. Align Kogito Maven dependency version
pnpm update-kogito-version-to --maven <version>

# 3. Update stream name
pnpm update-stream-name-to <stream-name>

# 4. Generate all release candidate artifacts
./scripts/release/release-all.sh 10.3.0 --rc

# 5. Publish artifacts to registries (after vote passes)
./scripts/release/release-all.sh 10.3.0 --publish
```

For Jenkins CI pipelines, see [`.ci/jenkins/Jenkinsfile.103xplus.release-candidate`](../.ci/jenkins/Jenkinsfile.103xplus.release-candidate) (Phase 1: build RC and publish to Apache SVN) and [`.ci/jenkins/Jenkinsfile.103xplus.release-publish`](../.ci/jenkins/Jenkinsfile.103xplus.release-publish) (Phase 2: publish to public registries after vote).
