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

## DRL Editor

Language support for [DRL (Drools Rule Language)](https://kie.apache.org/docs/10.2.x/drools/drools/language-reference-traditional/index.html) files in Visual Studio Code, powered by a dedicated [Language Server](https://microsoft.github.io/language-server-protocol/).

## Requirements

- Java 17 or later (`JAVA_HOME` must be set or `java` must be on your `PATH`)
- Maven (for classpath resolution of Java types used in rules)

## Features

### Code Editing

- Syntax highlighting
- Code completion for grammar keywords, Java class names, fields/properties, and DRL `declare` types
- Inlay hints for bound variables
- Live class index refresh on recompile (no server restart required)

### Navigation

- Go-to definition for DRL and Java types
- Find references for DRL types and bound variables
- Rename for DRL declared types and bound variables
- Document symbols (outline view)
- Type hierarchy for DRL types
- Folding ranges for DRL blocks and comments

### Diagnostics

- Syntax error reporting
- Lint diagnostics (missing `end`, missing separators, unbalanced parentheses, etc.)
- Unknown-type lint with typo quick-fix for DRL-declared types

### Information

- Hover tooltips for DRL/Java types with doc-comment rendering
- Reference-count code lens for DRL declared types

## Extension Settings

| Setting                              | Default   | Description                                                   |
| ------------------------------------ | --------- | ------------------------------------------------------------- |
| `drools.lsp.logLevel`                | `INFO`    | Server-side log level                                         |
| `drools.lsp.lint.missingEnd`         | `warning` | Severity for missing `end` keyword                            |
| `drools.lsp.lint.missingSeparator`   | `warning` | Severity for missing constraint separator                     |
| `drools.lsp.lint.missingSemicolon`   | `warning` | Severity for missing semicolon in consequence                 |
| `drools.lsp.lint.unbalancedParens`   | `warning` | Severity for unbalanced parentheses                           |
| `drools.lsp.lint.unknownTypes`       | `warning` | Severity for unrecognized type references                     |
| `drools.lsp.lint.mvelPropertyAccess` | `off`     | Hint to prefer property-access style over getter calls in LHS |
| `drools.lsp.inlayHints.enabled`      | `true`    | Show inline type hints for bound variables                    |
| `drools.lsp.maven.pomPath`           | `""`      | Maven POM path(s) for classpath resolution                    |

All lint settings accept: `off`, `hint`, `info`, `warning`, `error`.

## Known Issues

If you find any issues, please report them in [GitHub Issues](https://github.com/apache/incubator-kie-issues/issues).
