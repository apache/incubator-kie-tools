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

import { getDecisionServicePropertiesRelativeToThisDmn } from "../../mutations/addExistingDecisionServiceToDrd";
import { buildXmlHref } from "../../xml/xmlHrefs";
import { State } from "../Store";

export function computeContainingDecisionServiceHrefsByDecisionHrefs({
  thisDmnsNamespace,
  drgElementsNamespace,
  drgElements,
}: {
  thisDmnsNamespace: string;
  drgElementsNamespace: string;
  drgElements: State["dmn"]["model"]["definitions"]["drgElement"];
}) {
  drgElements ??= [];
  const decisionServiecHrefsByDecisionHrefs = new Map<string, string[]>();

  for (const drgElement of drgElements) {
    const drgElementHref = buildXmlHref({
      namespace: drgElementsNamespace === thisDmnsNamespace ? "" : drgElementsNamespace,
      id: drgElement["@_id"]!,
    });

    // Decision
    if (drgElement.__$$element === "decision") {
      decisionServiecHrefsByDecisionHrefs.set(
        drgElementHref,
        decisionServiecHrefsByDecisionHrefs.get(drgElementHref) ?? []
      );
    }
    // DS
    else if (drgElement.__$$element === "decisionService") {
      const { containedDecisionHrefsRelativeToThisDmn } = getDecisionServicePropertiesRelativeToThisDmn({
        thisDmnsNamespace,
        decisionServiceNamespace: drgElementsNamespace,
        decisionService: drgElement,
      });

      for (const containedDecisionHref of containedDecisionHrefsRelativeToThisDmn) {
        decisionServiecHrefsByDecisionHrefs.set(containedDecisionHref, [
          ...(decisionServiecHrefsByDecisionHrefs.get(containedDecisionHref) ?? []),
          drgElementHref,
        ]);
      }
    } else {
      // Ignore other elements
    }
  }

  return decisionServiecHrefsByDecisionHrefs;
}
