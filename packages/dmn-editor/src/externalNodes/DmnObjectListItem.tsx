import * as React from "react";
import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Unpacked } from "../store/useDiagramData";
import { DataTypeLabel } from "../dataTypes/DataTypeLabel";
import { NodeIcon } from "../icons/Icons";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";

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
  const { importsByNamespace } = useDmnEditorDerivedStore();
  if (!dmnObject) {
    return <>{dmnObjectHref}</>;
  }

  const showFullFeelQName = relativeToNamespace !== namespace;

  const Icon = NodeIcon(getNodeTypeFromDmnObject(dmnObject));
  return (
    <Flex
      alignItems={{ default: "alignItemsCenter" }}
      justifyContent={{ default: "justifyContentFlexStart" }}
      spaceItems={{ default: "spaceItemsNone" }}
    >
      <div style={{ width: "40px", height: "40px", marginRight: 0 }}>
        <Icon />
      </div>
      <div>{`${
        showFullFeelQName
          ? buildFeelQNameFromNamespace({ namedElement: dmnObject, importsByNamespace, namespace }).full
          : dmnObject["@_name"]
      }`}</div>
      <div>
        {dmnObject.__$$element !== "knowledgeSource" ? (
          <DataTypeLabel typeRef={dmnObject.variable?.["@_typeRef"]} namespace={namespace} isCollection={false} /> // FIXME: Tiago --> Actually say if it's collection of not.
        ) : (
          <></>
        )}
      </div>
    </Flex>
  );
}
