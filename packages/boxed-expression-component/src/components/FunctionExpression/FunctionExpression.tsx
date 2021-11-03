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
import { PropsWithChildren, useCallback, useContext, useMemo } from "react";
import {
  ContextProps,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  executeIfExpressionDefinitionChanged,
  ExpressionProps,
  FeelFunctionProps,
  FunctionKind,
  FunctionProps,
  JavaFunctionProps,
  LiteralExpressionProps,
  LogicType,
  PmmlFunctionProps,
  PMMLLiteralExpressionProps,
  resetEntry,
  TableHeaderVisibility,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import * as _ from "lodash";
import { BoxedExpressionGlobalContext } from "../../context";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { EditParameters } from "./EditParameters";
import { hashfy } from "../Resizer";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

export const FunctionExpression: React.FunctionComponent<FunctionProps> = (
  functionExpression: PropsWithChildren<FunctionProps>
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { boxedExpressionEditorRef, setSupervisorHash, pmmlParams } = useContext(BoxedExpressionGlobalContext);
  const pmmlDocument = useMemo(
    () => (functionExpression as PmmlFunctionProps).document,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionProps).document]
  );
  const pmmlModel = useMemo(
    () => (functionExpression as PmmlFunctionProps).model,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as PmmlFunctionProps).model]
  );
  const javaClassName = useMemo(
    () => (functionExpression as JavaFunctionProps).className,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionProps).className]
  );
  const javaMethodName = useMemo(
    () => (functionExpression as JavaFunctionProps).methodName,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as JavaFunctionProps).methodName]
  );
  const fellExpression = useMemo(
    () => (functionExpression as FeelFunctionProps).expression,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [(functionExpression as FeelFunctionProps).expression]
  );

  const extractContextEntriesFromJavaProps = useMemo(() => {
    return [
      {
        entryInfo: { name: i18n.class, dataType: DataType.String },
        entryExpression: {
          noClearAction: true,
          logicType: LogicType.LiteralExpression,
          content: javaClassName ?? "",
        } as LiteralExpressionProps,
      },
      {
        entryInfo: { name: i18n.methodSignature, dataType: DataType.String },
        entryExpression: {
          noClearAction: true,
          logicType: LogicType.LiteralExpression,
          content: javaMethodName ?? "",
        } as LiteralExpressionProps,
      },
    ];
  }, [i18n.class, i18n.methodSignature, javaClassName, javaMethodName]);

  const extractContextEntriesFromPmmlProps = useMemo(() => {
    return [
      {
        entryInfo: { name: i18n.document, dataType: DataType.String },
        entryExpression: {
          noClearAction: true,
          logicType: LogicType.PMMLLiteralExpression,
          testId: "pmml-selector-document",
          noOptionsLabel: i18n.pmml.firstSelection,
          getOptions: () => _.map(pmmlParams, "document"),
          selected: pmmlDocument ?? "",
        } as PMMLLiteralExpressionProps,
      },
      {
        entryInfo: { name: i18n.model, dataType: DataType.String },
        entryExpression: {
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
    pmmlModel,
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
            entryExpression: fellExpression || { logicType: LogicType.LiteralExpression },
          } as DataRecord;
        }
      }
    },
    [extractContextEntriesFromJavaProps, extractContextEntriesFromPmmlProps, fellExpression]
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
    (definition: Partial<FunctionProps>, functionKind?: FunctionKind) => {
      const currentFunctionKind = functionKind ?? functionExpression.functionKind;
      switch (currentFunctionKind) {
        case FunctionKind.Java: {
          const contextProps = rows(currentFunctionKind).entryExpression as ContextProps;
          const className =
            (_.nth(contextProps.contextEntries, 0)?.entryExpression as LiteralExpressionProps)?.content || "";
          const methodName =
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as LiteralExpressionProps)?.content || "";
          return _.extend(definition, { className, methodName });
        }
        case FunctionKind.Pmml: {
          const contextProps = rows(currentFunctionKind).entryExpression as ContextProps;
          const documentValue =
            (_.nth(contextProps.contextEntries, 0)?.entryExpression as PMMLLiteralExpressionProps)?.selected || "";
          const modelValue = retrieveModelValue(documentValue, contextProps);
          if (pmmlModel !== "") {
            const parametersFromPmmlProps = extractParametersFromPmmlProps;
            if (!_.isEmpty(parametersFromPmmlProps) && parametersFromPmmlProps) {
              definition.formalParameters = parametersFromPmmlProps;
            }
          } else {
            definition.formalParameters = [];
          }
          return _.extend(definition, { document: documentValue, model: modelValue });
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
          uid: functionExpression.uid,
          logicType: functionExpression.logicType,
          name: functionExpression.name,
          dataType: functionExpression.dataType,
          functionKind: functionExpression.functionKind ?? FunctionKind.Feel,
          parametersWidth: functionExpression.parametersWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
          formalParameters: functionExpression.formalParameters ?? [],
        } as Partial<FunctionProps>,
        updatedFunctionExpression?.functionKind
      );
      const updatedDefinition = {
        ...extendedDefinition,
        ...updatedFunctionExpression,
      };

      if (functionExpression.isHeadless) {
        functionExpression.onUpdatingRecursiveExpression?.(_.omit(updatedDefinition, ["name", "dataType"]));
      } else {
        executeIfExpressionDefinitionChanged(
          functionExpression,
          updatedDefinition,
          () => {
            setSupervisorHash(hashfy(rows(updatedFunctionExpression?.functionKind ?? functionExpression.functionKind)));
            window.beeApi?.broadcastFunctionExpressionDefinition?.(updatedDefinition as FunctionProps);
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
    [extendDefinitionBasedOnFunctionKind, setSupervisorHash, functionExpression, rows]
  );

  const editParametersPopoverAppendTo = useCallback(() => {
    return () => boxedExpressionEditorRef.current!;
  }, [boxedExpressionEditorRef]);

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
        accessor: functionExpression.name ?? DEFAULT_FIRST_PARAM_NAME,
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
  }, [functionExpression.name, functionExpression.dataType, headerCellElement, functionExpression.parametersWidth]);

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
    ([expressionColumn]: ColumnInstance[]) => {
      functionExpression.onUpdatingNameAndDataType?.(expressionColumn.label as string, expressionColumn.dataType);
      spreadFunctionExpressionDefinition({
        name: expressionColumn.label as string,
        dataType: expressionColumn.dataType,
        parametersWidth: expressionColumn.width as number,
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
    ([entries]) => {
      switch (functionExpression.functionKind) {
        case FunctionKind.Feel:
          spreadFunctionExpressionDefinition({ expression: entries.entryExpression });
          break;
        case FunctionKind.Java:
          const updatedJavaProperties = entries.entryExpression.contextEntries?.reduce(
            (acc: any, contextEntry: any) => {
              if (contextEntry.entryInfo.name?.includes("method")) {
                acc.methodName = contextEntry.entryExpression.content;
              }
              if (contextEntry.entryInfo.name?.includes("class")) {
                acc.className = contextEntry.entryExpression.content;
              }
              return acc;
            },
            {}
          );
          if (updatedJavaProperties) {
            spreadFunctionExpressionDefinition({
              className: updatedJavaProperties.className,
              methodName: updatedJavaProperties.methodName,
            });
          }
          break;
        case FunctionKind.Pmml:
          const updatedPmmlProperties = entries.entryExpression.contextEntries?.reduce(
            (acc: any, contextEntry: any) => {
              if (contextEntry.entryInfo.name.includes("model")) {
                acc.model = contextEntry.entryExpression.selected;
              }
              if (contextEntry.entryInfo.name.includes("document")) {
                acc.document = contextEntry.entryExpression.selected;
              }
              return acc;
            },
            {}
          );
          if (updatedPmmlProperties) {
            spreadFunctionExpressionDefinition({
              document: updatedPmmlProperties.document,
              model: updatedPmmlProperties.model,
            });
          }
          break;
      }
    },
    [spreadFunctionExpressionDefinition, functionExpression.functionKind]
  );

  return (
    <div className={`function-expression ${functionExpression.uid}`}>
      <Table
        handlerConfiguration={handlerConfiguration}
        columns={columns}
        onColumnsUpdate={onColumnsUpdate}
        rows={[rows(functionExpression.functionKind)]}
        onRowsUpdate={onRowsUpdate}
        headerLevels={1}
        headerVisibility={getHeaderVisibility}
        controllerCell={
          <FunctionKindSelector
            selectedFunctionKind={functionExpression.functionKind ?? FunctionKind.Feel}
            onFunctionKindSelect={onFunctionKindSelect}
          />
        }
        defaultCell={defaultCell}
        resetRowCustomFunction={resetRowCustomFunction}
      />
    </div>
  );
};
