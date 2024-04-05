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

import * as dns from "dns";
import * as os from "os";

/* 
Fix to allow the cors-proxy to correctly connect with local applications when running in macOs. More info: https://github.com/nodejs/node/issues/40702 
TODO: this is already fixed on Node 20: Remove this as part of https://github.com/apache/incubator-kie-issues/issues/392
*/
export const dnsFix = () => {
  const nodeVersion = +process.versions.node.split(".")[0];
  if (os.platform() === "darwin" && nodeVersion < 20) {
    dns.setDefaultResultOrder("ipv4first");
  }
};
