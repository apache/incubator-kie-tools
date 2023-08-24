import * as React from "react";
import { useState, useCallback, useEffect } from "react";
import { useArgs } from "@storybook/preview-api";
import { BoxedExpressionEditor, BoxedExpressionEditorProps } from "../src/expressions";
import {
  BeeGwtService,
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
  LiteralExpressionDefinition,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  ListExpressionDefinition,
  InvocationExpressionDefinition,
  RelationExpressionDefinition,
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionHitPolicy,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  ExpressionDefinitionBase,
} from "../src/api";
import {
  LITERAL_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  LITERAL_EXPRESSION_EXTRA_WIDTH,
} from "../src/resizing/WidthConstants";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "../src/expressions/DecisionTableExpression";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
} from "../src/expressions/InvocationExpression";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "../src/expressions/RelationExpression";

function getDefaultExpressionDefinitionByLogicType(
  logicType: ExpressionDefinitionLogicType,
  prev: ExpressionDefinitionBase,
  containerWidth: number
): ExpressionDefinition {
  if (logicType === ExpressionDefinitionLogicType.Literal) {
    const literalExpression: LiteralExpressionDefinition = {
      ...prev,
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Literal,
      width: Math.max(LITERAL_EXPRESSION_MIN_WIDTH, containerWidth - LITERAL_EXPRESSION_EXTRA_WIDTH),
    };
    return literalExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Function) {
    const functionExpression: FunctionExpressionDefinition = {
      ...prev,
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Function,
      functionKind: FunctionExpressionDefinitionKind.Feel,
      formalParameters: [],
      expression: {
        id: generateUuid(),
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
      },
    };
    return functionExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Context) {
    const contextExpression: ContextExpressionDefinition = {
      ...prev,
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Context,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      result: {
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
        id: generateUuid(),
      },
      contextEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
          },
          entryExpression: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Undefined,
          },
        },
      ],
    };
    return contextExpression;
  } else if (logicType === ExpressionDefinitionLogicType.List) {
    const listExpression: ListExpressionDefinition = {
      ...prev,
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.List,
      items: [
        {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
        },
      ],
    };
    return listExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Invocation) {
    const invocationExpression: InvocationExpressionDefinition = {
      ...prev,
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Invocation,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      bindingEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
          },
          entryExpression: {
            id: generateUuid(),
            name: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
            dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
            logicType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
          },
        },
      ],
      invokedFunction: {
        id: generateUuid(),
        name: "FUNCTION",
      },
    };
    return invocationExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Relation) {
    const relationExpression: RelationExpressionDefinition = {
      ...prev,
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Relation,
      columns: [
        {
          id: generateUuid(),
          name: "column-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: 100,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: [
            {
              id: generateUuid(),
              content: RELATION_EXPRESSION_DEFAULT_VALUE,
            },
          ],
        },
      ],
    };
    return relationExpression;
  } else if (logicType === ExpressionDefinitionLogicType.DecisionTable) {
    const decisionTableExpression: DecisionTableExpressionDefinition = {
      ...prev,
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.DecisionTable,
      hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
      aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
      input: [
        {
          id: generateUuid(),
          idLiteralExpression: generateUuid(),
          name: "input-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: DECISION_TABLE_INPUT_DEFAULT_WIDTH,
        },
      ],
      output: [
        {
          id: generateUuid(),
          name: "output-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
        },
      ],
      annotations: [
        {
          name: "annotation-1",
          width: DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
        },
      ],
      rules: [
        {
          id: generateUuid(),
          inputEntries: [{ id: generateUuid(), content: DECISION_TABLE_INPUT_DEFAULT_VALUE }],
          outputEntries: [{ id: generateUuid(), content: DECISION_TABLE_OUTPUT_DEFAULT_VALUE }],
          annotationEntries: ["// Your annotations here"],
        },
      ],
    };
    return decisionTableExpression;
  } else {
    throw new Error(`No default expression available for ${logicType}`);
  }
}

export const pmmlParams = [
  {
    document: "document",
    modelsFromDocument: [
      { model: "model", parametersFromModel: [{ id: "p1", name: "p-1", dataType: DmnBuiltInDataType.Number }] },
    ],
  },
  {
    document: "mining pmml",
    modelsFromDocument: [
      {
        model: "MiningModelSum",
        parametersFromModel: [{ id: "i1", name: "input1", dataType: DmnBuiltInDataType.Any }],
      },
    ],
  },
  {
    document: "regression pmml",
    modelsFromDocument: [
      {
        model: "RegressionLinear",
        parametersFromModel: [{ id: "i1", name: "i1", dataType: DmnBuiltInDataType.Number }],
      },
    ],
  },
];

export const dataTypes = [
  { typeRef: "Undefined", name: "<Undefined>", isCustom: false },
  { typeRef: "Any", name: "Any", isCustom: false },
  { typeRef: "Boolean", name: "boolean", isCustom: false },
  { typeRef: "Context", name: "context", isCustom: false },
  { typeRef: "Date", name: "date", isCustom: false },
  { typeRef: "DateTime", name: "date and time", isCustom: false },
  { typeRef: "DateTimeDuration", name: "days and time duration", isCustom: false },
  { typeRef: "Number", name: "number", isCustom: false },
  { typeRef: "String", name: "string", isCustom: false },
  { typeRef: "Time", name: "time", isCustom: false },
  { typeRef: "YearsMonthsDuration", name: "years and months duration", isCustom: false },
];

export const beeGwtService: BeeGwtService = {
  getDefaultExpressionDefinition(logicType: string, dataType: string): ExpressionDefinition {
    return getDefaultExpressionDefinitionByLogicType(
      logicType as ExpressionDefinitionLogicType,
      { dataType: dataType } as ExpressionDefinition,
      0
    );
  },
  openDataTypePage(): void {},
  selectObject(): void {},
};

export function BoxedExpressionEditorWrapper(props?: Partial<BoxedExpressionEditorProps>) {
  const emptyRef = React.useRef<HTMLDivElement>(null);
  const [args, updateArgs] = useArgs<BoxedExpressionEditorProps>();
  const [expressionDefinition, setExpressionDefinition] = useState<ExpressionDefinition>(args.expressionDefinition);

  useEffect(() => {
    setExpressionDefinition(args.expressionDefinition);
  }, [args]);

  const setExpressionCallback: React.Dispatch<React.SetStateAction<ExpressionDefinition>> = useCallback(
    (newExpression) => {
      setExpressionDefinition((prev) => {
        if (typeof newExpression === "function") {
          const expression = newExpression(prev);
          updateArgs({ ...args, expressionDefinition: expression });
          return expression;
        }
        updateArgs({ ...args, expressionDefinition: newExpression });
        return newExpression;
      });
    },
    [args, updateArgs]
  );

  return (
    <div ref={emptyRef}>
      <BoxedExpressionEditor
        decisionNodeId={props?.decisionNodeId ?? args.decisionNodeId}
        expressionDefinition={props?.expressionDefinition ?? expressionDefinition}
        setExpressionDefinition={props?.setExpressionDefinition ?? setExpressionCallback}
        dataTypes={props?.dataTypes ?? args.dataTypes}
        scrollableParentRef={props?.scrollableParentRef ?? emptyRef}
        beeGwtService={props?.beeGwtService ?? args.beeGwtService}
        pmmlParams={props?.pmmlParams ?? args.pmmlParams}
        isResetSupportedOnRootExpression={
          props?.isResetSupportedOnRootExpression ?? args.isResetSupportedOnRootExpression
        }
      />
    </div>
  );
}
