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

## drools-parser

Parser development has been done on drools main branch, so this module just takes the antlr4 grammar files from drools.

At the moment, the antlr4 grammar files are the same as the ones in drools main branch except for cleaning up unused java codes. In the future, we may need to customize the grammar files and/or add some drools dependencies to support requirements from lsp clients (DRL Editor) .

### Development tips

- IntelliJ IDEA has an ANTLR4 plugin, which "ANTLR Preview" window displays a parse tree. It is very useful to debug the parser rules.

### Resources

[The Definitive ANTLR 4 Reference](https://pragprog.com/titles/tpantlr2/the-definitive-antlr-4-reference/)
