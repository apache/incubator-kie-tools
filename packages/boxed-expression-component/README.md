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

# Boxed Expression Editor

This editor provides the possibility to edit the expression related to a Decision Node, or to a Business Knowledge Model's function.

## Static deployed showcase

[There](https://cutt.ly/boxed-expression-editor) you can access to the static deployed version of the showcase application for this editor. It will be manually updated as soon as new features will be added.

## Structure

The main component is `src/components/BoxedExpressionEditor/BoxedExpressionEditor.tsx`.
It represents the entry point for using the editor.

In the `showcase` folder, there is a tiny React application, which represent the Proof Of Value about how it is possible to integrate the `BoxedExpressionEditor` component inside another existing application.

Once the showcase application gets launched, you can see on the right side of the page the JSON that is actually produced for the corresponding selected logic type.
Such JSON represents the model data that must be adopted to initialize the `BoxedExpressionEditor` component, by populating its props.

The retrieval of the updated expression is performed by making usage of global functions, belonging to `beeApiWrapper` object, that must be available in the `Window` namespace and used by the `BoxedExpressionEditor` component.
All exposed function expected to exist, are defined in `src/api/BoxedExpressionEditor.ts`.

Consider that the showcase app is able to display the most updated JSON representing an expression, because uses such APIs (please refer to `showcase/src/index.tsx`).

## Scripts

In the main project (where the components actually live), it is possible to execute, from the root folder, the following scripts (`pnpm` is recommended):

```sh

# Remove 'dist' folder (such script is automatically called when the build is executed)
pnpm prebuild

# Build a production-ready artifact to be deployed
pnpm build:prod

# Execute all tests
pnpm test

# Trigger static code analysis
pnpm lint

# Trigger type checking
pnpm type-check

# Perform all the three checks above (tests, lint and type checking)
pnpm quality-checks
```

In the showcase project, these scripts are available:

```sh
# Start a local server to see the 'BoxedExpressionEditor' in action
pnpm start
# Compiles a production ready showcase application
pnpm build
# Run PlayWright 'BoxedExpressionEditor' tests.
pnpm test-e2e
# To update the PlayWright Snapshot files, used for the regression:
pnpm test-e2e:run -u
```

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
