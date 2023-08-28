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
import { DmnEditorTab, useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { dmnToBee, getUndefinedExpressionDefinition } from "./dmnToBee";
import { getDefaultExpressionDefinitionByLogicType } from "./getDefaultExpressionDefinitionByLogicType";
import { useDataTypes } from "../dataTypes/Hooks";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStatePrimary,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { ErrorCircleOIcon } from "@patternfly/react-icons/dist/js/icons/error-circle-o-icon";
import "@kie-tools/dmn-marshaller"; // This is here because of the KIE Extension for DMN.

export function BoxedExpression({ container }: { container: React.RefObject<HTMLElement> }) {
  const { dispatch, dmn, boxedExpressionEditor: boxedExpression } = useDmnEditorStore();

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
    if (!boxedExpression.id) {
      return undefined;
    }

    const drgElementIndex = (dmn.model.definitions.drgElement ?? []).findIndex((e) => e["@_id"] === boxedExpression.id);
    if (drgElementIndex < 0) {
      return undefined;
    }

    const drgElement = dmn.model.definitions.drgElement![drgElementIndex];
    if (!(drgElement.__$$element === "decision" || drgElement.__$$element === "businessKnowledgeModel")) {
      return undefined;
    }

    return {
      beeExpression: drgElementToBoxedExpression(widthsById, drgElement),
      drgElementIndex,
      drgElementType: drgElement.__$$element,
    };
  }, [boxedExpression.id, dmn.model.definitions.drgElement, widthsById]);

  const [lastValidExpression, setLastValidExpression] = useState<typeof expression>(undefined);
  useEffect(() => {
    if (expression) {
      setLastValidExpression(expression);
    }
  }, [expression, setLastValidExpression]);

  const setExpression: React.Dispatch<React.SetStateAction<ExpressionDefinition>> = useCallback(
    (expressionAction) => {
      dmnEditorStoreApi.setState((state) => {
        updateExpression({
          definitions: state.dmn.model.definitions,
          expression:
            typeof expressionAction === "function"
              ? expressionAction(expression?.beeExpression ?? getUndefinedExpressionDefinition())
              : expressionAction,
          index: expression?.drgElementIndex ?? 0,
        });
      });
    },
    [dmnEditorStoreApi, expression]
  );

  const isResetSupportedOnRootExpression = useMemo(() => {
    return expression?.drgElementType === "decision"; // BKMs are ALWAYS functions, and can't be reset.
  }, [expression?.drgElementType]);

  ////

  const { all: dataTypes } = useDataTypes(dmn.model.definitions);

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
        {!expression && (
          <>
            <EmptyState>
              <EmptyStateIcon icon={ErrorCircleOIcon} />
              <Title size="lg" headingLevel="h4">
                {`Expression with ID '${boxedExpression.id}' doesn't exist.`}
              </Title>
              <EmptyStateBody>
                This happens when the DMN file is modified externally while the expression was open here.
              </EmptyStateBody>
              <EmptyStatePrimary>
                <Button variant="link" onClick={dispatch.boxedExpression.close}>
                  Go back to the Diagram
                </Button>
              </EmptyStatePrimary>
            </EmptyState>
            {/* TODO: Tiago --> Show `lastValidExpression`in Black & White, with opacity reduced, and the empty state on top of it. This is a nice effect. */}
          </>
        )}
        {expression && (
          <>
            <BoxedExpressionEditor
              beeGwtService={beeGwtService}
              pmmlParams={pmmlParams}
              isResetSupportedOnRootExpression={isResetSupportedOnRootExpression}
              decisionNodeId={boxedExpression.id!}
              expressionDefinition={expression.beeExpression}
              setExpressionDefinition={setExpression}
              dataTypes={dataTypes}
              scrollableParentRef={container}
            />
          </>
        )}
      </>
    </>
  );
}

function drgElementToBoxedExpression(
  widthsById: Map<string, number[]>,
  drgElement:
    | (DMN15__tDecision & { __$$element: "decision" })
    | (DMN15__tBusinessKnowledgeModel & { __$$element: "businessKnowledgeModel" })
): ExpressionDefinition {
  if (drgElement.__$$element === "businessKnowledgeModel") {
    return {
      ...dmnToBee(widthsById, {
        expression: {
          __$$element: "functionDefinition",
          ...drgElement.encapsulatedLogic,
        },
      }),
      dataType: (drgElement.variable?.["@_typeRef"] ??
        drgElement.encapsulatedLogic?.["@_typeRef"] ??
        DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      name: drgElement["@_name"],
    };
  } else if (drgElement.__$$element === "decision") {
    return {
      ...dmnToBee(widthsById, drgElement),
      dataType: (drgElement.variable?.["@_typeRef"] ??
        drgElement.expression?.["@_typeRef"] ??
        DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
      name: drgElement["@_name"],
    };
  } else {
    throw new Error(`Unknown __$$element of drgElement that has an expression '${(drgElement as any).__$$element}'.`);
  }
}
