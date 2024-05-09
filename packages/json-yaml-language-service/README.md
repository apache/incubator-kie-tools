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

## JSON and YAML Language Service

JSON and YAML language service to be reused in VSCode and Monaco editor.

## Features

- getCompletionItems() provides Code Completions for given locations.
- getCodeLenses() provides Code Lenses for given locations to be defined with custom editor commands.
- getDiagnostics() provides Code Diagnostic from a schema and/or from custom references.
- doRefValidation() provide Code Diagnostic by references.
- findNodesAtLocation() finds nodes at a JSONPath location. It's similar to `jsonc.findNodeAtLocation`, but it allows the use of '\*' as a wildcard selector.
- positions_equals() Test if position `a` equals position `b`.
- getLineContentFromOffset() Gets a line from a content at a specific offset.
- getNodeFormat() Detect the format of a node's content.
- indentText() Indent a text.
- matchNodeWithLocation() Check if a Node is in Location.
- nodeUpUntilType() From a node goes up to levels until a certain node type.
