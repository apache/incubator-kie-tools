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

## Commit message validation service

Provides a simple service to check for patterns in a commit message.

## Environment Variables

- `EXAMPLE_COMMIT_MESSAGE_VALIDATION_SERVICE__port <PORT_NUMBER>`

  Sets service port, otherwise it will use `env/index.js` port.

- `EXAMPLE_COMMIT_MESSAGE_VALIDATION_SERVICE__validators <validatorName1>:<validatorParameters1>;<validatorName2>:<validatorParameters2>...`

  Enables and configures validators. The value is a list of `;` separated validators that are parameterized with anything after `:`. e.g.: `Length:5-72;IssuePrefix:kie-issues#*` will enable the Lenght validator, with min 5 and max 72 characters, and will also enable the IssuePrefix validator, with the prefix pattern being `kie-issues#*`.

## API

### - `/validate`

[POST] Validates a commit message against the enabled validators.

- **Request body:**
  ```
  String with your commit message.
  ```
- **Request response:**
  ```json
  {
    "result": true | false,
    "reasons": []string | undefined
  }
  ```
