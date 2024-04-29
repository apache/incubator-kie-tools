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

import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";
import { addMissingImportNamespaces } from "../mutations/addMissingImportNamespaces";
import { State } from "../store/Store";

export function normalize(model: State["dmn"]["model"]) {
  getNewDmnIdRandomizer()
    .ack({
      json: model.definitions.drgElement,
      type: "DMN15__tDefinitions",
      attr: "drgElement",
    })
    .ack({
      json: model.definitions.artifact,
      type: "DMN15__tDefinitions",
      attr: "artifact",
    })
    .ack({
      json: model.definitions["dmndi:DMNDI"],
      type: "DMN15__tDefinitions",
      attr: "dmndi:DMNDI",
    })
    .ack({
      json: model.definitions.import,
      type: "DMN15__tDefinitions",
      attr: "import",
    })
    .ack({
      json: model.definitions.itemDefinition,
      type: "DMN15__tDefinitions",
      attr: "itemDefinition",
    })
    .randomize({ skipAlreadyAttributedIds: true });

  addMissingImportNamespaces(model.definitions);

  return model;
}
