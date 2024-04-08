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

import * as React from "react";
import { useMemo } from "react";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Unpacked } from "../tsExt/tsExt";
import { TypeRefLabel } from "../dataTypes/TypeRefLabel";
import { NodeIcon } from "../icons/Icons";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditorStore } from "../store/StoreContext";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";

export function DmnObjectListItem({
  dmnObject,
  dmnObjectHref,
  namespace,
  relativeToNamespace,
}: {
  dmnObject: Unpacked<DMN15__tDefinitions["drgElement"]> | undefined;
  dmnObjectHref: string;
  namespace: string;
  relativeToNamespace: string;
}) {
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());
  const { externalModelsByNamespace } = useExternalModels();
  const allTopLevelDataTypesByFeelName = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allTopLevelDataTypesByFeelName
  );
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());

  const displayName = dmnObject
    ? buildFeelQNameFromNamespace({
        namedElement: dmnObject,
        importsByNamespace,
        namespace,
        relativeToNamespace,
      }).full
    : dmnObjectHref;

  const isValid = useDmnEditorStore((s) =>
    DMN15_SPEC.namedElement.isValidName(
      dmnObject?.["@_id"] ?? generateUuid(),
      displayName,
      s.computed(s).getAllFeelVariableUniqueNames()
    )
  );

  const Icon = useMemo(() => {
    if (dmnObject === undefined) {
      throw new Error("Icon can't be defined without a DMN object");
    }
    const nodeType = getNodeTypeFromDmnObject(dmnObject);
    if (nodeType === undefined) {
      throw new Error("Can't determine node icon with undefined node type");
    }
    return NodeIcon({ nodeType, isAlternativeInputDataShape });
  }, [dmnObject, isAlternativeInputDataShape]);

  return !dmnObject ? (
    <>{dmnObjectHref}</>
  ) : (
    <Flex
      alignItems={{ default: "alignItemsCenter" }}
      justifyContent={{ default: "justifyContentFlexStart" }}
      spaceItems={{ default: "spaceItemsNone" }}
    >
      <div style={{ width: "40px", height: "40px", marginRight: 0 }}>
        <Icon />
      </div>
      <div style={{ color: isValid ? undefined : "red" }}>{`${displayName}`}</div>
      <div>
        {dmnObject.__$$element !== "knowledgeSource" ? (
          <>
            &nbsp;
            <TypeRefLabel
              typeRef={dmnObject.variable?.["@_typeRef"]}
              relativeToNamespace={namespace}
              isCollection={
                allTopLevelDataTypesByFeelName.get(dmnObject.variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined)
                  ?.itemDefinition["@_isCollection"]
              }
            />
          </>
        ) : (
          <></>
        )}
      </div>
    </Flex>
  );
}
