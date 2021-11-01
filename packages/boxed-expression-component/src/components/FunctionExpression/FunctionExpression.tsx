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
import { PropsWithChildren, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
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
  const [selectedFunctionKind, setSelectedFunctionKind] = useState(
    functionExpression.functionKind ?? FunctionKind.Feel
  );
  const [width, setWidth] = useState(functionExpression.parametersWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH);
  const [parameters, setParameters] = useState(functionExpression.formalParameters ?? []);

  useEffect(() => {
    setSelectedFunctionKind(functionExpression.functionKind ?? FunctionKind.Feel);
  }, [functionExpression.functionKind]);

  useEffect(() => {
    setWidth(functionExpression.parametersWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH);
  }, [functionExpression.parametersWidth]);

  useEffect(() => {
    setParameters(functionExpression.formalParameters ?? []);
  }, [functionExpression.formalParameters]);

  const { i18n } = useBoxedExpressionEditorI18n();

  const { boxedExpressionEditorRef, setSupervisorHash, pmmlParams } = useContext(BoxedExpressionGlobalContext);

  const document = useRef((functionExpression as PmmlFunctionProps).document);
  const model = useRef((functionExpression as PmmlFunctionProps).model);

  const editParametersPopoverAppendTo = useCallback(() => {
    return () => boxedExpressionEditorRef.current!;
  }, [boxedExpressionEditorRef]);

  const headerCellElement = useMemo(
    () => (
      <PopoverMenu
        title={i18n.editParameters}
        appendTo={editParametersPopoverAppendTo()}
        className="parameters-editor-popover"
        minWidth="400px"
        body={<EditParameters parameters={parameters} setParameters={setParameters} />}
      >
        <div className={`parameters-list ${_.isEmpty(parameters) ? "empty-parameters" : ""}`}>
          <p className="pf-u-text-truncate">
            {_.isEmpty(parameters)
              ? i18n.editParameters
              : `(${_.join(
                  _.map(parameters, (parameter) => parameter.name),
                  ", "
                )})`}
          </p>
        </div>
      </PopoverMenu>
    ),
    [editParametersPopoverAppendTo, i18n.editParameters, parameters]
  );

  const [columns, setColumns] = useState<ColumnInstance[]>([]);

  useEffect(() => {
    setColumns([
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
            width,
            minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
          },
        ],
      },
    ] as ColumnInstance[]);
  }, [functionExpression.name, functionExpression.dataType, headerCellElement, width]);

  const extractContextEntriesFromJavaProps = useCallback(
    (javaProps: JavaFunctionProps & { children?: React.ReactNode }) => {
      return [
        {
          entryInfo: { name: i18n.class, dataType: DataType.String },
          entryExpression: {
            noClearAction: true,
            logicType: LogicType.LiteralExpression,
            content: javaProps.className,
          } as LiteralExpressionProps,
        },
        {
          entryInfo: { name: i18n.methodSignature, dataType: DataType.String },
          entryExpression: {
            noClearAction: true,
            logicType: LogicType.LiteralExpression,
            content: javaProps.methodName,
          } as LiteralExpressionProps,
        },
      ];
    },
    [i18n.class, i18n.methodSignature]
  );

  const extractContextEntriesFromPmmlProps = useCallback(() => {
    return [
      {
        entryInfo: { name: i18n.document, dataType: DataType.String },
        entryExpression: {
          noClearAction: true,
          logicType: LogicType.PMMLLiteralExpression,
          noOptionsLabel: i18n.pmml.firstSelection,
          getOptions: () => _.map(pmmlParams, "document"),
          selected: document.current,
        } as PMMLLiteralExpressionProps,
      },
      {
        entryInfo: { name: i18n.model, dataType: DataType.String },
        entryExpression: {
          noClearAction: true,
          logicType: LogicType.PMMLLiteralExpression,
          noOptionsLabel: i18n.pmml.secondSelection,
          getOptions: () =>
            _.map(_.find(pmmlParams, (param) => param.document === document.current)?.modelsFromDocument, "model"),
          selected: model.current,
        } as PMMLLiteralExpressionProps,
      },
    ];
  }, [i18n.document, i18n.model, i18n.pmml.firstSelection, i18n.pmml.secondSelection, pmmlParams]);

  const extractParametersFromPmmlProps = useCallback(() => {
    return (
      _.find(_.find(pmmlParams, { document: document.current })?.modelsFromDocument, {
        model: model.current,
      })?.parametersFromModel || []
    );
  }, [pmmlParams]);

  const evaluateRows = useCallback(
    (functionKind: FunctionKind) => {
      switch (functionKind) {
        case FunctionKind.Java: {
          const javaProps: PropsWithChildren<JavaFunctionProps> =
            functionExpression as PropsWithChildren<JavaFunctionProps>;
          return [
            {
              entryExpression: {
                logicType: LogicType.Context,
                noClearAction: true,
                renderResult: false,
                noHandlerMenu: true,
                contextEntries: extractContextEntriesFromJavaProps(javaProps),
              },
            } as DataRecord,
          ];
        }
        case FunctionKind.Pmml: {
          return [
            {
              entryExpression: {
                logicType: LogicType.Context,
                noClearAction: true,
                renderResult: false,
                noHandlerMenu: true,
                contextEntries: extractContextEntriesFromPmmlProps(),
              },
            } as DataRecord,
          ];
        }
        case FunctionKind.Feel:
        default: {
          const feelProps: PropsWithChildren<FeelFunctionProps> =
            functionExpression as PropsWithChildren<FeelFunctionProps>;
          return [
            { entryExpression: feelProps.expression || { logicType: LogicType.LiteralExpression } } as DataRecord,
          ];
        }
      }
    },
    [extractContextEntriesFromJavaProps, extractContextEntriesFromPmmlProps, functionExpression]
  );

  const [rows, setRows] = useState(evaluateRows(selectedFunctionKind));

  const retrieveModelValue = useCallback(
    (documentValue: string, contextProps: ContextProps) =>
      documentValue === document.current
        ? _.includes(
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.getOptions(),
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.selected
          )
          ? (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.selected
          : ""
        : "",
    []
  );

  const setParametersBasedOnDocumentAndModel = useCallback(
    (documentHasBeenChanged: boolean, modelHasBeenChanged: boolean) => {
      if (documentHasBeenChanged) {
        setParameters([]);
      }
      if (modelHasBeenChanged) {
        const parametersFromPmmlProps = extractParametersFromPmmlProps();
        if (!_.isEmpty(parametersFromPmmlProps)) {
          setParameters(parametersFromPmmlProps);
        }
      }
    },
    [extractParametersFromPmmlProps]
  );

  const extendDefinitionBasedOnFunctionKind = useCallback(
    (definition: FunctionProps, functionKind: FunctionKind) => {
      switch (functionKind) {
        case FunctionKind.Java: {
          const contextProps = _.first(rows)?.entryExpression as ContextProps;
          const className =
            (_.nth(contextProps.contextEntries, 0)?.entryExpression as LiteralExpressionProps)?.content || "";
          const methodName =
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as LiteralExpressionProps)?.content || "";
          return _.extend(definition, { className, methodName });
        }
        case FunctionKind.Pmml: {
          const contextProps = _.first(rows)?.entryExpression as ContextProps;
          const documentValue =
            (_.nth(contextProps.contextEntries, 0)?.entryExpression as PMMLLiteralExpressionProps)?.selected || "";
          const modelValue = retrieveModelValue(documentValue, contextProps);
          const documentHasBeenChanged = documentValue !== document.current;
          const modelHasBeenChanged = modelValue !== model.current;
          document.current = documentValue;
          model.current = modelValue;
          setParametersBasedOnDocumentAndModel(documentHasBeenChanged, modelHasBeenChanged);
          return _.extend(definition, { document: documentValue, model: modelValue });
        }
        case FunctionKind.Feel:
        default: {
          return _.extend(definition, { expression: _.first(rows)?.entryExpression as ExpressionProps });
        }
      }
    },
    [retrieveModelValue, rows, setParametersBasedOnDocumentAndModel]
  );

  useEffect(() => {
    spreadFunctionExpressionDefinition();
  }, [rows, parameters]);

  const spreadFunctionExpressionDefinition = useCallback(() => {
    const [expressionColumn] = columns;
    if (!expressionColumn) {
      return;
    }
    const updatedDefinition: FunctionProps = extendDefinitionBasedOnFunctionKind(
      {
        uid: functionExpression.uid,
        logicType: functionExpression.logicType,
        name: expressionColumn.accessor,
        dataType: expressionColumn.dataType,
        functionKind: selectedFunctionKind,
        formalParameters: parameters,
        parametersWidth: width,
      },
      selectedFunctionKind
    );

    if (functionExpression.isHeadless) {
      functionExpression.onUpdatingRecursiveExpression?.(_.omit(updatedDefinition, ["name", "dataType"]));
    } else {
      executeIfExpressionDefinitionChanged(
        functionExpression,
        updatedDefinition,
        () => {
          setSupervisorHash(hashfy(rows));
          window.beeApi?.broadcastFunctionExpressionDefinition?.(updatedDefinition);
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
  }, [
    columns,
    extendDefinitionBasedOnFunctionKind,
    setSupervisorHash,
    parameters,
    functionExpression,
    selectedFunctionKind,
    rows,
    width,
  ]);

  const getHeaderVisibility = useMemo(() => {
    return functionExpression.isHeadless ? TableHeaderVisibility.LastLevel : TableHeaderVisibility.Full;
  }, [functionExpression.isHeadless]);

  const onFunctionKindSelect = useCallback(
    (itemId: string) => {
      const kind = itemId as FunctionKind;
      setSelectedFunctionKind(kind);
      setParameters([]);
      setRows(evaluateRows(kind));
    },
    [evaluateRows]
  );

  const onColumnsUpdate = useCallback(
    ([expressionColumn]: ColumnInstance[]) => {
      functionExpression.onUpdatingNameAndDataType?.(expressionColumn.label as string, expressionColumn.dataType);
      setWidth(expressionColumn.width as number);

      setColumns((previousColumn) => {
        const [newColumn] = [...previousColumn];
        newColumn.label = expressionColumn.label as string;
        newColumn.accessor = expressionColumn.accessor;
        newColumn.dataType = expressionColumn.dataType;
        return [newColumn];
      });
      spreadFunctionExpressionDefinition();
    },
    [functionExpression, spreadFunctionExpressionDefinition]
  );

  const resetRowCustomFunction = useCallback((row) => {
    setSelectedFunctionKind(FunctionKind.Feel);
    return resetEntry(row);
  }, []);

  const handlerConfiguration = useMemo(() => {
    return [
      {
        group: _.upperCase(i18n.function),
        items: [{ name: i18n.rowOperations.clear, type: TableOperation.RowClear }],
      },
    ];
  }, [i18n.function, i18n.rowOperations.clear]);

  const defaultCell = useMemo(() => ({ parameters: ContextEntryExpressionCell }), []);

  return (
    <div className={`function-expression ${functionExpression.uid}`}>
      <Table
        handlerConfiguration={handlerConfiguration}
        columns={columns}
        onColumnsUpdate={onColumnsUpdate}
        rows={rows}
        onRowsUpdate={setRows}
        headerLevels={1}
        headerVisibility={getHeaderVisibility}
        controllerCell={
          <FunctionKindSelector
            selectedFunctionKind={selectedFunctionKind}
            onFunctionKindSelect={onFunctionKindSelect}
          />
        }
        defaultCell={defaultCell}
        resetRowCustomFunction={resetRowCustomFunction}
      />
    </div>
  );
};
