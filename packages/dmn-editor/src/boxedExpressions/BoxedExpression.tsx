import {
  BeeGwtService,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  PmmlParam,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import { BoxedExpressionEditor } from "@kie-tools/boxed-expression-component/dist/expressions";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { updateExpression } from "../mutations/updateExpression";
import {
  DmnEditorTab,
  DrgElementWithExpression,
  TypeOfDrgElementWithExpression,
  useDmnEditorStore,
  useDmnEditorStoreApi,
} from "../store/Store";
import { dmnToBee, getUndefinedExpressionDefinition } from "./dmnToBee";
import { getDefaultExpressionDefinitionByLogicType } from "./getDefaultExpressionDefinitionByLogicType";
import "@kie-tools/dmn-marshaller";

export function BoxedExpression({ container }: { container: React.RefObject<HTMLElement> }) {
  const { dispatch, dmn, boxedExpression } = useDmnEditorStore();

  const dmnEditorStoreApi = useDmnEditorStoreApi();

  const widthsById = useMemo(() => {
    return (
      dmn.model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]["di:extension"]?.[
        "kie:ComponentsWidthsExtension"
      ]?.["kie:ComponentWidths"] ?? []
    ).reduce((acc, c) => {
      if (c["@_dmnElementRef"] === undefined) {
        return acc;
      } else {
        return acc.set(c["@_dmnElementRef"], c["kie:width"] ?? []);
      }
    }, new Map<string, number[]>());
  }, [dmn.model.definitions]);

  const expression = useMemo(() => {
    return boxedExpression.drgElement
      ? drgElementToBoxedExpression(widthsById, boxedExpression.drgElement)
      : getUndefinedExpressionDefinition();
  }, [boxedExpression.drgElement, widthsById]);

  const setExpression: React.Dispatch<React.SetStateAction<ExpressionDefinition>> = useCallback(
    (expressionAction) => {
      dmnEditorStoreApi.setState((state) => {
        updateExpression({
          definitions: state.dmn.model.definitions,
          expression: typeof expressionAction === "function" ? expressionAction(expression) : expressionAction,
          index: boxedExpression.drgElement?.index ?? 0,
        });
      });
    },
    [boxedExpression.drgElement?.index, dmnEditorStoreApi, expression]
  );

  //Necessary, since `state.boxedExpression.drgElement!.content` is a copy of the original element.
  useEffect(() => {
    dmnEditorStoreApi.setState((state) => {
      state.boxedExpression.drgElement!.content =
        dmn.model.definitions.drgElement![boxedExpression.drgElement?.index ?? 0];
    });
  }, [boxedExpression.drgElement?.index, dmn.model.definitions.drgElement, dmnEditorStoreApi]);

  const isResetSupportedOnRootExpression = useMemo(() => {
    return boxedExpression.drgElement?.type === TypeOfDrgElementWithExpression.DECISION;
  }, [boxedExpression.drgElement?.type]);

  ////

  // FIXME: Tiago -->  This is duplicated.
  const dataTypes = useMemo(
    () =>
      (dmn.model.definitions.itemDefinition ?? []).map((item) => ({
        isCustom: true,
        typeRef: item.typeRef!,
        name: item["@_name"]!,
      })),
    [dmn.model.definitions.itemDefinition]
  );

  const pmmlParams = useMemo<PmmlParam[]>(() => [], []);

  const beeGwtService = useMemo<BeeGwtService>(() => {
    return {
      getDefaultExpressionDefinition(logicType: string, dataType: string): ExpressionDefinition {
        return getDefaultExpressionDefinitionByLogicType(
          logicType as ExpressionDefinitionLogicType,
          {
            id: generateUuid(),
            dataType: (dataType as DmnBuiltInDataType) || DmnBuiltInDataType.Undefined,
          },
          0
        );
      },
      selectObject(uuid) {
        dmnEditorStoreApi.setState((state) => {
          dispatch.diagram.setNodeStatus(state, uuid!, { selected: true });
        });
      },
      openDataTypePage() {
        dispatch.navigation.setTab(DmnEditorTab.DATA_TYPES);
      },
    };
  }, [dispatch.diagram, dispatch.navigation, dmnEditorStoreApi]);

  ////

  return (
    <>
      <Label
        isCompact={true}
        className={"kie-dmn-editor--boxed-expression-back"}
        onClick={dispatch.boxedExpression.close}
      >
        Back to Diagram
      </Label>
      <Divider inset={{ default: "insetMd" }} />
      <br />
      <>
        <BoxedExpressionEditor
          beeGwtService={beeGwtService}
          pmmlParams={pmmlParams}
          isResetSupportedOnRootExpression={isResetSupportedOnRootExpression}
          decisionNodeId={boxedExpression.drgElement!.content["@_id"]!}
          expressionDefinition={expression}
          setExpressionDefinition={setExpression}
          dataTypes={dataTypes}
          scrollableParentRef={container}
        />
      </>
    </>
  );
}

function drgElementToBoxedExpression(
  widthsById: Map<string, number[]>,
  drgElement: DrgElementWithExpression
): ExpressionDefinition {
  if (drgElement.type === TypeOfDrgElementWithExpression.BKM) {
    return {
      ...dmnToBee(widthsById, {
        expression: {
          __$$element: "functionDefinition",
          ...drgElement.content.encapsulatedLogic,
        },
      }),
      dataType: (drgElement.content.variable?.["@_typeRef"] ??
        drgElement.content.encapsulatedLogic?.["@_typeRef"] ??
        DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      name: drgElement.content["@_name"],
    };
  } else if (drgElement.type === TypeOfDrgElementWithExpression.DECISION) {
    return {
      ...dmnToBee(widthsById, drgElement.content),
      dataType: (drgElement.content.variable?.["@_typeRef"] ??
        drgElement.content.expression?.["@_typeRef"] ??
        DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      name: drgElement.content["@_name"],
    };
  } else {
    throw new Error(`Unknown type of drgElement that has an expression '${(drgElement as any).type}'.`);
  }
}
