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

Edit DRL (Drools Rule Language) files with language support provided by the [DRL Language Server](../drools-lsp).

## Features

- Syntax highlighting for `.drl` files.
- LSP features backed by a local JVM running the DRL Language Server: completion, diagnostics, hover, go-to-definition, references, document symbols, code lenses, folding ranges, inlay hints, and rename.

## Requirements

- Java 17+ available on the machine running the extension. The extension discovers Java through the `java.home` setting, or the `GHA_JAVA_HOME`/`JAVA_HOME` environment variables, falling back to `java` on the `PATH`.
