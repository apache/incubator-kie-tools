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

## File Grouping

Scopes completion, navigation and validation to the files a rule compiles with, so `declare` types, imports, functions and `global` declarations resolve across the group. Resolved in this order:

1. The `drools.lsp.grouping` setting
2. `drl-lsp-kbases.json` anywhere in the workspace — same content as the setting, for committed grouping
3. `META-INF/kmodule.xml` — the `packages` and `includes` attributes the build already uses
4. The containing directory of the active file, if none of the above

Changes apply without a restart. The status bar shows the active group, and pins one when a file matches several; pins persist per workspace, and the tooltip names the declaring file. Groups from a `kmodule.xml` show as **KIE base**, all others as **DRL group**.

### Declaring groups

Use `packages`/`includes` for `kmodule.xml` semantics, or `files` for an explicit ordered list. Relative paths resolve against the workspace root in the setting, against the file's own directory in `drl-lsp-kbases.json`.

```json
{
  "kbases": [
    {
      "name": "validation",
      "packages": ["!com.example.validation.internal.*", "com.example.validation.*"],
      "includes": ["shared"]
    },
    { "name": "legacy", "files": ["rules/Types.drl", "rules/Enums.drl"] }
  ]
}
```

> In `packages`, the first matching pattern decides — including its sign. List exclusions before the wildcard they carve out of.

### Adopting an existing manifest

`sources` allows rule group definitions with alternate syntax. `aliases` maps each canonical key to one or more alternate keys; several may collapse onto one.

Paths listed explicitly under `files` are taken as given — build output is filtered out of discovered files, not out of a path you named yourself.

```json
{
  "sources": [
    {
      "include": "**/*_Rule-Configs.json",
      "pathsRelativeTo": ["**/src/main/resources"],
      "aliases": {
        "kbases": "rule.config.list",
        "name": "rule.config.type",
        "files": ["relative.path.list", "absolute.path.list"]
      }
    }
  ]
}
```

Same-named groups from several files are merged, with a warning.

## Commands

| Command                   | Description                                               |
| ------------------------- | --------------------------------------------------------- |
| `DRL: Select File Group…` | Pin the current file to a group, or clear an existing pin |
| `DRL: Reload File Groups` | Re-read grouping configuration from disk                  |

## Extension Settings

| Setting                              | Default   | Description                                                   |
| ------------------------------------ | --------- | ------------------------------------------------------------- |
| `drools.lsp.logLevel`                | `INFO`    | Server-side log level                                         |
| `drools.lsp.grouping`                | `{}`      | DRL file grouping, declared inline (see above)                |
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
