import { Select, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as React from "react";
import { useCallback, useMemo } from "react";
import {
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableProps,
  PmmlFunctionExpressionDefinition,
} from "../../api";
import {
  PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { useBeeTableCell } from "../../selection/BeeTableSelectionContext";
import { BeeTable } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ROWTYPE } from "./FunctionExpression";
import "./PmmlFunctionBindingCell.css";

const PMML_BINDING_VALUE_PLACEHOLDER = "-- None selected --";

type PMML_ROWTYPE = {
  value: string;
  label: string;
  pmmlFunctionExpression: PmmlFunctionExpressionDefinition;
};

export function PmmlFunctionBindingCell({ data, rowIndex }: BeeTableCellProps<ROWTYPE>) {
  const functionExpression = data[rowIndex].functionExpression as PmmlFunctionExpressionDefinition;

  const beeTableColumns = useMemo(
    () => [
      {
        label: "label",
        accessor: "label" as any,
        dataType: undefined as any,
        isRowIndexColumn: false,
        width: undefined,
        minWidth: PMML_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
      },
      {
        label: "value",
        accessor: "value" as any,
        dataType: undefined as any,
        isRowIndexColumn: false,
        width: undefined,
        minWidth: PMML_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
      },
    ],
    []
  );

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

  const cellComponentByColumnId: BeeTableProps<PMML_ROWTYPE>["cellComponentByColumnId"] = useMemo(
    () => ({
      label: (props) => <PmmlFunctionExpressionLabelCell {...props} />,
      value: (props) => <PmmlFunctionExpressionValueCell {...props} />,
    }),
    []
  );

  return (
    <BeeTable<PMML_ROWTYPE>
      columns={beeTableColumns}
      rows={beeTableRows}
      getRowKey={(r) => r.id}
      getColumnKey={(c) => c.id}
      operationConfig={[]}
      cellComponentByColumnId={cellComponentByColumnId}
      headerVisibility={BeeTableHeaderVisibility.None}
      shouldRenderRowIndexColumn={false}
      shouldShowRowsInlineControls={false}
      shouldShowColumnsInlineControls={false}
    />
  );
}

function PmmlFunctionExpressionLabelCell(props: React.PropsWithChildren<BeeTableCellProps<PMML_ROWTYPE>>) {
  const label = useMemo(() => {
    return props.data[props.rowIndex].label;
  }, [props.data, props.rowIndex]);

  useBeeTableCell(
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

  useBeeTableCell(
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

  useBeeTableCell(
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
