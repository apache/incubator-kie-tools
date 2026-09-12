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

## Formatting

_Format Document_ and _Format Selection_ rewrite a `.drl` file into one canonical shape. The formatter is a style enforcer, not a byte-preserving pretty-printer: it replaces your spacing rather than echoing it. Token content and comments survive; layout is the formatter's to decide. Formatting is idempotent, and the formatter refuses to write at all rather than risk damaging a file.

Three changes go beyond layout, and are together governed by `normalizeTerminators`: `import` gains a `;` and `global` loses one, and the `accumulate`/`groupby` source separator `,` becomes `;`. A positional constraint list always gets its closing `;` — that one is grammar, not style.

### Options

Each `drools.lsp.formatter.*` setting applies without a restart. Indentation is not among them: it follows the editor's `tabSize` and `insertSpaces`, which VS Code sends with every format request and which you can set per language under `[drools]`.

| Setting                | Default    | Effect                                                                                                                                                                                              |
| ---------------------- | ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `lineLength`           | `110`      | Wrap trigger, not a cap — it decides when a construct breaks; a line with nothing breakable runs past it                                                                                            |
| `lineEndings`          | `preserve` | `preserve` keeps the file's own ending; `lf` or `crlf` forces one                                                                                                                                   |
| `parenPadding`         | `true`     | `Person( age > 18 )`; off gives `Person(age > 18)`. An empty pair is always `()`                                                                                                                    |
| `bindingColonSpace`    | `false`    | `$p: Person(…)` and `a: int`; on gives `$p : Person(…)` and `a : int`                                                                                                                               |
| `normalizeTerminators` | `true`     | The three normalizations above; off keeps the source's choice                                                                                                                                       |
| `alignDeclarations`    | `true`     | `declare` fields and enum constants as aligned columns; off is one per line, single-spaced                                                                                                          |
| `headerMetadata`       | `indented` | Annotations and attributes of rule/query/declare headers: `indented` on their own lines one level in, `flush` on their own lines at column 0, `inline` on the header line, wrapping at `lineLength` |

The defaults are proposed. They are open for discussion in [kie-tools#3709](https://github.com/apache/incubator-kie-tools/issues/3709).

### Layout

Top-level constructs start at column 0 and their `end` returns to column 0. Statements are never reordered. Trailing whitespace is never emitted, a run of two or more blank lines collapses to one, and trailing blank lines are removed — the file ends with exactly one newline.

Between tokens: `(` and its contents are padded (or not, per `parenPadding`); `[ ]` and `{ }` stay tight; no space before `,` or `;`, one after `,`; none around `.`, `!.`, `#`; one around operators and between words; `)` followed by a word gets one (`) from`, `) do[x]`).

### `declare` blocks

The header is one line — `declare Name`, `declare trait|type Name`, `declare enum Name`, `declare entry-point Name`, `declare window Name` — with `extends` supertypes comma-separated. Fields and enum constants are laid out as tables when `alignDeclarations` is on: field rows align label-with-colon then type; constant rows align cell by cell. Column widths are the widest cell in the group plus one.

A blank line in your source ends an alignment group, so a short group is never stretched to match a long one. A comment on its own line does not end the group.

### Rules, queries, functions

```
rule <name> [extends <parent>]
  <annotations and attributes, per headerMetadata>
  when
    <conditions>
  then
    <consequence>
end
```

`extends` rides on the header line unless that exceeds `lineLength`. `query` has the same shape without `when`. `function` gets its signature normalized and its body emitted verbatim — Java inside a function is never touched.

### Conditions

A pattern is `$binding: Type( constraints )`, one line if it fits in `lineLength`, otherwise one constraint per line with the closing parenthesis back at the pattern's indent. An explicit `and` is kept as a leading `and ` on the next condition; an `or` between conditions goes on its own line; a parenthesized group puts `(` and `)` on their own lines. `accumulate` and `groupby` are always blocks: source pattern, `;`, functions, then any constraints.

### Consequences

Statements split at line breaks where parentheses balance, so a call already spread over several lines stays one statement. Each statement is one line if it fits, otherwise every parenthesized group that would overflow expands to one argument per line with `)` aligned to the line that opened it. Braces drive indentation; blank lines between statements are kept, collapsed to one.

### Comments

Line comments keep their text and are re-indented. Javadoc-shaped block comments have their `*` column squared up; a drawn banner (a run of four or more `*`) moves as a whole and keeps its columns. A trailing comment after a condition moves to its own line; one in a consequence stays. A `//` comment inside a pattern's parentheses forces that pattern multi-line.

### Turning the formatter off

A line comment reading exactly `@formatter:off` suspends formatting and `@formatter:on` resumes it; an unmatched `off` runs to end of file. Freezing works per top-level statement: any statement overlapping the region is emitted verbatim in full. Inside a frozen region only line endings are normalized.

### What it refuses

It writes nothing at all — never a partial file — when the input does not parse, when a rule's `when` block could not be read as one, or when its own output fails to re-parse. A refusal is logged at INFO in the _Drools LSP_ output channel; the editor sees no edits.

### Command line

The same engine is available as a CLI for hooks, CI and tooling. It is not published; build it locally:

```
mvn -f packages/drools-lsp/pom.xml -pl drools-formatter -am package
java -jar packages/drools-lsp/drools-formatter/target/drools-formatter-jar-with-dependencies.jar --help
```

`--check` exits 1 if any file would change; `--write` rewrites in place; `--write-dir` walks a directory; `--lines a:b` limits `--check`/`--write` to the statements overlapping those lines; `--stdin` formats standard input; `--config file.json` reads the options above (same keys, plus `tabSize` and `insertSpaces`). Exit 2 means at least one file was refused; a refused file is never written, but other files in the same run may have been.

### Known limitations

- Comments inside `accumulate(…)`/`groupby(…)` parentheses are dropped; put notes above the element.
- A block comment between a pattern's `(` and its first constraint is dropped; one between constraints is kept.
- `default:` with its statement on the same line is not split, while `case N:` is.
- Long lines are not guaranteed to fit — see the wrap-trigger note above.

## Commands

| Command                   | Description                                               |
| ------------------------- | --------------------------------------------------------- |
| `DRL: Select File Group…` | Pin the current file to a group, or clear an existing pin |
| `DRL: Reload File Groups` | Re-read grouping configuration from disk                  |

## Extension Settings

| Setting                                     | Default    | Description                                                   |
| ------------------------------------------- | ---------- | ------------------------------------------------------------- |
| `drools.lsp.logLevel`                       | `INFO`     | Server-side log level                                         |
| `drools.lsp.grouping`                       | `{}`       | DRL file grouping, declared inline (see above)                |
| `drools.lsp.lint.missingEnd`                | `warning`  | Severity for missing `end` keyword                            |
| `drools.lsp.lint.missingSeparator`          | `warning`  | Severity for missing constraint separator                     |
| `drools.lsp.lint.missingSemicolon`          | `warning`  | Severity for missing semicolon in consequence                 |
| `drools.lsp.lint.unbalancedParens`          | `warning`  | Severity for unbalanced parentheses                           |
| `drools.lsp.lint.unknownTypes`              | `warning`  | Severity for unrecognized type references                     |
| `drools.lsp.lint.mvelPropertyAccess`        | `off`      | Hint to prefer property-access style over getter calls in LHS |
| `drools.lsp.inlayHints.enabled`             | `true`     | Show inline type hints for bound variables                    |
| `drools.lsp.maven.pomPath`                  | `""`       | Maven POM path(s) for classpath resolution                    |
| `drools.lsp.java.sourcePaths`               | `[]`       | Extra Java source roots, beyond those found automatically     |
| `drools.lsp.java.packageFilters`            | `[]`       | Package prefixes limiting which Java source types are indexed |
| `drools.lsp.formatter.lineLength`           | `110`      | Formatter wrap trigger (see Formatting)                       |
| `drools.lsp.formatter.lineEndings`          | `preserve` | `preserve`, `lf` or `crlf`                                    |
| `drools.lsp.formatter.parenPadding`         | `true`     | Space inside non-empty parentheses                            |
| `drools.lsp.formatter.bindingColonSpace`    | `false`    | Space before a binding's or declare field's colon             |
| `drools.lsp.formatter.normalizeTerminators` | `true`     | Normalize `import`/`global` `;` and the accumulate separator  |
| `drools.lsp.formatter.alignDeclarations`    | `true`     | Column-align `declare` fields and enum constants              |
| `drools.lsp.formatter.headerMetadata`       | `indented` | `indented`, `flush` or `inline` header annotations/attributes |

All lint settings accept: `off`, `hint`, `info`, `warning`, `error`.

Formatter settings apply without a restart.

The project's own Java types resolve from `.java` sources, so completion, hover,
navigation and the unknown-type lint work on a fresh checkout, before Maven has
produced any class files. Compiled classes take precedence as soon as they
exist. Every `src/main/java` directory under the workspace is found
automatically, so `drools.lsp.java.sourcePaths` is only needed for source roots
that sit elsewhere; each entry is a literal directory path, absolute or
workspace-relative, not a glob. Both settings are read when the language server
starts, so changing either needs a restart.

## Known Issues

If you find any issues, please report them in [GitHub Issues](https://github.com/apache/incubator-kie-tools/issues).
