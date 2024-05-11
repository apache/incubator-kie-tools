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

# How to enable live-reload on React apps inside the DMN Editor

### 1. Configure GWT web app on IntelliJ

![IntelliJ configuration](./intellij.png?raw=true)

> **Notice**:
>
> - Check the **Update resources on frame deactivation** checkbox
> - Add the dev mode parameter `-war /tmp/webapp` (let's keep this special directory in mind)

---

### 2. Start the DMN Loader build watcher

1. Go to `packages/stunner-editors-dmn-loader`
2. Run `DMN_LOADER__outputPath=/tmp/webapp/kogito-editors-js pnpm watch`

> **Notice**:
>
> - We're using `DMN_LOADER__outputPath=<special directory>/kogito-editors-js`

---

### 3. It works!

Now, changes that you make on the components exposed by the DMN Loader (e.g. Boxed Expressions) will be automatically picked up by the DMN Editor, so when you refresh the page, you'll see the updated code.
