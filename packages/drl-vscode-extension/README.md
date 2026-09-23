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
- Deprecated-syntax lint for constructs the DRL10 parser no longer supports, with migration quick-fixes

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

| Setting                              | Default   | Description                                                      |
| ------------------------------------ | --------- | ---------------------------------------------------------------- |
| `drools.lsp.logLevel`                | `INFO`    | Server-side log level                                            |
| `drools.lsp.grouping`                | `{}`      | DRL file grouping, declared inline (see above)                   |
| `drools.lsp.lint.missingEnd`         | `warning` | Severity for missing `end` keyword                               |
| `drools.lsp.lint.missingSeparator`   | `warning` | Severity for missing constraint separator                        |
| `drools.lsp.lint.missingSemicolon`   | `warning` | Severity for missing semicolon in consequence                    |
| `drools.lsp.lint.unbalancedParens`   | `warning` | Severity for unbalanced parentheses                              |
| `drools.lsp.lint.unknownTypes`       | `warning` | Severity for unrecognized type references                        |
| `drools.lsp.lint.mvelPropertyAccess` | `off`     | Hint to prefer property-access style over getter calls in LHS    |
| `drools.lsp.lint.deprecated`         | `warning` | Severity for syntax deprecated with the DRL10 parser (see below) |
| `drools.lsp.inlayHints.enabled`      | `true`    | Show inline type hints for bound variables                       |
| `drools.lsp.maven.pomPath`           | `""`      | Maven POM path(s) for classpath resolution                       |
| `drools.lsp.java.sourcePaths`        | `[]`      | Extra Java source roots, beyond those found automatically        |
| `drools.lsp.java.packageFilters`     | `[]`      | Package prefixes limiting which Java source types are indexed    |

All lint settings accept: `off`, `hint`, `info`, `warning`, `error`.

The project's own Java types resolve from `.java` sources, so completion, hover,
navigation and the unknown-type lint work on a fresh checkout, before Maven has
produced any class files. Compiled classes take precedence as soon as they
exist. Every `src/main/java` directory under the workspace is found
automatically, so `drools.lsp.java.sourcePaths` is only needed for source roots
that sit elsewhere; each entry is a literal directory path, absolute or
workspace-relative, not a glob. Both settings are read when the language server
starts, so changing either needs a restart.

## Deprecated syntax

Drools 10 ships two DRL parsers: the legacy `DRL6` parser it compiles with by default, and the ANTLR4 `DRL10` parser, enabled with `-Ddrools.drl.antlr4.parser.enabled=true`, which accepts a slimmer syntax ([apache/incubator-kie#6220](https://github.com/apache/incubator-kie/issues/6220)). The language server parses with the `DRL10` grammar, so a rule set that still uses the dropped syntax shows `no viable alternative` errors where the engine merely logs a deprecation. The lint names the construct instead, at its position:

| Construct                          | Example                         | Quick fix                    |
| ---------------------------------- | ------------------------------- | ---------------------------- |
| Half constraint                    | `age > 18 && < 65`              | Repeat the left operand      |
| Patterns joined with `&&`/`\|\|`   | `Person( ) && Account( )`       | `and` / `or`                 |
| Annotation inside an LHS pattern   | `Person( @watch(age) age > 1 )` | none — move or drop it       |
| `agenda-group`                     | `agenda-group "g"`              | none — migrate rulebase-wide |
| Custom operator without its prefix | `name supersetOf "x"`           | `##supersetOf`               |

A recognized construct replaces the parse errors on its line. `drools.lsp.lint.deprecated` sets the severity; `off` leaves the raw parse errors in place.

Custom operators are the `drools.evaluator.<id>` entries of `META-INF/kie.properties.conf` on the project classpath, read where the engine reads them. The server registers those ids with its parser, so `##id` is accepted in the editor as it is by the engine; an identifier no configuration declares stays a parse error. Operators registered from Java code are not seen.

## Known Issues

If you find any issues, please report them in [GitHub Issues](https://github.com/apache/incubator-kie-issues/issues).
