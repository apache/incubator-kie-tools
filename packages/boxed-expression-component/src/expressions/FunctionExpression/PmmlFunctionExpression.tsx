/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import * as React from "react";
import { useCallback, useEffect, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableCellProps,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  FunctionExpressionDefinitionKind,
  generateUuid,
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
import {
  DMN15__tContext,
  DMN15__tContextEntry,
  DMN15__tFunctionDefinition,
  DMN15__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export type PmmlFunctionExpressionDefinition = DMN15__tFunctionDefinition & {
  "@_kind": "PMML";
  __$$element: "functionDefinition";
  isNested: boolean;
  parentElementId: string;
};

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
  const { expressionHolderId, widthsById } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const parametersColumnHeader = useFunctionExpressionParametersColumnHeader(functionExpression.formalParameter);

  const beeTableColumns = useMemo<ReactTable.Column<PMML_ROWTYPE>[]>(() => {
    return [
      {
        label: functionExpression["@_label"] ?? DEFAULT_EXPRESSION_NAME,
        accessor: expressionHolderId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        dataType: functionExpression["@_typeRef"] ?? "",
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
  }, [expressionHolderId, functionExpression, parametersColumnHeader]);

  const headerVisibility = useMemo(() => {
    return functionExpression.isNested
      ? BeeTableHeaderVisibility.SecondToLastLevel
      : BeeTableHeaderVisibility.AllLevels;
  }, [functionExpression.isNested]);

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<PMML_ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        "@_label": name,
        "@_typeRef": dataType,
      }));
    },
    [setExpression]
  );

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [{ name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy }],
      },
      {
        group: i18n.function.toUpperCase(),
        items: [{ name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset }],
      },
    ];
  }, [i18n]);

  const getDocument = useCallback(() => {
    return (functionExpression.expression as DMN15__tContext).contextEntry?.find(
      ({ variable }) => variable?.["@_name"] === "document"
    );
  }, [functionExpression.expression]);

  const getModel = useCallback(() => {
    return (functionExpression.expression as DMN15__tContext).contextEntry?.find(
      ({ variable }) => variable?.["@_name"] === "model"
    );
  }, [functionExpression.expression]);

  const beeTableRows = useMemo(() => {
    const document = getDocument();
    const model = getModel();

    return [
      {
        label: "Document",
        value:
          (document?.expression as DMN15__tLiteralExpression | undefined)?.text?.__$$text.replaceAll(`"`, ``) ?? "",
        pmmlFunctionExpression: functionExpression,
      },
      {
        label: "Model",
        value: (model?.expression as DMN15__tLiteralExpression | undefined)?.text?.__$$text.replaceAll(`"`, ``) ?? "",
        pmmlFunctionExpression: functionExpression,
      },
    ];
  }, [functionExpression, getDocument, getModel]);

  const controllerCell = useFunctionExpressionControllerCell(FunctionExpressionDefinitionKind.Pmml);

  const getRowKey = useCallback((r: ReactTable.Row<PMML_ROWTYPE>) => {
    return r.id;
  }, []);

  const onRowReset = useCallback(() => {
    setExpression((prev) => {
      return {
        ...prev,
        expression: undefined!,
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
    functionExpression["@_id"]!,
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

  const allowedOperations = useCallback((conditions: BeeTableContextMenuAllowedOperationsConditions) => {
    return [BeeTableOperation.SelectionCopy];
  }, []);

  return (
    <div className={`function-expression ${functionExpression["@_id"]}`}>
      <BeeTable<PMML_ROWTYPE>
        forwardRef={beeTableRef}
        onColumnResizingWidthChange={onColumnResizingWidthChange}
        resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
        operationConfig={beeTableOperationConfig}
        allowedOperations={allowedOperations}
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
        widthsById={widthsById}
      />
    </div>
  );
}

const PMML_BINDING_VALUE_PLACEHOLDER = "-- None selected --";

function PmmlFunctionExpressionLabelCell(props: React.PropsWithChildren<BeeTableCellProps<PMML_ROWTYPE>>) {
  const label = useMemo(() => {
    return props.data[props.rowIndex].label;
  }, [props.data, props.rowIndex]);

  const { isActive } = useBeeTableSelectableCellRef(
    props.rowIndex,
    props.columnIndex,
    undefined,
    useCallback(() => label, [label])
  );

  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject("");
    }
  }, [beeGwtService, isActive]);

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

function getDocumentEntry(pmmlFunction: PmmlFunctionExpressionDefinition): DMN15__tContextEntry {
  return (
    (pmmlFunction.expression as DMN15__tContext).contextEntry?.find(
      ({ variable }) => variable?.["@_name"] === "document"
    ) ?? {
      "@_id": generateUuid(),
      expression: {
        "@_id": generateUuid(),
        __$$element: "literalExpression",
      },
    }
  );
}

function getModelEntry(pmmlFunction: PmmlFunctionExpressionDefinition): DMN15__tContextEntry {
  return (
    (pmmlFunction.expression as DMN15__tContext).contextEntry?.find(
      ({ variable }) => variable?.["@_name"] === "model"
    ) ?? {
      "@_id": generateUuid(),
      expression: {
        "@_id": generateUuid(),
        __$$element: "literalExpression",
      },
    }
  );
}

function getUpdatedExpression(
  prev: PmmlFunctionExpressionDefinition,
  newDocument: string,
  newModel: string
): PmmlFunctionExpressionDefinition {
  const document = getDocumentEntry(prev);
  const model = getModelEntry(prev);

  return {
    ...prev,
    expression: {
      __$$element: "context",
      ...(prev.expression as DMN15__tContext),
      contextEntry: [
        {
          ...document,
          variable: {
            "@_name": "document",
          },
          expression: {
            __$$element: "literalExpression",
            text: { __$$text: newDocument },
          },
        },
        {
          ...model,
          variable: {
            "@_name": "model",
          },
          expression: {
            __$$element: "literalExpression",
            text: { __$$text: newModel },
          },
        },
      ],
    },
  };
}

function PmmlFunctionExpressionDocumentCell(props: React.PropsWithChildren<BeeTableCellProps<PMML_ROWTYPE>>) {
  const pmmlFunctionExpression = useMemo(
    () => props.data[props.rowIndex].pmmlFunctionExpression,
    [props.data, props.rowIndex]
  );

  const { pmmlDocuments, editorRef } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const contextExpression = useMemo(() => {
    if (pmmlFunctionExpression.expression?.__$$element === "context") {
      return pmmlFunctionExpression.expression;
    }
  }, [pmmlFunctionExpression.expression]);

  const pmmlDocument = useMemo(() => {
    if (contextExpression) {
      const docExpression = contextExpression.contextEntry?.find(({ variable }) => variable?.["@_name"] === "document");
      if (docExpression?.expression.__$$element === "literalExpression") {
        return docExpression?.expression.text?.__$$text;
      }
    }
  }, [contextExpression]);

  const onSelect = useCallback(
    (event, newDocument) => {
      setSelectOpen(false);
      setExpression((prev: PmmlFunctionExpressionDefinition) => {
        return getUpdatedExpression(prev, newDocument, "");
      });
    },
    [setExpression]
  );

  const [isSelectOpen, setSelectOpen] = React.useState(false);

  useBeeTableSelectableCellRef(
    props.rowIndex,
    props.columnIndex,
    undefined,
    useCallback(() => pmmlDocument ?? "", [pmmlDocument])
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
      selections={[pmmlDocument]}
    >
      {(pmmlDocuments ?? []).map(({ document }) => (
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

  const { pmmlDocuments, editorRef } = useBoxedExpressionEditor();

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSelect = useCallback(
    (event, newModel) => {
      setSelectOpen(false);

      setExpression((prev: PmmlFunctionExpressionDefinition) => {
        const document = getDocumentEntry(prev);
        const currentDocument =
          document.expression?.__$$element === "literalExpression" ? document.expression.text?.__$$text ?? "" : "";

        return getUpdatedExpression(prev, currentDocument, newModel);
      });
    },
    [setExpression]
  );

  const [isSelectOpen, setSelectOpen] = React.useState(false);

  const contextExpression = useMemo(() => {
    if (pmmlFunctionExpression.expression?.__$$element === "context") {
      return pmmlFunctionExpression.expression;
    }
  }, [pmmlFunctionExpression.expression]);

  const pmmlDocument = useMemo(() => {
    if (contextExpression) {
      const docExpression = contextExpression.contextEntry?.find(({ variable }) => variable?.["@_name"] === "document");
      if (docExpression?.expression.__$$element === "literalExpression") {
        return docExpression?.expression.text?.__$$text;
      }
    }
  }, [contextExpression]);

  const model = useMemo(() => {
    if (contextExpression) {
      const modelExpression = contextExpression.contextEntry?.find(({ variable }) => variable?.["@_name"] === "model");
      if (modelExpression?.expression.__$$element === "literalExpression") {
        return modelExpression?.expression.text?.__$$text;
      }
    }
  }, [contextExpression]);

  const models = useMemo(
    () =>
      (pmmlDocuments ?? [])
        .filter(({ document }) => document === pmmlDocument)
        .flatMap(({ modelsFromDocument }) => modelsFromDocument ?? []),
    [pmmlDocument, pmmlDocuments]
  );

  useBeeTableSelectableCellRef(
    props.rowIndex,
    props.columnIndex,
    undefined,
    useCallback(() => model ?? "", [model])
  );

  return (
    <Select
      className={`pmml-document-select`}
      menuAppendTo={editorRef?.current ?? "inline"}
      ouiaId="pmml-document-select"
      isDisabled={!pmmlDocument}
      placeholderText={pmmlDocument ? PMML_BINDING_VALUE_PLACEHOLDER : "Select a document first"}
      aria-placeholder={PMML_BINDING_VALUE_PLACEHOLDER}
      variant={SelectVariant.single}
      onToggle={setSelectOpen}
      onSelect={onSelect}
      isOpen={isSelectOpen}
      selections={[model]}
    >
      {models.map(({ model }) => (
        <SelectOption data-testid={`pmml-${model}`} key={model} value={model} data-ouia-component-id={model}>
          {model}
        </SelectOption>
      ))}
    </Select>
  );
}
