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

import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import _ from "lodash";
import * as React from "react";
import { useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableOperationConfig,
  BeeTableProps,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  generateUuid,
  PmmlFunctionExpressionDefinition,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContext";
import { useApportionedColumnWidthsIfNestedTable } from "../../resizing/Hooks";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  PMML_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { BeeTable, BeeTableColumnUpdate, BeeTableRef } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { useFunctionExpressionControllerCell, useFunctionExpressionParametersColumnHeader } from "./FunctionExpression";
import "./PmmlFunctionExpression.css";

type PMML_ROWTYPE = {
  value: string;
  label: string;
  pmmlFunctionExpression: PmmlFunctionExpressionDefinition;
};

export function PmmlFunctionExpression({
  functionExpression,
}: {
  functionExpression: PmmlFunctionExpressionDefinition & { isNested: boolean };
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const parametersColumnHeader = useFunctionExpressionParametersColumnHeader(functionExpression.formalParameters);

  const beeTableColumns = useMemo<ReactTable.Column<PMML_ROWTYPE>[]>(() => {
    return [
      {
        label: functionExpression.name ?? DEFAULT_EXPRESSION_NAME,
        accessor: functionExpression.id as any, // FIXME: Tiago -> ?
        dataType: functionExpression.dataType ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement: parametersColumnHeader,
            accessor: "parameters" as any,
            label: "parameters",
            isRowIndexColumn: false,
            dataType: undefined as any,
            width: undefined,
            columns: [
              {
                label: "label",
                accessor: "label" as any,
                dataType: undefined as any,
                isRowIndexColumn: false,
                isWidthPinned: true,
                isWidthConstant: true,
                width: PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
                minWidth: PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
              },
              {
                label: "value",
                accessor: "value" as any,
                dataType: undefined as any,
                isRowIndexColumn: false,
                isWidthConstant: true,
                width: PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
                minWidth: PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
              },
            ],
          },
        ],
      },
    ];
  }, [functionExpression.dataType, functionExpression.name, parametersColumnHeader]);

  const headerVisibility = useMemo(() => {
    return functionExpression.isNested
      ? BeeTableHeaderVisibility.SecondToLastLevel
      : BeeTableHeaderVisibility.AllLevels;
  }, [functionExpression.isNested]);

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<PMML_ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        name,
        dataType,
      }));
    },
    [setExpression]
  );

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: _.upperCase(i18n.function),
        items: [],
      },
    ];
  }, [i18n]);

  const beeTableRows = useMemo(() => {
    return [
      {
        label: "Document",
        value: functionExpression.document ?? "",
        pmmlFunctionExpression: functionExpression,
      },
      {
        label: "Model",
        value: functionExpression.model ?? "",
        pmmlFunctionExpression: functionExpression,
      },
    ];
  }, [functionExpression]);

  const controllerCell = useFunctionExpressionControllerCell(FunctionExpressionDefinitionKind.Pmml);

  const getRowKey = useCallback((r: ReactTable.Row<PMML_ROWTYPE>) => {
    return r.id;
  }, []);

  const onRowReset = useCallback(() => {
    setExpression((prev) => {
      return {
        ...prev,
        expression: {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
        },
      };
    });
  }, [setExpression]);

  const cellComponentByColumnAccessor: BeeTableProps<PMML_ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    () => ({
      label: (props) => <PmmlFunctionExpressionLabelCell {...props} />,
      value: (props) => <PmmlFunctionExpressionValueCell {...props} />,
    }),
    []
  );

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const columns = useMemo(
    () => [
      {
        minWidth: PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
        width: PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
        isFrozen: true,
      },
      {
        minWidth: PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
        width: PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
      },
    ],
    []
  );

  const { onColumnResizingWidthChange, isPivoting, columnResizingWidths } = usePublishedBeeTableResizableColumns(
    functionExpression.id,
    columns.length,
    true
  );

  const beeTableRef = React.useRef<BeeTableRef>(null);

  useApportionedColumnWidthsIfNestedTable(
    beeTableRef,
    isPivoting,
    functionExpression.isNested,
    PMML_FUNCTION_EXPRESSION_EXTRA_WIDTH,
    columns,
    columnResizingWidths,
    useMemo(() => [], []) // rows
  );

  /// //////////////////////////////////////////////////////

  return (
    <div className={`function-expression ${functionExpression.id}`}>
      <BeeTable<PMML_ROWTYPE>
        forwardRef={beeTableRef}
        onColumnResizingWidthChange={onColumnResizingWidthChange}
        resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
        operationConfig={beeTableOperationConfig}
        onColumnUpdates={onColumnUpdates}
        getRowKey={getRowKey}
        onRowReset={onRowReset}
        columns={beeTableColumns}
        rows={beeTableRows}
        headerLevelCountForAppendingRowIndexColumn={2}
        skipLastHeaderGroup={true}
        cellComponentByColumnAccessor={cellComponentByColumnAccessor}
        headerVisibility={headerVisibility}
        controllerCell={controllerCell}
        shouldRenderRowIndexColumn={true}
        shouldShowRowsInlineControls={false}
        shouldShowColumnsInlineControls={false}
      />
    </div>
  );
}

const PMML_BINDING_VALUE_PLACEHOLDER = "-- None selected --";

function PmmlFunctionExpressionLabelCell(props: React.PropsWithChildren<BeeTableCellProps<PMML_ROWTYPE>>) {
  const label = useMemo(() => {
    return props.data[props.rowIndex].label;
  }, [props.data, props.rowIndex]);

  useBeeTableSelectableCellRef(
    props.rowIndex,
    props.columnIndex,
    undefined,
    useCallback(() => label, [label])
  );

  return (
    <div className={"pmml-function-expression-label"}>
      <div className={"name"}>{label}</div>
      <div className={"data-type"}>{`(string)`}</div>
    </div>
  );
}

function PmmlFunctionExpressionValueCell(props: React.PropsWithChildren<BeeTableCellProps<PMML_ROWTYPE>>) {
  return props.rowIndex === 0 ? (
    <PmmlFunctionExpressionDocumentCell {...props} />
  ) : (
    <PmmlFunctionExpressionModelCell {...props} />
  );
}

function PmmlFunctionExpressionDocumentCell(props: React.PropsWithChildren<BeeTableCellProps<PMML_ROWTYPE>>) {
  const pmmlFunctionExpression = useMemo(
    () => props.data[props.rowIndex].pmmlFunctionExpression,
    [props.data, props.rowIndex]
  );

  const { pmmlParams, editorRef } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSelect = useCallback(
    (event, newDocument) => {
      setSelectOpen(false);
      setExpression((prev: PmmlFunctionExpressionDefinition) => ({
        ...prev,
        document: newDocument,
        model: "",
      }));
    },
    [setExpression]
  );

  const [isSelectOpen, setSelectOpen] = React.useState(false);

  useBeeTableSelectableCellRef(
    props.rowIndex,
    props.columnIndex,
    undefined,
    useCallback(() => pmmlFunctionExpression.document ?? "", [pmmlFunctionExpression.document])
  );

  return (
    <Select
      className={`pmml-document-select`}
      menuAppendTo={editorRef?.current ?? "inline"}
      ouiaId="pmml-document-select"
      placeholderText={PMML_BINDING_VALUE_PLACEHOLDER}
      aria-placeholder={PMML_BINDING_VALUE_PLACEHOLDER}
      variant={SelectVariant.single}
      onToggle={setSelectOpen}
      onSelect={onSelect}
      isOpen={isSelectOpen}
      selections={[pmmlFunctionExpression.document]}
    >
      {(pmmlParams ?? []).map(({ document }) => (
        <SelectOption
          data-testid={`pmml-${document}`}
          key={document}
          value={document}
          data-ouia-component-id={document}
        >
          {document}
        </SelectOption>
      ))}
    </Select>
  );
}

function PmmlFunctionExpressionModelCell(props: React.PropsWithChildren<BeeTableCellProps<PMML_ROWTYPE>>) {
  const pmmlFunctionExpression = useMemo(
    () => props.data[props.rowIndex].pmmlFunctionExpression,
    [props.data, props.rowIndex]
  );

  const { pmmlParams, editorRef } = useBoxedExpressionEditor();

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSelect = useCallback(
    (event, newModel) => {
      setSelectOpen(false);
      setExpression((prev: PmmlFunctionExpressionDefinition) => ({
        ...prev,
        model: newModel,
      }));
    },
    [setExpression]
  );

  const [isSelectOpen, setSelectOpen] = React.useState(false);

  const models = useMemo(
    () =>
      (pmmlParams ?? [])
        .filter(({ document }) => document === pmmlFunctionExpression.document)
        .flatMap(({ modelsFromDocument }) => modelsFromDocument ?? []),
    [pmmlFunctionExpression.document, pmmlParams]
  );

  useBeeTableSelectableCellRef(
    props.rowIndex,
    props.columnIndex,
    undefined,
    useCallback(() => pmmlFunctionExpression.model ?? "", [pmmlFunctionExpression.model])
  );

  return (
    <Select
      className={`pmml-document-select`}
      menuAppendTo={editorRef?.current ?? "inline"}
      ouiaId="pmml-document-select"
      isDisabled={!pmmlFunctionExpression.document}
      placeholderText={pmmlFunctionExpression.document ? PMML_BINDING_VALUE_PLACEHOLDER : "Select a document first"}
      aria-placeholder={PMML_BINDING_VALUE_PLACEHOLDER}
      variant={SelectVariant.single}
      onToggle={setSelectOpen}
      onSelect={onSelect}
      isOpen={isSelectOpen}
      selections={[pmmlFunctionExpression.model]}
    >
      {models.map(({ model }) => (
        <SelectOption data-testid={`pmml-${model}`} key={model} value={model} data-ouia-component-id={model}>
          {model}
        </SelectOption>
      ))}
    </Select>
  );
}
