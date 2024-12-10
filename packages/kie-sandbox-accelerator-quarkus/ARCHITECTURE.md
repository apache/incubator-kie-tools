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

# @kie-tools/kie-sandbox-accelerator-quarkus (architecture)

This package is not a normal Maven package, as its purpose is not to produce a JAR or a container image, but rather, source code to be used as a KIE Sandbox Accelerator. Therefore, its pom.xml file does not depend on `@kie-tools/maven-base`.

The source code template is hosted at `git-repo-content-src` and contains a `pom.xml.envsubst` file, which is interpolated with versions parametrized during the build itself.

Final artifacts are at the `./dist/git-repo-content` folder, and can be copied and pushed to a Git repository.

During development, a naive HTTP server supporting `git backend-http` (`./dev-server/server.mjs`) serves this content as a bare Git repository, as well as static content. This mimics GitHub's (and other Git providers) bare repo and raw files serving capabilities.

Testing is done by pretending to use it as a user would, running a Maven build and checking it if succeeds.

### References and credits

- https://github.com/fuubi/node-git-http-backend
- https://git-scm.com/book/en/v2/Git-on-the-Server-Smart-HTTP
- https://git-scm.com/docs/git-http-backend
- https://github.com/isomorphic-git/isomorphic-git/blob/main/src/managers/GitRemoteHTTP.js#L135
