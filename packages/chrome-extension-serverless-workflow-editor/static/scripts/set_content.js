/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// gets the initial content and sets in a pre tag
var kieTools_cmInstance = document.querySelector(".cm-content").cmView;
var kieTools_content = kieTools_cmInstance.view.state.doc.toString();
var kieTools_existingElement = document.getElementById("kie-tools__initial-content") ?? null;
if (kieTools_existingElement) {
  kieTools_existingElement.remove();
}
var kieTools_newElement = document.createElement("pre");
kieTools_newElement.id = "kie-tools__initial-content";
kieTools_newElement.style.display = "none";
kieTools_newElement.textContent = kieTools_content;
document.body.appendChild(kieTools_newElement);
