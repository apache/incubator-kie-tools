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

import * as React from "react";
import { useEffect, useMemo, useRef } from "react";

import * as ReactTable from "react-table";

import { SceSim__simulationType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { BeeTableHeaderVisibility } from "@kie-tools/boxed-expression-component/dist/api/BeeTable";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";
import { StandaloneBeeTable } from "@kie-tools/boxed-expression-component/dist/table/BeeTable";

import { useTestScenarioEditorI18n } from "../i18n";

function TestScenarioTable({ simulationData }: { simulationData: SceSim__simulationType }) {
  type ROWTYPE = any; // FIXME: https://github.com/kiegroup/kie-issues/issues/169

  const { i18n } = useTestScenarioEditorI18n();

  const tableScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });

  useEffect(() => {
    tableScrollableElementRef.current.current =
      document.querySelector(".kie-tools--dmn-runner-table--drawer")?.querySelector(".pf-c-drawer__content") ?? null;
  }, []);

  const fakeList = [{ label: "asd1" }, { label: "asd2" }, { label: "asd3" }];

  const fakeList2 = [{ label: "asd11" }, { label: "asd12" }, { label: "asd13" }];

  const scesimTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    const descriptionSection = {
      groupType: "description",
      id: "DESCRIPTION",
      accessor: "DESCRIPTION",
      label: "Description",
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      width: 200,
      minWidth: 200,
    };

    const givenColumns: ReactTable.Column<ROWTYPE>[] = (fakeList ?? []).map((inputClause, inputIndex) => ({
      accessor: inputClause.label,
      label: inputClause.label,
      id: inputClause.label,
      dataType: "asd",
      width: 150,
      minWidth: 100,
      groupType: "input",
      cssClasses: "decision-table--input",
      isRowIndexColumn: false,
    }));

    const givenSection = {
      groupType: "given",
      id: "GIVEN",
      accessor: "GIVEN",
      label: "GIVEN",
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      width: undefined,
      columns: givenColumns,
    };

    const expectedColumns: ReactTable.Column<ROWTYPE>[] = (fakeList2 ?? []).map((inputClause, inputIndex) => ({
      accessor: inputClause.label,
      label: inputClause.label,
      id: inputClause.label,
      dataType: "asd",
      width: 150,
      minWidth: 100,
      groupType: "output",
      cssClasses: "decision-table--input",
      isRowIndexColumn: false,
    }));

    const expectedSection = {
      groupType: "expected",
      id: "EXPECTED",
      accessor: "EXPECTED",
      label: "EXPECTED",
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      width: undefined,
      columns: expectedColumns,
    };

    return [descriptionSection, givenSection, expectedSection];

    /*const inputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.input ?? []).map(
      (inputClause, inputIndex) => ({
        accessor: inputClause.id ?? generateUuid(),
        label: inputClause.name,
        id: inputClause.id,
        dataType: inputClause.dataType,
        width: inputClause.width ?? DECISION_TABLE_INPUT_MIN_WIDTH,
        setWidth: setInputColumnWidth(inputIndex),
        minWidth: DECISION_TABLE_INPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.InputClause,
        cssClasses: "decision-table--input",
        isRowIndexColumn: false,
      })
    );

    const outputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.output ?? []).map(
      (outputClause, outputIndex) => ({
        accessor: outputClause.id ?? generateUuid(),
        id: outputClause.id,
        label:
          decisionTableExpression.output?.length == 1
            ? decisionTableExpression.name ?? DEFAULT_EXPRESSION_NAME
            : outputClause.name,
        dataType:
          decisionTableExpression.output?.length == 1 ? decisionTableExpression.dataType : outputClause.dataType,
        width: outputClause.width ?? DECISION_TABLE_OUTPUT_MIN_WIDTH,
        setWidth: setOutputColumnWidth(outputIndex),
        minWidth: DECISION_TABLE_OUTPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.OutputClause,
        cssClasses: "decision-table--output",
        isRowIndexColumn: false,
      })
    );

    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      id: decisionTableExpression.id,
      accessor: "decision-table-expression" as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
      label: decisionTableExpression.name ?? DEFAULT_EXPRESSION_NAME,
      dataType: decisionTableExpression.dataType ?? DmnBuiltInDataType.Undefined,
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      width: undefined,
      columns: outputColumns,
    };

    const annotationColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.annotations ?? []).map(
      (annotation, annotationIndex) => {
        const annotationId = generateUuid();
        return {
          accessor: annotationId,
          id: annotationId,
          label: annotation.name,
          width: annotation.width ?? DECISION_TABLE_ANNOTATION_MIN_WIDTH,
          setWidth: setAnnotationColumnWidth(annotationIndex),
          minWidth: DECISION_TABLE_ANNOTATION_MIN_WIDTH,
          isInlineEditable: true,
          groupType: DecisionTableColumnType.Annotation,
          cssClasses: "decision-table--annotation",
          isRowIndexColumn: false,
          dataType: undefined as any,
        };
      }
    ); */

    /*
    if (outputColumns.length == 1) {
      return [...inputColumns, ...outputColumns, ...annotationColumns];
    } else {
      return [...inputColumns, outputSection, ...annotationColumns];
    }*/
    // return [descriptionColumn];
  }, [fakeList, fakeList2]);

  const simulationRows = useMemo(
    () =>
      (simulationData.scesimData.Scenario ?? []).map((rule) => {
        /*const ruleRow = [...rule.inputEntries, ...rule.outputEntries, ...rule.annotationEntries];
        const tableRow = getColumnsAtLastLevel(beeTableColumns).reduce(
          (tableRow: ROWTYPE, column, columnIndex) => {
            tableRow[column.accessor] = ruleRow[columnIndex] ?? "";
            return tableRow;
          },
          { id: rule.id }
        );
        return tableRow; */
      }),
    [simulationData]
  );

  return (
    <StandaloneBeeTable
      scrollableParentRef={tableScrollableElementRef.current}
      allowedOperations={() => []}
      getColumnKey={undefined}
      getRowKey={undefined}
      tableId={undefined}
      isEditableHeader={true}
      headerLevelCountForAppendingRowIndexColumn={1}
      headerVisibility={BeeTableHeaderVisibility.AllLevels}
      operationConfig={undefined}
      columns={scesimTableColumns}
      rows={[{}, {}]}
      isReadOnly={false} //OK
      enableKeyboardNavigation={true} //OK
      shouldRenderRowIndexColumn={true} //OK
      shouldShowRowsInlineControls={true} //OK
      shouldShowColumnsInlineControls={true} //OK
      resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_ALWAYS} //OK
    />
  );
}

export default TestScenarioTable;
