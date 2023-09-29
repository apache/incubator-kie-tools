import {
  ContextExpressionDefinition,
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  InvocationExpressionDefinition,
  ListExpressionDefinition,
  LiteralExpressionDefinition,
  RelationExpressionDefinition,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import {
  LITERAL_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
} from "@kie-tools/boxed-expression-component/dist/resizing/WidthConstants";
import {
  DECISION_TABLE_INPUT_DEFAULT_VALUE,
  DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
} from "@kie-tools/boxed-expression-component/dist/expressions/DecisionTableExpression";
import {
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
  INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE,
} from "@kie-tools/boxed-expression-component/dist/expressions/InvocationExpression";
import { RELATION_EXPRESSION_DEFAULT_VALUE } from "@kie-tools/boxed-expression-component/dist/expressions/RelationExpression";
import { DataTypeIndex } from "../dataTypes/DataTypes";
import { isStruct } from "../dataTypes/DataTypeSpec";

export function getDefaultExpressionDefinitionByLogicType({
  logicType,
  typeRef,
  allTopLevelDataTypesByFeelName,
  getInputs,
  getDefaultColumnWidth,
}: {
  logicType: ExpressionDefinitionLogicType;
  typeRef: string;
  allTopLevelDataTypesByFeelName: DataTypeIndex;
  getInputs?: () => { name: string; typeRef: string | undefined }[] | undefined;
  getDefaultColumnWidth?: (args: { name: string; typeRef: string | undefined }) => number | undefined;
}): ExpressionDefinition {
  const dataType = allTopLevelDataTypesByFeelName.get(typeRef);

  if (logicType === ExpressionDefinitionLogicType.Literal) {
    const literalExpression: LiteralExpressionDefinition = {
      id: generateUuid(),
      dataType: typeRef as DmnBuiltInDataType,
      logicType,
      width: LITERAL_EXPRESSION_MIN_WIDTH,
    };
    return literalExpression;
  }
  //
  else if (logicType === ExpressionDefinitionLogicType.Function) {
    const functionExpression: FunctionExpressionDefinition = {
      id: generateUuid(),
      dataType: typeRef as DmnBuiltInDataType,
      logicType,
      functionKind: FunctionExpressionDefinitionKind.Feel,
      formalParameters: [],
      expression: {
        id: generateUuid(),
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
      },
    };
    return functionExpression;
  }
  //
  else if (logicType === ExpressionDefinitionLogicType.Context) {
    let maxWidthBasedOnEntryNames = CONTEXT_ENTRY_INFO_MIN_WIDTH;

    const contextEntries: ContextExpressionDefinition["contextEntries"] =
      !dataType || !isStruct(dataType.itemDefinition)
        ? [
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
          ]
        : (dataType.itemDefinition.itemComponent ?? []).map((ic) => {
            const name = ic["@_name"];
            const typeRef = isStruct(ic)
              ? DmnBuiltInDataType.Any
              : (ic.typeRef as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined;
            maxWidthBasedOnEntryNames = Math.max(
              maxWidthBasedOnEntryNames,
              getDefaultColumnWidth?.({ name, typeRef }) ?? CONTEXT_ENTRY_INFO_MIN_WIDTH
            );
            return {
              entryInfo: {
                id: generateUuid(),
                name,
                dataType: typeRef,
              },
              entryExpression: {
                id: generateUuid(),
                name,
                dataType: typeRef,
                logicType: ExpressionDefinitionLogicType.Undefined,
              },
            };
          });

    const contextExpression: ContextExpressionDefinition = {
      id: generateUuid(),
      dataType: typeRef as DmnBuiltInDataType,
      logicType,
      entryInfoWidth: maxWidthBasedOnEntryNames,
      result: {
        logicType: ExpressionDefinitionLogicType.Undefined,
        dataType: DmnBuiltInDataType.Undefined,
        id: generateUuid(),
      },
      contextEntries,
    };
    return contextExpression;
  } else if (logicType === ExpressionDefinitionLogicType.List) {
    const listExpression: ListExpressionDefinition = {
      id: generateUuid(),
      dataType: typeRef as DmnBuiltInDataType,
      logicType,
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
      id: generateUuid(),
      dataType: typeRef as DmnBuiltInDataType,
      logicType,
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
        name: "FUNCTION NAME",
      },
    };
    return invocationExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Relation) {
    const relationExpression: RelationExpressionDefinition = {
      id: generateUuid(),
      dataType: typeRef as DmnBuiltInDataType,
      logicType,
      rows: [
        {
          id: generateUuid(),
          cells: [{ id: generateUuid(), content: RELATION_EXPRESSION_DEFAULT_VALUE }],
        },
      ],
      columns:
        !dataType || !isStruct(dataType.itemDefinition)
          ? [
              {
                id: generateUuid(),
                name: dataType?.itemDefinition["@_name"] ?? "column-1",
                dataType: (dataType?.feelName as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined,
                width: RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
              },
            ]
          : (dataType.itemDefinition.itemComponent ?? []).map((ic) => {
              const name = ic["@_name"];
              const typeRef = isStruct(ic)
                ? DmnBuiltInDataType.Any
                : (ic.typeRef as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined;
              return {
                id: generateUuid(),
                name,
                dataType: typeRef,
                width: getDefaultColumnWidth?.({ name, typeRef }) ?? RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
              };
            }),
    };
    return relationExpression;
  } else if (logicType === ExpressionDefinitionLogicType.DecisionTable) {
    const singleOutputColumn = {
      name: dataType?.itemDefinition["@_name"] ?? "output-1",
      typeRef: (dataType?.feelName as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined,
    };
    const singleInputColumn = {
      name: "input-1",
      typeRef: DmnBuiltInDataType.Undefined,
    };
    const annotationColumn = {
      name: "annotation-1",
    };

    const input = getInputs?.()?.map((input) => {
      return {
        id: generateUuid(),
        idLiteralExpression: generateUuid(),
        name: input.name, // FIXME: Tiago --> This is actually a FEEL expression!
        dataType: (input.typeRef as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined,
        width: getDefaultColumnWidth?.(input) ?? DECISION_TABLE_INPUT_DEFAULT_WIDTH,
      };
    }) ?? [
      {
        id: generateUuid(),
        idLiteralExpression: generateUuid(),
        name: singleInputColumn.name,
        dataType: singleInputColumn.typeRef,
        width: getDefaultColumnWidth?.(singleInputColumn) ?? DECISION_TABLE_INPUT_DEFAULT_WIDTH,
      },
    ];

    const output =
      !dataType || !isStruct(dataType.itemDefinition)
        ? [
            {
              id: generateUuid(),
              name: singleOutputColumn.name,
              dataType: singleOutputColumn.typeRef,
              width: getDefaultColumnWidth?.(singleOutputColumn) ?? DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
            },
          ]
        : (dataType.itemDefinition.itemComponent ?? []).map((ic) => {
            const name = ic["@_name"];
            const typeRef = isStruct(ic)
              ? DmnBuiltInDataType.Any
              : (ic.typeRef as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined;
            return {
              id: generateUuid(),
              name,
              dataType: typeRef,
              width: getDefaultColumnWidth?.({ name, typeRef }) ?? DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
            };
          });

    const decisionTableExpression: DecisionTableExpressionDefinition = {
      id: generateUuid(),
      dataType: typeRef as DmnBuiltInDataType,
      logicType,
      hitPolicy: DecisionTableExpressionDefinitionHitPolicy.Unique,
      aggregation: DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
      input,
      output,
      annotations: [
        {
          name: annotationColumn.name,
          width:
            getDefaultColumnWidth?.({ name: annotationColumn.name, typeRef: undefined }) ??
            DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
        },
      ],
      rules: [
        {
          id: generateUuid(),
          inputEntries: input.map(() => ({ id: generateUuid(), content: DECISION_TABLE_INPUT_DEFAULT_VALUE })),
          outputEntries: output.map(() => ({ id: generateUuid(), content: DECISION_TABLE_OUTPUT_DEFAULT_VALUE })),
          annotationEntries: ["// Your annotations here"],
        },
      ],
    };
    return decisionTableExpression;
  } else {
    throw new Error(`No default expression available for ${logicType}`);
  }
}
