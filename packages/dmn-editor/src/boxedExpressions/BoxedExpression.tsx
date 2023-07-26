import {
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/dist/expressions";
import {
  DMN14__tDecision,
  DMN14__tDefinitions,
  DMN14__tFunctionDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { DmnNodeWithExpression } from "../diagram/DmnNodeWithExpression";
import { beeToDmn } from "./beeToDmn";
import { dmnToBee, getUndefinedExpressionDefinition } from "./dmnToBee";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";

export function BoxedExpression({
  dmn,
  setDmn,
  nodeWithExpression,
  onBackToDiagram,
  container,
}: {
  dmn: { definitions: DMN14__tDefinitions };
  setDmn: React.Dispatch<React.SetStateAction<{ definitions: DMN14__tDefinitions }>>;
  nodeWithExpression: DmnNodeWithExpression;
  onBackToDiagram: () => void;
  container: React.RefObject<HTMLElement>;
}) {
  const widthsById = useMemo(() => {
    return (
      dmn.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]["di:extension"]?.["kie:ComponentsWidthsExtension"]?.[
        "kie:ComponentWidths"
      ] ?? []
    ).reduce((acc, c) => {
      if (c["@_dmnElementRef"] === undefined) {
        return acc;
      } else {
        return acc.set(c["@_dmnElementRef"], c["kie:width"] ?? []);
      }
    }, new Map<string, number[]>());
  }, [dmn.definitions]);

  const expressionDefinition = useMemo<ExpressionDefinition>(
    () =>
      nodeWithExpression
        ? dmnNodeToBoxedExpression(widthsById, nodeWithExpression)
        : getUndefinedExpressionDefinition(),
    [nodeWithExpression, widthsById]
  );

  const dataTypes = useMemo(
    () =>
      (dmn.definitions.itemDefinition ?? []).map((item) => ({
        isCustom: true,
        typeRef: item.typeRef!,
        name: item["@_name"]!,
      })),
    [dmn.definitions.itemDefinition]
  );

  const setExpressionDefinition = useCallback(
    (beeExpression: ExpressionDefinition) => {
      setDmn((prev) => {
        console.info(`TIAGO WRITE: Boxed Expression updated! ${beeToDmn(beeExpression)}`); // TODO: Actually mutate the DMN JSON.
        return prev;
      });
    },
    [setDmn]
  );

  return (
    <>
      <Label isCompact={true} className={"kie-dmn-editor--boxed-expression-back"} onClick={onBackToDiagram}>
        Back to Diagram
      </Label>
      <Divider inset={{ default: "insetMd" }} />
      <br />
      <>
        <BoxedExpressionEditor
          decisionNodeId={nodeWithExpression.content["@_id"]!}
          expressionDefinition={expressionDefinition}
          setExpressionDefinition={setExpressionDefinition}
          dataTypes={dataTypes}
          scrollableParentRef={container}
        />
      </>
    </>
  );
}

function dmnNodeToBoxedExpression(
  widthsById: Map<string, number[]>,
  dmnNode: DmnNodeWithExpression
): ExpressionDefinition {
  if (dmnNode.type == NODE_TYPES.bkm) {
    return {
      ...dmnToBee(widthsById, {
        expression: { __$$element: "functionDefinition", ...dmnNode.content.encapsulatedLogic },
      }),
      dataType: dmnNode.content.variable?.["@_typeRef"] as DmnBuiltInDataType,
      name: dmnNode.content["@_name"],
    };
  } else if (dmnNode.type == NODE_TYPES.decision) {
    return {
      ...dmnToBee(widthsById, dmnNode.content),
      dataType: dmnNode.content.variable?.["@_typeRef"] as DmnBuiltInDataType,
      name: dmnNode.content["@_name"],
    };
  } else {
    throw new Error(`Unknown type of node that has an expression '${(dmnNode as any).type}'.`);
  }
}
