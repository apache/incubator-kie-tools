/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./FunctionExpression.css";
import * as React from "react";
import { PropsWithChildren, useCallback, useMemo } from "react";
import {
  BeeTableColumnsUpdateArgs,
  ContextExpressionDefinitionEntry,
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  executeIfExpressionDefinitionChanged,
  FeelFunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  FunctionExpressionDefinition,
  generateUuid,
  JavaFunctionExpressionDefinition,
  LiteralExpressionDefinition,
  ExpressionDefinitionLogicType,
  PmmlFunctionExpressionDefinition,
  PmmlLiteralExpressionDefinition,
  resetEntry,
  BeeTableRowsUpdateArgs,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  ROWGENERICTYPE,
  ExpressionDefinition,
} from "../../api";
import { BeeTable } from "../BeeTable";
import * as ReactTable from "react-table";
import { ContextEntryExpressionCell } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import * as _ from "lodash";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { EditParameters } from "./EditParameters";
import { hashfy } from "../Resizer";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

export const FunctionExpression: React.FunctionComponent<FunctionExpressionDefinition> = (
  functionExpression: PropsWithChildren<FunctionExpressionDefinition>
) => {
  const FIRST_ENTRY_ID = "0";
  const SECOND_ENTRY_ID = "1";
  const { i18n } = useBoxedExpressionEditorI18n();
  const { editorRef, setSupervisorHash, pmmlParams, beeGwtService, decisionNodeId } = useBoxedExpressionEditor();
  const pmmlDocument = useMemo(
    () => (functionExpression as PmmlFunctionExpressionDefinition).document,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionExpressionDefinition).document]
  );
  const pmmlDocumentFieldId = useMemo(
    () => (functionExpression as PmmlFunctionExpressionDefinition).documentFieldId ?? generateUuid(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionExpressionDefinition).documentFieldId]
  );
  const pmmlModel = useMemo(
    () => (functionExpression as PmmlFunctionExpressionDefinition).model,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionExpressionDefinition).model]
  );
  const pmmlModelFieldId = useMemo(
    () => (functionExpression as PmmlFunctionExpressionDefinition).modelFieldId ?? generateUuid(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionExpressionDefinition).modelFieldId]
  );
  const javaClassName = useMemo(
    () => (functionExpression as JavaFunctionExpressionDefinition).className,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionExpressionDefinition).className]
  );
  const javaClassFieldId = useMemo(
    () => (functionExpression as JavaFunctionExpressionDefinition).classFieldId ?? generateUuid(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionExpressionDefinition).classFieldId]
  );
  const javaMethodName = useMemo(
    () => (functionExpression as JavaFunctionExpressionDefinition).methodName,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionExpressionDefinition).methodName]
  );
  const javaMethodFieldId = useMemo(
    () => (functionExpression as JavaFunctionExpressionDefinition).methodFieldId ?? generateUuid(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionExpressionDefinition).methodFieldId]
  );
  const feelExpression = useMemo(
    () => (functionExpression as FeelFunctionExpressionDefinition).expression,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as FeelFunctionExpressionDefinition).expression]
  );

  const extractContextEntriesFromJavaProps = useMemo(() => {
    return [
      {
        entryInfo: { id: FIRST_ENTRY_ID, name: i18n.class, dataType: DmnBuiltInDataType.String },
        entryExpression: {
          id: javaClassFieldId,
          noClearAction: true,
          logicType: ExpressionDefinitionLogicType.LiteralExpression,
          content: javaClassName ?? "",
        } as LiteralExpressionDefinition,
      },
      {
        entryInfo: { id: SECOND_ENTRY_ID, name: i18n.methodSignature, dataType: DmnBuiltInDataType.String },
        entryExpression: {
          id: javaMethodFieldId,
          noClearAction: true,
          logicType: ExpressionDefinitionLogicType.LiteralExpression,
          content: javaMethodName ?? "",
        } as LiteralExpressionDefinition,
      },
    ];
  }, [i18n, javaClassName, javaClassFieldId, javaMethodName, javaMethodFieldId]);

  const extractContextEntriesFromPmmlProps = useMemo(() => {
    return [
      {
        entryInfo: { id: FIRST_ENTRY_ID, name: i18n.document, dataType: DmnBuiltInDataType.String },
        entryExpression: {
          id: pmmlDocumentFieldId,
          noClearAction: true,
          logicType: ExpressionDefinitionLogicType.PmmlLiteralExpression,
          testId: "pmml-selector-document",
          noOptionsLabel: i18n.pmml.firstSelection,
          getOptions: () => _.map(pmmlParams, "document"),
          selected: pmmlDocument ?? "",
        } as PmmlLiteralExpressionDefinition,
      },
      {
        entryInfo: { id: SECOND_ENTRY_ID, name: i18n.model, dataType: DmnBuiltInDataType.String },
        entryExpression: {
          id: pmmlModelFieldId,
          noClearAction: true,
          logicType: ExpressionDefinitionLogicType.PmmlLiteralExpression,
          noOptionsLabel: i18n.pmml.secondSelection,
          testId: "pmml-selector-model",
          getOptions: () =>
            _.map(_.find(pmmlParams, (param) => param.document === pmmlDocument)?.modelsFromDocument, "model"),
          selected: pmmlModel ?? "",
        } as PmmlLiteralExpressionDefinition,
      },
    ];
  }, [
    i18n.document,
    i18n.model,
    i18n.pmml.firstSelection,
    i18n.pmml.secondSelection,
    pmmlParams,
    pmmlDocument,
    pmmlDocumentFieldId,
    pmmlModel,
    pmmlModelFieldId,
  ]);

  const extractParametersFromPmmlProps = useMemo(() => {
    return (
      _.find(_.find(pmmlParams, { document: pmmlDocument ?? "" })?.modelsFromDocument, {
        model: pmmlModel ?? "",
      })?.parametersFromModel || []
    );
  }, [pmmlParams, pmmlModel, pmmlDocument]);

  const rows = useCallback(
    (functionKind: FunctionExpressionDefinitionKind) => {
      switch (functionKind) {
        case FunctionExpressionDefinitionKind.Java: {
          return {
            id: FIRST_ENTRY_ID,
            entryExpression: {
              logicType: ExpressionDefinitionLogicType.Context,
              noClearAction: true,
              renderResult: false,
              noHandlerMenu: true,
              contextEntries: extractContextEntriesFromJavaProps,
            },
          } as ROWGENERICTYPE;
        }
        case FunctionExpressionDefinitionKind.Pmml: {
          return {
            id: FIRST_ENTRY_ID,
            entryExpression: {
              logicType: ExpressionDefinitionLogicType.Context,
              noClearAction: true,
              renderResult: false,
              noHandlerMenu: true,
              contextEntries: extractContextEntriesFromPmmlProps,
            },
          } as ROWGENERICTYPE;
        }
        case FunctionExpressionDefinitionKind.Feel:
        default: {
          return {
            id: FIRST_ENTRY_ID,
            entryExpression: feelExpression || { logicType: ExpressionDefinitionLogicType.LiteralExpression },
          } as ROWGENERICTYPE;
        }
      }
    },
    [extractContextEntriesFromJavaProps, extractContextEntriesFromPmmlProps, feelExpression]
  );

  const retrieveModelValue = useCallback(
    (documentValue: string, contextProps: ContextExpressionDefinition) =>
      documentValue === pmmlDocument
        ? _.includes(
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as PmmlLiteralExpressionDefinition)?.getOptions(),
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as PmmlLiteralExpressionDefinition)?.selected
          )
          ? (_.nth(contextProps.contextEntries, 1)?.entryExpression as PmmlLiteralExpressionDefinition)?.selected
          : ""
        : "",
    [pmmlDocument]
  );

  const extendDefinitionBasedOnFunctionKind = useCallback(
    (
      definition: Partial<FunctionExpressionDefinition>,
      updatedDefinition?: Partial<FunctionExpressionDefinition>,
      functionKind?: FunctionExpressionDefinitionKind
    ) => {
      const currentFunctionKind = functionKind ?? functionExpression.functionKind;
      switch (currentFunctionKind) {
        case FunctionExpressionDefinitionKind.Java: {
          const contextProps = rows(currentFunctionKind).entryExpression as ContextExpressionDefinition;
          const firstEntry = _.nth(contextProps.contextEntries, 0)?.entryExpression as LiteralExpressionDefinition;
          const secondEntry = _.nth(contextProps.contextEntries, 1)?.entryExpression as LiteralExpressionDefinition;
          const className = firstEntry?.content ?? "";
          const classFieldId = firstEntry?.id;
          const methodName = secondEntry?.content ?? "";
          const methodFieldId = secondEntry?.id;
          return _.extend(definition, { className, methodName, classFieldId, methodFieldId });
        }
        case FunctionExpressionDefinitionKind.Pmml: {
          const contextProps = rows(currentFunctionKind).entryExpression as ContextExpressionDefinition;
          const firstEntry = _.nth(contextProps.contextEntries, 0)?.entryExpression as PmmlLiteralExpressionDefinition;
          const secondEntry = _.nth(contextProps.contextEntries, 1)?.entryExpression as PmmlLiteralExpressionDefinition;
          const documentValue = firstEntry?.selected ?? "";
          const documentFieldId = firstEntry?.id;
          const modelValue = retrieveModelValue(documentValue, contextProps);
          const modelFieldId = secondEntry?.id;
          const modelHasChanged =
            ((definition as PmmlFunctionExpressionDefinition)?.model ?? "") !==
            ((updatedDefinition as PmmlFunctionExpressionDefinition)?.model ?? "");
          if (pmmlModel !== "") {
            const parametersFromPmmlProps = extractParametersFromPmmlProps;
            if (
              !_.isEmpty(parametersFromPmmlProps) &&
              parametersFromPmmlProps &&
              definition.formalParameters?.length === 0
            ) {
              definition.formalParameters = parametersFromPmmlProps;
            }
          } else if (modelHasChanged) {
            definition.formalParameters = [];
          }
          return _.extend(definition, { document: documentValue, model: modelValue, documentFieldId, modelFieldId });
        }
        case FunctionExpressionDefinitionKind.Feel:
        default: {
          return _.extend(definition, {
            expression: rows(currentFunctionKind).entryExpression as ExpressionDefinition,
          });
        }
      }
    },
    [retrieveModelValue, rows, functionExpression.functionKind, pmmlModel, extractParametersFromPmmlProps]
  );

  const spreadFunctionExpressionDefinition = useCallback(
    (updatedFunctionExpression?: Partial<FunctionExpressionDefinition>) => {
      const extendedDefinition = extendDefinitionBasedOnFunctionKind(
        {
          id: functionExpression.id,
          logicType: functionExpression.logicType,
          name: functionExpression.name ?? DEFAULT_FIRST_PARAM_NAME,
          dataType: functionExpression.dataType ?? DmnBuiltInDataType.Undefined,
          functionKind: functionExpression.functionKind ?? FunctionExpressionDefinitionKind.Feel,
          parametersWidth: functionExpression.parametersWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
          formalParameters: functionExpression.formalParameters,
        } as Partial<FunctionExpressionDefinition>,
        updatedFunctionExpression,
        updatedFunctionExpression?.functionKind
      );

      const updatedDefinition = {
        ...extendedDefinition,
        ...updatedFunctionExpression,
      } as FunctionExpressionDefinition;

      if (functionExpression.isHeadless) {
        const headlessDefinition = _.omit(updatedDefinition, ["name", "dataType", "isHeadless"]);
        executeIfExpressionDefinitionChanged(
          functionExpression,
          headlessDefinition,
          () => {
            functionExpression.onUpdatingRecursiveExpression?.(headlessDefinition);
          },
          [
            "functionKind",
            "formalParameters",
            "parametersWidth",
            "className",
            "methodName",
            "document",
            "model",
            "expression",
          ]
        );
      } else {
        executeIfExpressionDefinitionChanged(
          functionExpression,
          updatedDefinition,
          () => {
            setSupervisorHash(hashfy(updatedDefinition));
            beeGwtService?.broadcastFunctionExpressionDefinition?.(updatedDefinition as FunctionExpressionDefinition);
          },
          [
            "name",
            "dataType",
            "functionKind",
            "formalParameters",
            "parametersWidth",
            "className",
            "methodName",
            "document",
            "model",
            "expression",
          ]
        );
      }
    },
    [beeGwtService, extendDefinitionBasedOnFunctionKind, setSupervisorHash, functionExpression]
  );

  const editParametersPopoverAppendTo = useCallback(() => {
    return () => editorRef.current!;
  }, [editorRef]);

  const setParameters = useCallback(
    (newParameter) => {
      spreadFunctionExpressionDefinition({ formalParameters: newParameter });
    },
    [spreadFunctionExpressionDefinition]
  );

  const headerCellElement = useMemo(
    () => (
      <PopoverMenu
        title={i18n.editParameters}
        appendTo={editParametersPopoverAppendTo()}
        className="parameters-editor-popover"
        minWidth="400px"
        body={<EditParameters parameters={functionExpression.formalParameters ?? []} setParameters={setParameters} />}
      >
        <div
          className={`parameters-list ${
            _.isEmpty(functionExpression.formalParameters ?? []) ? "empty-parameters" : ""
          }`}
        >
          <p className="pf-u-text-truncate">
            {_.isEmpty(functionExpression.formalParameters ?? [])
              ? i18n.editParameters
              : `(${_.join(
                  _.map(functionExpression.formalParameters ?? [], (parameter) => parameter.name),
                  ", "
                )})`}
          </p>
        </div>
      </PopoverMenu>
    ),
    [editParametersPopoverAppendTo, i18n.editParameters, functionExpression.formalParameters, setParameters]
  );

  const beeTableColumns = useMemo<ReactTable.ColumnInstance<ROWGENERICTYPE>[]>(() => {
    return [
      {
        label: functionExpression.name ?? DEFAULT_FIRST_PARAM_NAME,
        accessor: decisionNodeId,
        dataType: functionExpression.dataType ?? DmnBuiltInDataType.Undefined,
        disableHandlerOnHeader: true,
        columns: [
          {
            headerCellElement,
            accessor: "parameters",
            disableHandlerOnHeader: true,
            width: functionExpression.parametersWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
            minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
          },
        ],
      },
    ] as ReactTable.ColumnInstance<ROWGENERICTYPE>[];
  }, [
    decisionNodeId,
    functionExpression.name,
    functionExpression.dataType,
    headerCellElement,
    functionExpression.parametersWidth,
  ]);

  const getHeaderVisibility = useMemo(() => {
    return functionExpression.isHeadless ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.Full;
  }, [functionExpression.isHeadless]);

  const onFunctionKindSelect = useCallback(
    (itemId: string) => {
      const kind = itemId as FunctionExpressionDefinitionKind;
      spreadFunctionExpressionDefinition({
        functionKind: kind,
        formalParameters: [],
      } as Partial<FunctionExpressionDefinition>);
    },
    [spreadFunctionExpressionDefinition]
  );

  const onColumnsUpdate = useCallback(
    ({ columns: [column] }: BeeTableColumnsUpdateArgs<ReactTable.ColumnInstance<ROWGENERICTYPE>>) => {
      functionExpression.onExpressionHeaderUpdated?.({ name: column.label, dataType: column.dataType });
      spreadFunctionExpressionDefinition({
        name: column.label,
        dataType: column.dataType,
        parametersWidth: column.width,
      });
    },
    [functionExpression, spreadFunctionExpressionDefinition]
  );

  const resetRowCustomFunction = useCallback(
    (row) => {
      spreadFunctionExpressionDefinition({
        functionKind: FunctionExpressionDefinitionKind.Feel,
        formalParameters: [],
      } as Partial<FunctionExpressionDefinition>);
      return resetEntry(row);
    },
    [spreadFunctionExpressionDefinition]
  );

  const operationHandlerConfig = useMemo(() => {
    return [
      {
        group: _.upperCase(i18n.function),
        items: [{ name: i18n.rowOperations.clear, type: BeeTableOperation.RowClear }],
      },
    ];
  }, [i18n]);

  const defaultCellByColumnId = useMemo(
    () => ({
      parameters: ContextEntryExpressionCell,
    }),
    []
  );

  const onRowsUpdate = useCallback(
    ({ rows: [row] }: BeeTableRowsUpdateArgs<ContextExpressionDefinitionEntry<ContextExpressionDefinition>>) => {
      switch (functionExpression.functionKind) {
        case FunctionExpressionDefinitionKind.Feel:
          spreadFunctionExpressionDefinition({ expression: row.entryExpression });
          break;
        case FunctionExpressionDefinitionKind.Java:
          if (row.entryExpression.contextEntries) {
            spreadFunctionExpressionDefinition({
              className:
                (row.entryExpression.contextEntries[0]?.entryExpression as LiteralExpressionDefinition).content ?? "",
              methodName:
                (row.entryExpression.contextEntries[1]?.entryExpression as LiteralExpressionDefinition).content ?? "",
            });
          }
          break;
        case FunctionExpressionDefinitionKind.Pmml:
          if (row.entryExpression.contextEntries) {
            spreadFunctionExpressionDefinition({
              document:
                (row.entryExpression.contextEntries[0]?.entryExpression as PmmlLiteralExpressionDefinition).selected ??
                "",
              model:
                (row.entryExpression.contextEntries[1]?.entryExpression as PmmlLiteralExpressionDefinition).selected ??
                "",
            });
          }
          break;
      }
    },
    [spreadFunctionExpressionDefinition, functionExpression.functionKind]
  );

  const beeTableRows = useMemo(() => [rows(functionExpression.functionKind)], [rows, functionExpression.functionKind]);

  const controllerCell = useMemo(
    () => (
      <FunctionKindSelector
        selectedFunctionKind={functionExpression.functionKind ?? FunctionExpressionDefinitionKind.Feel}
        onFunctionKindSelect={onFunctionKindSelect}
      />
    ),
    [functionExpression.functionKind, onFunctionKindSelect]
  );

  return (
    <div className={`function-expression ${functionExpression.id}`}>
      <BeeTable
        operationHandlerConfig={operationHandlerConfig}
        onColumnsUpdate={onColumnsUpdate}
        columns={beeTableColumns}
        rows={beeTableRows}
        onRowsUpdate={onRowsUpdate}
        headerLevels={1}
        headerVisibility={getHeaderVisibility}
        controllerCell={controllerCell}
        defaultCellByColumnId={defaultCellByColumnId}
        resetRowCustomFunction={resetRowCustomFunction}
      />
    </div>
  );
};
