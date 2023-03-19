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
  ColumnsUpdateArgs,
  ContextEntryRecord,
  ContextProps,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  executeIfExpressionDefinitionChanged,
  ExpressionProps,
  FeelFunctionProps,
  FunctionKind,
  FunctionProps,
  generateUuid,
  JavaFunctionProps,
  LiteralExpressionProps,
  LogicType,
  PmmlFunctionProps,
  PMMLLiteralExpressionProps,
  resetEntry,
  RowsUpdateArgs,
  TableHeaderVisibility,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import * as _ from "lodash";
import { useBoxedExpression } from "../../context";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { EditParameters } from "./EditParameters";
import { hashfy } from "../Resizer";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

export const FunctionExpression: React.FunctionComponent<FunctionProps> = (
  functionExpression: PropsWithChildren<FunctionProps>
) => {
  const FIRST_ENTRY_ID = "0";
  const SECOND_ENTRY_ID = "1";
  const { i18n } = useBoxedExpressionEditorI18n();
  const { editorRef, setSupervisorHash, pmmlParams, boxedExpressionEditorGWTService, decisionNodeId } =
    useBoxedExpression();
  const pmmlDocument = useMemo(
    () => (functionExpression as PmmlFunctionProps).document,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionProps).document]
  );
  const pmmlDocumentFieldId = useMemo(
    () => (functionExpression as PmmlFunctionProps).documentFieldId ?? generateUuid(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionProps).documentFieldId]
  );
  const pmmlModel = useMemo(
    () => (functionExpression as PmmlFunctionProps).model,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionProps).model]
  );
  const pmmlModelFieldId = useMemo(
    () => (functionExpression as PmmlFunctionProps).modelFieldId ?? generateUuid(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionProps).modelFieldId]
  );
  const javaClassName = useMemo(
    () => (functionExpression as JavaFunctionProps).className,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionProps).className]
  );
  const javaClassFieldId = useMemo(
    () => (functionExpression as JavaFunctionProps).classFieldId ?? generateUuid(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionProps).classFieldId]
  );
  const javaMethodName = useMemo(
    () => (functionExpression as JavaFunctionProps).methodName,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionProps).methodName]
  );
  const javaMethodFieldId = useMemo(
    () => (functionExpression as JavaFunctionProps).methodFieldId ?? generateUuid(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionProps).methodFieldId]
  );
  const feelExpression = useMemo(
    () => (functionExpression as FeelFunctionProps).expression,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as FeelFunctionProps).expression]
  );

  const extractContextEntriesFromJavaProps = useMemo(() => {
    return [
      {
        entryInfo: { id: FIRST_ENTRY_ID, name: i18n.class, dataType: DataType.String },
        entryExpression: {
          id: javaClassFieldId,
          noClearAction: true,
          logicType: LogicType.LiteralExpression,
          content: javaClassName ?? "",
        } as LiteralExpressionProps,
      },
      {
        entryInfo: { id: SECOND_ENTRY_ID, name: i18n.methodSignature, dataType: DataType.String },
        entryExpression: {
          id: javaMethodFieldId,
          noClearAction: true,
          logicType: LogicType.LiteralExpression,
          content: javaMethodName ?? "",
        } as LiteralExpressionProps,
      },
    ];
  }, [i18n.class, i18n.methodSignature, javaClassName, javaClassFieldId, javaMethodName, javaMethodFieldId]);

  const extractContextEntriesFromPmmlProps = useMemo(() => {
    return [
      {
        entryInfo: { id: FIRST_ENTRY_ID, name: i18n.document, dataType: DataType.String },
        entryExpression: {
          id: pmmlDocumentFieldId,
          noClearAction: true,
          logicType: LogicType.PMMLLiteralExpression,
          testId: "pmml-selector-document",
          noOptionsLabel: i18n.pmml.firstSelection,
          getOptions: () => _.map(pmmlParams, "document"),
          selected: pmmlDocument ?? "",
        } as PMMLLiteralExpressionProps,
      },
      {
        entryInfo: { id: SECOND_ENTRY_ID, name: i18n.model, dataType: DataType.String },
        entryExpression: {
          id: pmmlModelFieldId,
          noClearAction: true,
          logicType: LogicType.PMMLLiteralExpression,
          noOptionsLabel: i18n.pmml.secondSelection,
          testId: "pmml-selector-model",
          getOptions: () =>
            _.map(_.find(pmmlParams, (param) => param.document === pmmlDocument)?.modelsFromDocument, "model"),
          selected: pmmlModel ?? "",
        } as PMMLLiteralExpressionProps,
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
    (functionKind: FunctionKind) => {
      switch (functionKind) {
        case FunctionKind.Java: {
          return {
            id: FIRST_ENTRY_ID,
            entryExpression: {
              logicType: LogicType.Context,
              noClearAction: true,
              renderResult: false,
              noHandlerMenu: true,
              contextEntries: extractContextEntriesFromJavaProps,
            },
          } as DataRecord;
        }
        case FunctionKind.Pmml: {
          return {
            id: FIRST_ENTRY_ID,
            entryExpression: {
              logicType: LogicType.Context,
              noClearAction: true,
              renderResult: false,
              noHandlerMenu: true,
              contextEntries: extractContextEntriesFromPmmlProps,
            },
          } as DataRecord;
        }
        case FunctionKind.Feel:
        default: {
          return {
            id: FIRST_ENTRY_ID,
            entryExpression: feelExpression || { logicType: LogicType.LiteralExpression },
          } as DataRecord;
        }
      }
    },
    [extractContextEntriesFromJavaProps, extractContextEntriesFromPmmlProps, feelExpression]
  );

  const retrieveModelValue = useCallback(
    (documentValue: string, contextProps: ContextProps) =>
      documentValue === pmmlDocument
        ? _.includes(
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.getOptions(),
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.selected
          )
          ? (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.selected
          : ""
        : "",
    [pmmlDocument]
  );

  const extendDefinitionBasedOnFunctionKind = useCallback(
    (definition: Partial<FunctionProps>, updatedDefinition?: Partial<FunctionProps>, functionKind?: FunctionKind) => {
      const currentFunctionKind = functionKind ?? functionExpression.functionKind;
      switch (currentFunctionKind) {
        case FunctionKind.Java: {
          const contextProps = rows(currentFunctionKind).entryExpression as ContextProps;
          const firstEntry = _.nth(contextProps.contextEntries, 0)?.entryExpression as LiteralExpressionProps;
          const secondEntry = _.nth(contextProps.contextEntries, 1)?.entryExpression as LiteralExpressionProps;
          const className = firstEntry?.content ?? "";
          const classFieldId = firstEntry?.id;
          const methodName = secondEntry?.content ?? "";
          const methodFieldId = secondEntry?.id;
          return _.extend(definition, { className, methodName, classFieldId, methodFieldId });
        }
        case FunctionKind.Pmml: {
          const contextProps = rows(currentFunctionKind).entryExpression as ContextProps;
          const firstEntry = _.nth(contextProps.contextEntries, 0)?.entryExpression as PMMLLiteralExpressionProps;
          const secondEntry = _.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps;
          const documentValue = firstEntry?.selected ?? "";
          const documentFieldId = firstEntry?.id;
          const modelValue = retrieveModelValue(documentValue, contextProps);
          const modelFieldId = secondEntry?.id;
          const modelHasChanged =
            ((definition as PmmlFunctionProps)?.model ?? "") !==
            ((updatedDefinition as PmmlFunctionProps)?.model ?? "");
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
        case FunctionKind.Feel:
        default: {
          return _.extend(definition, {
            expression: rows(currentFunctionKind).entryExpression as ExpressionProps,
          });
        }
      }
    },
    [retrieveModelValue, rows, functionExpression.functionKind, pmmlModel, extractParametersFromPmmlProps]
  );

  const spreadFunctionExpressionDefinition = useCallback(
    (updatedFunctionExpression?: Partial<FunctionProps>) => {
      const extendedDefinition = extendDefinitionBasedOnFunctionKind(
        {
          id: functionExpression.id,
          logicType: functionExpression.logicType,
          name: functionExpression.name ?? DEFAULT_FIRST_PARAM_NAME,
          dataType: functionExpression.dataType ?? DataType.Undefined,
          functionKind: functionExpression.functionKind ?? FunctionKind.Feel,
          parametersWidth: functionExpression.parametersWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
          formalParameters: functionExpression.formalParameters,
        } as Partial<FunctionProps>,
        updatedFunctionExpression,
        updatedFunctionExpression?.functionKind
      );

      const updatedDefinition = {
        ...extendedDefinition,
        ...updatedFunctionExpression,
      };

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
            boxedExpressionEditorGWTService?.broadcastFunctionExpressionDefinition?.(
              updatedDefinition as FunctionProps
            );
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
    [boxedExpressionEditorGWTService, extendDefinitionBasedOnFunctionKind, setSupervisorHash, functionExpression]
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

  const columns = useMemo(() => {
    return [
      {
        label: functionExpression.name ?? DEFAULT_FIRST_PARAM_NAME,
        accessor: decisionNodeId,
        dataType: functionExpression.dataType ?? DataType.Undefined,
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
    ] as ColumnInstance[];
  }, [
    decisionNodeId,
    functionExpression.name,
    functionExpression.dataType,
    headerCellElement,
    functionExpression.parametersWidth,
  ]);

  const getHeaderVisibility = useMemo(() => {
    return functionExpression.isHeadless ? TableHeaderVisibility.LastLevel : TableHeaderVisibility.Full;
  }, [functionExpression.isHeadless]);

  const onFunctionKindSelect = useCallback(
    (itemId: string) => {
      const kind = itemId as FunctionKind;
      spreadFunctionExpressionDefinition({ functionKind: kind, formalParameters: [] } as Partial<FunctionProps>);
    },
    [spreadFunctionExpressionDefinition]
  );

  const onColumnsUpdate = useCallback(
    ({ columns: [column] }: ColumnsUpdateArgs<ColumnInstance>) => {
      functionExpression.onUpdatingNameAndDataType?.(column.label as string, column.dataType);
      spreadFunctionExpressionDefinition({
        name: column.label as string,
        dataType: column.dataType,
        parametersWidth: column.width as number,
      });
    },
    [functionExpression, spreadFunctionExpressionDefinition]
  );

  const resetRowCustomFunction = useCallback(
    (row) => {
      spreadFunctionExpressionDefinition({
        functionKind: FunctionKind.Feel,
        formalParameters: [],
      } as Partial<FunctionProps>);
      return resetEntry(row);
    },
    [spreadFunctionExpressionDefinition]
  );

  const handlerConfiguration = useMemo(() => {
    return [
      {
        group: _.upperCase(i18n.function),
        items: [{ name: i18n.rowOperations.clear, type: TableOperation.RowClear }],
      },
    ];
  }, [i18n.function, i18n.rowOperations.clear]);

  const defaultCell = useMemo(() => ({ parameters: ContextEntryExpressionCell }), []);

  const onRowsUpdate = useCallback(
    ({ rows: [row] }: RowsUpdateArgs<ContextEntryRecord<ContextProps>>) => {
      switch (functionExpression.functionKind) {
        case FunctionKind.Feel:
          spreadFunctionExpressionDefinition({ expression: row.entryExpression });
          break;
        case FunctionKind.Java:
          if (row.entryExpression.contextEntries) {
            spreadFunctionExpressionDefinition({
              className:
                (row.entryExpression.contextEntries[0]?.entryExpression as LiteralExpressionProps).content ?? "",
              methodName:
                (row.entryExpression.contextEntries[1]?.entryExpression as LiteralExpressionProps).content ?? "",
            });
          }
          break;
        case FunctionKind.Pmml:
          if (row.entryExpression.contextEntries) {
            spreadFunctionExpressionDefinition({
              document:
                (row.entryExpression.contextEntries[0]?.entryExpression as PMMLLiteralExpressionProps).selected ?? "",
              model:
                (row.entryExpression.contextEntries[1]?.entryExpression as PMMLLiteralExpressionProps).selected ?? "",
            });
          }
          break;
      }
    },
    [spreadFunctionExpressionDefinition, functionExpression.functionKind]
  );

  const tableRows = useMemo(() => [rows(functionExpression.functionKind)], [rows, functionExpression.functionKind]);

  const controllerCell = useMemo(
    () => (
      <FunctionKindSelector
        selectedFunctionKind={functionExpression.functionKind ?? FunctionKind.Feel}
        onFunctionKindSelect={onFunctionKindSelect}
      />
    ),
    [functionExpression.functionKind, onFunctionKindSelect]
  );

  return (
    <div className={`function-expression ${functionExpression.id}`}>
      <Table
        handlerConfiguration={handlerConfiguration}
        columns={columns}
        onColumnsUpdate={onColumnsUpdate}
        rows={tableRows}
        onRowsUpdate={onRowsUpdate}
        headerLevels={1}
        headerVisibility={getHeaderVisibility}
        controllerCell={controllerCell}
        defaultCell={defaultCell}
        resetRowCustomFunction={resetRowCustomFunction}
      />
    </div>
  );
};
