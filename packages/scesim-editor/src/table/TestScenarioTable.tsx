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

import {
  SceSim__FactMappingType,
  SceSim__simulationType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

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

  const simulationColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    const givenFactMappingToFactMap: Map<{ factName: String; factType: String }, SceSim__FactMappingType[]> = new Map();
    const expectFactMappingToFactMap: Map<{ factName: String; factType: String }, SceSim__FactMappingType[]> =
      new Map();
    let descriptionFactMapping: SceSim__FactMappingType | undefined;

    (simulationData?.scesimModelDescriptor?.factMappings?.FactMapping ?? []).forEach((factMapping) => {
      if (factMapping.expressionIdentifier.type === "GIVEN") {
        givenFactMappingToFactMap.set(
          { factName: factMapping.factAlias, factType: factMapping.factIdentifier!.className! },
          [
            ...(givenFactMappingToFactMap.get({
              factName: factMapping.factAlias,
              factType: factMapping.factIdentifier!.className!,
            }) ?? []),
            factMapping,
          ]
        );
      } else if (factMapping.expressionIdentifier.type === "EXPECT") {
        expectFactMappingToFactMap.set(
          { factName: factMapping.factAlias, factType: factMapping.factIdentifier!.className! },
          [
            ...(expectFactMappingToFactMap.get({
              factName: factMapping.factAlias,
              factType: factMapping.factIdentifier!.className!,
            }) ?? []),
            factMapping,
          ]
        );
      } else if (
        factMapping.expressionIdentifier.type === "OTHER" &&
        factMapping.expressionIdentifier.name === "Description"
      ) {
        descriptionFactMapping = factMapping;
      }
    });

    const descriptionSection = {
      groupType: descriptionFactMapping!.expressionIdentifier.type,
      id: descriptionFactMapping!.factAlias + descriptionFactMapping!.expressionIdentifier.type,
      //accessor: descriptionFactMapping!.expressionIdentifier.type,
      label: descriptionFactMapping!.factAlias,
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      width: descriptionFactMapping!.columnWidth ?? 300,
      minWidth: descriptionFactMapping!.columnWidth ?? 300,
    };

    const givenSection = {
      groupType: "given",
      id: "GIVEN",
      accessor: "GIVEN",
      label: "GIVEN",
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      width: undefined,
      columns: [...givenFactMappingToFactMap.entries()].map((entry) => {
        return {
          accessor: entry[0].factName + "GIVEN",
          label: entry[0].factName,
          id: entry[0].factName + "GIVEN",
          dataType: entry[0].factType,
          groupType: "GIVEN",
          cssClasses: "decision-table--input",
          isRowIndexColumn: false,
          columns: entry[1].map((factMapping) => {
            return {
              accessor: factMapping.factAlias,
              label: factMapping.expressionAlias,
              id: factMapping.factAlias + factMapping.expressionAlias + "GIVEN",
              dataType: factMapping.className,
              width: factMapping.columnWidth,
              minWidth: 100,
              groupType: factMapping.expressionIdentifier.type,
              cssClasses: "decision-table--input",
              isRowIndexColumn: false,
            };
          }),
        };
      }),
    };

    const expectSection = {
      groupType: "expected",
      id: "EXPECT",
      accessor: "EXPECT",
      label: "EXPECT",
      isRowIndexColumn: false,
      width: undefined,
      columns: [...expectFactMappingToFactMap.entries()].map((entry) => {
        return {
          accessor: entry[0].factName,
          label: entry[0].factName,
          id: entry[0].factName,
          dataType: entry[0].factType,
          groupType: "EXPECT",
          cssClasses: "decision-table--input",
          isRowIndexColumn: false,
          columns: entry[1].map((factMapping) => {
            return {
              accessor: factMapping.factAlias,
              label: factMapping.expressionAlias,
              id: factMapping.factAlias + factMapping.expressionAlias,
              dataType: factMapping.className,
              width: factMapping.columnWidth,
              minWidth: 100,
              groupType: factMapping.expressionIdentifier.type,
              cssClasses: "decision-table--input",
              isRowIndexColumn: false,
            };
          }),
        };
      }),
    };

    return [descriptionSection, givenSection, expectSection];
  }, [simulationData.scesimModelDescriptor.factMappings]);

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
      columns={simulationColumns}
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
