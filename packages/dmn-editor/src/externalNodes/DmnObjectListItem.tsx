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
import { Normalized } from "../normalization/normalize";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";

export function DmnObjectListItem({
  dmnObject,
  dmnObjectHref,
  namespace,
  relativeToNamespace,
}: {
  dmnObject: Unpacked<Normalized<DMN15__tDefinitions>["drgElement"]> | undefined;
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

  // The dmnObject represented here can be a node from a 3rd model that is not included in this model.
  // For example, consider a "Local Decision Service" with an encapsulated "Decision-A" from "Model A",
  // but that "Decision-A" have an "Input-B" that is from "Model B", which is not included in local model.
  //
  // Model Name: Local Model.dmn
  // Nodes: Local Decision Service
  // Included Models: Model-A.dmn
  //
  // Model Name: Model-A.dmn
  // Nodes: Decision-A
  // Included Models: Model-B.dmn
  //
  // Model Name: Model-B.dmn
  // Nodes: Input-B
  // Included Models: [none]
  //
  // So, the "Local Model" only "knows" the nodes from "Model-A" and NOT from "Model-B".
  // That's why we have different logic to build description for "known dmnObjects" and "unknown dmnObjects".
  const isNamespaceIncluded = useMemo(
    () => namespace === relativeToNamespace || importsByNamespace.has(namespace),
    [importsByNamespace, namespace, relativeToNamespace]
  );

  const notIncludedNamespaceDescription = useMemo(() => {
    return `${namespace.substring(0, 11)}...${namespace.substring(namespace.length - 4)}`;
  }, [namespace]);

  const displayName = useMemo(
    () =>
      dmnObject && isNamespaceIncluded
        ? buildFeelQNameFromNamespace({
            namedElement: dmnObject,
            importsByNamespace,
            namespace,
            relativeToNamespace,
          }).full
        : dmnObject?.["@_name"],
    [dmnObject, importsByNamespace, isNamespaceIncluded, namespace, relativeToNamespace]
  );

  const nodeTypeTooltipDescription = useMemo(() => {
    if (dmnObject === undefined) {
      throw new Error("nodeTypeDescription can't be defined without a DMN object");
    }
    const nodeType = getNodeTypeFromDmnObject(dmnObject);
    if (nodeType === undefined) {
      throw new Error("Can't determine nodeTypeDescription with undefined node type");
    }
    if (nodeType === NODE_TYPES.decision) {
      return "Decision";
    } else if (nodeType === NODE_TYPES.inputData) {
      return "Input Data";
    } else {
      return "Unknown";
    }
  }, [dmnObject]);

  const toolTip = useMemo(() => {
    return dmnObject && isNamespaceIncluded ? (
      <p>{displayName}</p>
    ) : (
      <div>{`This ${nodeTypeTooltipDescription} node is from an external model that is not included in this one. Namespace: ${namespace}`}</div>
    );
  }, [displayName, dmnObject, isNamespaceIncluded, namespace, nodeTypeTooltipDescription]);

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
    <Tooltip content={toolTip} isContentLeftAligned={true}>
      <Flex
        alignItems={{ default: "alignItemsCenter" }}
        justifyContent={{ default: "justifyContentFlexStart" }}
        spaceItems={{ default: "spaceItemsNone" }}
      >
        <div style={{ width: "40px", height: "40px", marginRight: 0 }}>
          <Icon />
        </div>
        {!isNamespaceIncluded && (
          <div
            style={{
              backgroundColor: "#f0f0f0",
              color: "#6a6e72",
            }}
          >{`${notIncludedNamespaceDescription}.`}</div>
        )}
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
    </Tooltip>
  );
}
