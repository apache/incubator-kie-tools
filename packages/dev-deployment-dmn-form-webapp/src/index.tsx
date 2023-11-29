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

import "@patternfly/react-core/dist/styles/base.css";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { DmnFormApp } from "./DmnFormApp";
import * as path from "path";
import "../static/resources/style.css";

// The following regular expression matches everything between the first and last '/'.
const re = new RegExp("^([^/]*)/|/(?=[^/]*$)", "g");

const baseOrigin = window.location.origin;
const basePath = path.join(window.location.pathname, window.location.pathname !== "/" ? ".." : "").replace(re, "$1");

ReactDOM.render(<DmnFormApp baseOrigin={baseOrigin} basePath={basePath} />, document.getElementById("app")!);
