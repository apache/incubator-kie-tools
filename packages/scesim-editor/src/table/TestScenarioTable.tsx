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
  SceSim__ScenarioType,
  SceSim__simulationType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { BeeTableHeaderVisibility } from "@kie-tools/boxed-expression-component/dist/api/BeeTable";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";
import { StandaloneBeeTable, getColumnsAtLastLevel } from "@kie-tools/boxed-expression-component/dist/table/BeeTable";

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
      if (factMapping.expressionIdentifier.type?.__$$text === "GIVEN") {
        givenFactMappingToFactMap.set(
          { factName: factMapping.factAlias.__$$text, factType: factMapping.factIdentifier!.className!.__$$text },
          [
            ...(givenFactMappingToFactMap.get({
              factName: factMapping.factAlias.__$$text,
              factType: factMapping.factIdentifier!.className!.__$$text,
            }) ?? []),
            factMapping,
          ]
        );
      } else if (factMapping.expressionIdentifier.type!.__$$text === "EXPECT") {
        expectFactMappingToFactMap.set(
          { factName: factMapping.factAlias.__$$text, factType: factMapping.factIdentifier!.className!.__$$text },
          [
            ...(expectFactMappingToFactMap.get({
              factName: factMapping.factAlias.__$$text,
              factType: factMapping.factIdentifier!.className!.__$$text,
            }) ?? []),
            factMapping,
          ]
        );
      } else if (
        factMapping.expressionIdentifier.type!.__$$text === "OTHER" &&
        factMapping.expressionIdentifier.name!.__$$text === "Description"
      ) {
        descriptionFactMapping = factMapping;
      }
    });

    const descriptionSection = {
      groupType: descriptionFactMapping!.expressionIdentifier.type!.__$$text,
      id: descriptionFactMapping!.expressionIdentifier.name!.__$$text,
      accessor: descriptionFactMapping!.expressionIdentifier.name!.__$$text,
      label: descriptionFactMapping!.factAlias.__$$text,
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      width: descriptionFactMapping!.columnWidth?.__$$text ?? 300,
      minWidth: descriptionFactMapping!.columnWidth?.__$$text ?? 300,
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
          dataType: entry[0].factType != "java.lang.Void" ? entry[0].factType : "",
          groupType: "GIVEN",
          cssClasses: "decision-table--input",
          isRowIndexColumn: false,
          columns: entry[1].map((factMapping) => {
            return {
              accessor: factMapping.expressionIdentifier.name!.__$$text,
              label: factMapping.expressionAlias!.__$$text,
              id: factMapping.expressionIdentifier.name!.__$$text + "GIVEN",
              dataType: factMapping.className!.__$$text != "java.lang.Void" ? factMapping.className!.__$$text : "",
              width: factMapping.columnWidth?.__$$text ?? 150,
              minWidth: 150,
              groupType: factMapping.expressionIdentifier.type!.__$$text,
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
          dataType: entry[0].factType != "java.lang.Void" ? entry[0].factType : "",
          groupType: "EXPECT",
          cssClasses: "decision-table--input",
          isRowIndexColumn: false,
          columns: entry[1].map((factMapping) => {
            return {
              accessor: factMapping.expressionIdentifier.name!.__$$text,
              label: factMapping.expressionAlias!.__$$text,
              id: factMapping.expressionIdentifier.name!.__$$text,
              dataType: factMapping.className!.__$$text != "java.lang.Void" ? factMapping.className!.__$$text : "",
              width: factMapping.columnWidth?.__$$text ?? 150,
              minWidth: 150,
              groupType: factMapping.expressionIdentifier.type!.__$$text,
              cssClasses: "decision-table--input",
              isRowIndexColumn: false,
            };
          }),
        };
      }),
    };

    console.log(descriptionSection);

    console.log(givenSection);

    return [descriptionSection, givenSection, expectSection];
  }, [simulationData.scesimModelDescriptor.factMappings]);

  const simulationRows = useMemo(
    () =>
      (simulationData.scesimData.Scenario ?? []).map((scenario) => {
        console.log("*=====*");
        console.log(scenario);
        const factMappingValues = scenario.factMappingValues.FactMappingValue ?? [];
        console.log(factMappingValues);
        console.log("*=====*");

        const tableRow = getColumnsAtLastLevel(simulationColumns, 2).reduce(
          (tableRow: ROWTYPE, column: ReactTable.Column<ROWTYPE>, columnIndex) => {
            console.log("accessor:" + column.accessor);
            console.log(tableRow[column.accessor as string]);
            console.log(factMappingValues[columnIndex]);
            console.log(columnIndex);
            console.log("=====");

            tableRow[column.accessor as string] = factMappingValues[columnIndex].rawValue?.__$$text ?? "";
            return tableRow;
          }
        );
        return tableRow;
      }),
    [simulationColumns, simulationData.scesimData.Scenario]
  );

  console.log(simulationRows);

  return (
    <StandaloneBeeTable
      scrollableParentRef={tableScrollableElementRef.current}
      allowedOperations={() => []}
      getColumnKey={undefined}
      getRowKey={undefined}
      tableId={undefined}
      isEditableHeader={true}
      headerLevelCountForAppendingRowIndexColumn={2}
      headerVisibility={BeeTableHeaderVisibility.AllLevels}
      operationConfig={undefined}
      columns={simulationColumns} //OK
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
