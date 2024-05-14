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

This package contains graph processors that provide specific traversal for the different node/edge's content types.

- View traversal processor -> Use it for performing a traversal over the graph nodes that has View content
- Children traversal processor -> Use it for performing a traversal over the graph nodes that has Child content, so traverse the graph hierarchy using parent -> child direction
- Parent traversal processor -> Use it for performing a traversal over the graph nodes that has Parent content, so traverse the graph hierarchy using child -> parent direction
- Full content traversal processor -> Use it for performing a traversal over the all current available edge's contents: view, parent or child
