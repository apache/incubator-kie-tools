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

import { DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { buildXmlHref } from "../xml/xmlHrefs";
import * as __path from "path";

export const KIE_PMML_NAMESPACE = "https://kie.org/pmml";

export const allPmmlImportNamespaces = new Set([
  "https://www.dmg.org/PMML-4_4",
  "http://www.dmg.org/PMML-4_4", // http is not official, but there might be files using it.
  "https://www.dmg.org/PMML-4_3",
  "http://www.dmg.org/PMML-4_3",
  "https://www.dmg.org/PMML-4_2",
  "http://www.dmg.org/PMML-4_2",
  "https://www.dmg.org/PMML-4_1",
  "http://www.dmg.org/PMML-4_1",
  "https://www.dmg.org/PMML-4_0",
  "http://www.dmg.org/PMML-4_0",
  "https://www.dmg.org/PMML-3_2",
  "http://www.dmg.org/PMML-3_2",
  "https://www.dmg.org/PMML-3_1",
  "http://www.dmg.org/PMML-3_1",
  "https://www.dmg.org/PMML-3_0",
  "http://www.dmg.org/PMML-3_0",
  "https://www.dmg.org/PMML-2_1",
  "http://www.dmg.org/PMML-2_1",
  "https://www.dmg.org/PMML-2_0",
  "http://www.dmg.org/PMML-2_0",
  "https://www.dmg.org/PMML-1_1",
  "http://www.dmg.org/PMML-1_1",
]);

export function getPmmlNamespace({
  normalizedPosixPathRelativeToTheOpenFile,
}: {
  normalizedPosixPathRelativeToTheOpenFile: string;
}) {
  return buildXmlHref({ namespace: KIE_PMML_NAMESPACE, id: normalizedPosixPathRelativeToTheOpenFile });
}

export function getPmmlNamespaceFromDmnImport({ dmnImport }: { dmnImport: DMN15__tImport }) {
  return dmnImport["@_locationURI"]
    ? getPmmlNamespace({
        // We need to normalize the path here because they're always stored as explicit relative paths starting with `./` or `../`
        normalizedPosixPathRelativeToTheOpenFile: __path.normalize(dmnImport["@_locationURI"]),
      })
    : dmnImport["@_namespace"];
}
