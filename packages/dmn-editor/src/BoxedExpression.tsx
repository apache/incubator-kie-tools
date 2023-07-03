import * as React from "react";
import { DmnNodeWithExpression } from "./DmnEditor";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/dist/expressions";
import {
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import { useMemo } from "react";
import {
  DMN14__tDecision,
  DMN14__tDefinitions,
  DMN14__tFunctionDefinition,
  DMN14__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

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
  const expressionDefinition = useMemo<ExpressionDefinition>(
    () => (openNodeWithExpression ? dmnNodeToBoxedExpression(openNodeWithExpression) : newExpressionDefinition()),
    [openNodeWithExpression]
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

  return (
    <>
      <br />
      <Button
        isSmall={true}
        variant={ButtonVariant.tertiary}
        onClick={() => setOpenNodeWithExpression(undefined)}
      >{`Back`}</Button>
      <br />
      <br />
      <>
        <BoxedExpressionEditor
          decisionNodeId={openNodeWithExpression.content["@_id"]!}
          expressionDefinition={expressionDefinition}
          setExpressionDefinition={function (value: React.SetStateAction<ExpressionDefinition>): void {
            throw new Error("Function not implemented.");
          }}
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

function dmnNodeToBoxedExpression(dmnNode: DmnNodeWithExpression): ExpressionDefinition {
  if (dmnNode.type == "bkm") {
    return {
      ...exprToBee({ functionDefinition: dmnNode.content.encapsulatedLogic }),
      dataType: dmnNode.content.variable?.["@_typeRef"],
      name: dmnNode.content["@_name"],
    };
  } else if (dmnNode.type == "decision") {
    return {
      ...exprToBee(dmnNode.content),
      dataType: dmnNode.content.variable?.["@_typeRef"],
      name: dmnNode.content["@_name"],
    };
  } else {
    throw new Error(`Unknown type of node that has an expression '${(dmnNode as any).type}'.`);
  }
}

function exprToBee(expr: DMN14__tDecision | DMN14__tFunctionDefinition | undefined): any {
  if (!expr) {
    return newExpressionDefinition();
  } else if (expr.literalExpression) {
    const l = expr.literalExpression as DMN14__tLiteralExpression;
    return {
      id: l["@_id"],
      name: l["@_label"],
      logicType: ExpressionDefinitionLogicType.Literal,
      dataType: l["@_typeRef"] as DmnBuiltInDataType,
      content: l.text,
    };
  } else if (expr.decisionTable) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.DecisionTable,
    // };
  } else if (expr.relation) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.Relation,
    // };
  } else if (expr.context) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.Context,
    // };
  } else if (expr.invocation) {
    return newExpressionDefinition();
    // return {
    //   logicType: ExpressionDefinitionLogicType.Invocation,
    // };
  } else if (expr.functionDefinition) {
    const f = expr.functionDefinition;
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

    const kind = f["@_kind"] ?? "FEEL"; // FEEL is the default;
    switch (kind) {
      case "FEEL":
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Feel,
          expression: exprToBee(f),
        };
      case "Java":
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Java,
          className: "",
          methodName: "",
          classFieldId: "",
          methodFieldId: "",
        };
      case "PMML":
        return {
          ...basic,
          functionKind: FunctionExpressionDefinitionKind.Pmml,
          document: "",
          model: "",
          documentFieldId: "",
          modelFieldId: "",
        };
      default:
        throw new Error(`Unknown function expression kind '${f["@_kind"]}'`);
    }
  } else if (expr.list) {
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
