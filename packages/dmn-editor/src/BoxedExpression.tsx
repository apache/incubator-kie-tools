import * as React from "react";
import { DmnNodeWithExpression } from "./DmnEditor";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/dist/expressions";
import {
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import { useCallback, useMemo } from "react";
import {
  DMN14__tDecision,
  DMN14__tDefinitions,
  DMN14__tFunctionDefinition,
  DMN14__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { SPEC } from "./Spec";

export function BoxedExpression({
  dmn,
  setDmn,
  openNodeWithExpression,
  setOpenNodeWithExpression,
  container,
}: {
  dmn: { definitions: DMN14__tDefinitions };
  setDmn: React.Dispatch<React.SetStateAction<{ definitions: DMN14__tDefinitions }>>;
  openNodeWithExpression: DmnNodeWithExpression;
  setOpenNodeWithExpression: React.Dispatch<React.SetStateAction<DmnNodeWithExpression | undefined>>;
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
      openNodeWithExpression ? dmnNodeToBoxedExpression(widthsById, openNodeWithExpression) : newExpressionDefinition(),
    [openNodeWithExpression, widthsById]
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
        console.info(beeToDmn(beeExpression)); // TODO: Actually mutate the DMN JSON.
        return prev;
      });
    },
    [setDmn]
  );

  return (
    <>
      <Label
        isCompact={true}
        className={"kie-dmn-editor--boxed-expression-back"}
        onClick={() => setOpenNodeWithExpression(undefined)}
      >
        Back to Diagram
      </Label>
      <Divider inset={{ default: "insetMd" }} />
      <br />
      <>
        <BoxedExpressionEditor
          decisionNodeId={openNodeWithExpression.content["@_id"]!}
          expressionDefinition={expressionDefinition}
          setExpressionDefinition={setExpressionDefinition}
          dataTypes={dataTypes}
          scrollableParentRef={container}
        />
      </>
    </>
  );
}

function newExpressionDefinition(): ExpressionDefinition {
  return {
    id: generateUuid(),
    logicType: ExpressionDefinitionLogicType.Undefined,
    dataType: DmnBuiltInDataType.Undefined,
  };
}

function dmnNodeToBoxedExpression(
  widthsById: Map<string, number[]>,
  dmnNode: DmnNodeWithExpression
): ExpressionDefinition {
  if (dmnNode.type == "bkm") {
    return {
      ...dmnToBee(widthsById, { functionDefinition: dmnNode.content.encapsulatedLogic }),
      dataType: dmnNode.content.variable?.["@_typeRef"] as DmnBuiltInDataType,
      name: dmnNode.content["@_name"],
    };
  } else if (dmnNode.type == "decision") {
    return {
      ...dmnToBee(widthsById, dmnNode.content),
      dataType: dmnNode.content.variable?.["@_typeRef"] as DmnBuiltInDataType,
      name: dmnNode.content["@_name"],
    };
  } else {
    throw new Error(`Unknown type of node that has an expression '${(dmnNode as any).type}'.`);
  }
}

export type DmnExpression = DMN14__tDecision | DMN14__tFunctionDefinition | undefined;

/** Converts an ExpressionDefinition to a DMN JSON. This convertion is
 *  necessary for historical reasons, as the Boxed Expression Editor was
 *  created prior to the DMN Editor, needing to declare its own model. */
function beeToDmn(expression: ExpressionDefinition): DmnExpression {
  return {} as any;
}

/** Converts a DMN JSON to an ExpressionDefinition. This convertion is
 *  necessary for historical reasons, as the Boxed Expression Editor was
 *  created prior to the DMN Editor, needing to declare its own model. */
function dmnToBee(widthsById: Map<string, number[]>, dmnExpr: DmnExpression): ExpressionDefinition {
  if (!dmnExpr) {
    return newExpressionDefinition();
  } else if (dmnExpr.literalExpression) {
    const l = dmnExpr.literalExpression as DMN14__tLiteralExpression;
    return {
      id: l["@_id"]!,
      name: l["@_label"],
      logicType: ExpressionDefinitionLogicType.Literal,
      dataType: l["@_typeRef"] as DmnBuiltInDataType,
      content: l.text,
      width: widthsById.get(l["@_id"]!)?.[0],
    };
  } else if (dmnExpr.decisionTable) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.DecisionTable,
    // };
  } else if (dmnExpr.relation) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.Relation,
    // };
  } else if (dmnExpr.context) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.Context,
    // };
  } else if (dmnExpr.invocation) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.Invocation,
    // };
  } else if (dmnExpr.functionDefinition) {
    const f = dmnExpr.functionDefinition;
    const basic = {
      id: f["@_id"]!,
      dataType: f["@_typeRef"] as DmnBuiltInDataType,
      logicType: ExpressionDefinitionLogicType.Function as const,
      formalParameters: (f.formalParameter ?? []).map((p) => ({
        id: p["@_id"]!,
        name: p["@_name"]!,
        dataType: p["@_typeRef"]! as DmnBuiltInDataType,
      })),
    };

    const kind = f["@_kind"] ?? SPEC.BOXED.FUNCTION.kind.default;
    switch (kind) {
      case "FEEL": {
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Feel,
          expression: dmnToBee(widthsById, f),
        };
      }
      case "Java": {
        // Special case, defined by the spec, where the implementation is a context expression with two fields.
        const c = f.context!;
        const clazz = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === SPEC.BOXED.FUNCTION.JAVA.classFieldName
        );
        const method = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === SPEC.BOXED.FUNCTION.JAVA.methodSignatureFieldName
        );

        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Java,
          className: clazz?.literalExpression?.text,
          classFieldId: clazz?.literalExpression?.["@_id"],
          methodName: method?.literalExpression?.text,
          methodFieldId: method?.literalExpression?.["@_id"],
          // `clazz` and `method` would have the exact same width, as they're always in sync, so it doens't matter which one we use.
          classAndMethodNamesWidth: widthsById.get(clazz?.literalExpression?.["@_id"] ?? "")?.[0],
        };
      }
      case "PMML": {
        // Special case, defined by the spec, where the implementation is a context expression with two fields.
        const c = f.context!;
        const document = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === SPEC.BOXED.FUNCTION.PMML.documentFieldName
        );
        const model = c.contextEntry?.find(
          ({ variable }) => variable?.["@_name"] === SPEC.BOXED.FUNCTION.PMML.modelFieldName
        );
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Pmml,
          document: document?.literalExpression?.text,
          documentFieldId: document?.literalExpression?.["@_id"],
          model: model?.literalExpression?.text,
          modelFieldId: model?.literalExpression?.["@_id"],
        };
      }
      default:
        throw new Error(`Unknown function expression kind '${f["@_kind"]}'`);
    }
  } else if (dmnExpr.list) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.List,
    // };
  } else {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.Undefined,
    // };
  }
}
