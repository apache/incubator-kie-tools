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

# @kie-tools/accelerator-git-http-server

A reusable Git HTTP server for Accelerator packages. This package provides a simple HTTP server that can serve both Git repositories (using Git's "smart" HTTP protocol) and static content.

## Features

- **Git Smart HTTP Protocol**: Serves bare Git repositories over HTTP, allowing `git clone`, `git pull`, and `git push` operations
- **Static Content Serving**: Serves static files and directories alongside Git repositories

## Usage

### As a Library

```typescript
import { startGitHttpServer } from "@kie-tools/accelerator-git-http-server";

const server = startGitHttpServer({
  port: 8080,
  contentRoot: "./dist-dev",
  logPrefix: "my-accelerator", // Optional
});
```

### As a CLI Tool

```bash
kie-tools--accelerator-git-http-server [port] [content-root-path]
```

Example:

```bash
kie-tools--accelerator-git-http-server 8080 ./dist-dev
```

## Configuration Options

### `StartServerOptions`

| Option        | Type     | Required | Description                                                   |
| ------------- | -------- | -------- | ------------------------------------------------------------- |
| `port`        | `number` | Yes      | Port number for the HTTP server                               |
| `contentRoot` | `string` | Yes      | Root directory containing Git repositories and static content |
| `logPrefix`   | `string` | No       | Custom log prefix (defaults to "git-repo-http-dev-server")    |

## Expected directory structure

The `contentRoot` directory should contain:

- **Bare Git repositories**: Directories ending with `.git` will be served using Git's smart HTTP protocol
- **Static content**: All other directories and files will be served as static content

Example structure:

```
dist-dev/
├── my-repo.git/          # Bare Git repository (accessible via git clone)
├── static-files/         # Static content directory
│   ├── index.html
│   └── styles.css
└── another-repo.git/     # Another bare Git repository
```

## How It Works

1. **Git Repositories**: When a request is made to a path ending with `.git`, the server uses Git's `http-backend` to handle the request, enabling full Git operations over HTTP.
2. **Static Content**: All other requests are served as static files using the `serve-static` middleware.

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html
