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

import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { State } from "../Store";

export function computeAllFeelVariableUniqueNames(
  drgElements: State["dmn"]["model"]["definitions"]["drgElement"],
  imports: State["dmn"]["model"]["definitions"]["import"]
) {
  const ret: UniqueNameIndex = new Map();

  drgElements ??= [];
  imports ??= [];

  for (let i = 0; i < drgElements.length; i++) {
    const drgElement = drgElements[i];
    ret.set(drgElement["@_name"]!, drgElement["@_id"]!);
  }

  for (let i = 0; i < imports.length; i++) {
    const _import = imports[i];
    ret.set(_import["@_name"], _import["@_id"]!);
  }

  return ret;
}
