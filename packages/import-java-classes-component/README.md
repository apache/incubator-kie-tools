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

# Import Java Classes component

This editor provides the possibility to edit the expression related to a Decision Node, or to a Business Knowledge Model's function.

## Static deployed showcase

[There](https://yesamer.github.io/import-java-classes/) you can access to the static deployed version of the showcase application for this editor. It will be manually updated as soon as new features will be added.

## Structure

In the `showcase` folder, there is a tiny React application, which represent the Proof Of Value about how it is possible to integrate the `ImportJavaClasses` component inside another existing application.

## Scripts

In the main project (where the components actually live), it is possible to execute, from the root folder, the following scripts (`pnpm` is recommended):

```sh

# Remove 'dist' folder (such script is automatically called when the build is executed)
pnpm prebuild

# Build a production-ready artifact to be deployed
pnpm build

# Execute all tests
pnpm test

# Trigger static code analysis
pnpm lint

# Trigger type checking
pnpm type-check

# Perform all the three checks above (tests, lint and type checking)
pnpm quality-checks
```

In the showcase project, only two scripts are available:

```sh
# Start a local server to see the 'ImportJavaClasses' in action
pnpm start
# Compiles a production ready showcase application
pnpm build
```
