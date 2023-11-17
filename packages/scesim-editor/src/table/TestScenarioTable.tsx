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
import { useCallback, useEffect, useMemo, useRef } from "react";

import * as ReactTable from "react-table";
import _ from "lodash";

import {
  SceSim__FactMappingType,
  SceSim__simulationType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import {
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
} from "@kie-tools/boxed-expression-component/dist/api/BeeTable";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";
import { StandaloneBeeTable, getColumnsAtLastLevel } from "@kie-tools/boxed-expression-component/dist/table/BeeTable";

import { useTestScenarioEditorI18n } from "../i18n";
import { TestScenarioType } from "../TestScenarioEditor";

function TestScenarioTable({
  assetType,
  onRowAdded,
  onRowDeleted,
  simulationData,
}: {
  assetType: string;
  onRowAdded: (args: { beforeIndex: number }) => void;
  onRowDeleted: (args: { rowIndex: number }) => void;
  simulationData: SceSim__simulationType;
}) {
  enum SimulationTableColumnGroup {
    Expect = "EXPECT",
    Given = "GIVEN",
    Other = "OTHER",
  }

  type ROWTYPE = any; // FIXME: https://github.com/kiegroup/kie-issues/issues/169

  const { i18n } = useTestScenarioEditorI18n();

  const tableScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });

  useEffect(() => {
    tableScrollableElementRef.current.current = document.querySelector(".kie-scesim-editor--table-container") ?? null;
  }, []);

  const simulationColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    const givenFactMappingToFactMap: Map<{ factName: string; factType: string }, SceSim__FactMappingType[]> = new Map();
    const expectFactMappingToFactMap: Map<{ factName: string; factType: string }, SceSim__FactMappingType[]> =
      new Map();
    let descriptionFactMapping: SceSim__FactMappingType | undefined;

    (simulationData?.scesimModelDescriptor?.factMappings?.FactMapping ?? []).forEach((factMapping) => {
      if (factMapping.expressionIdentifier.type?.__$$text === SimulationTableColumnGroup.Given) {
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
      } else if (factMapping.expressionIdentifier.type!.__$$text === SimulationTableColumnGroup.Expect) {
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
        factMapping.expressionIdentifier.type!.__$$text === SimulationTableColumnGroup.Other &&
        factMapping.expressionIdentifier.name!.__$$text === "Description"
      ) {
        descriptionFactMapping = factMapping;
      }
    });

    const descriptionSection: ReactTable.Column<ROWTYPE> = {
      accessor: descriptionFactMapping!.expressionIdentifier.name!.__$$text,
      groupType: descriptionFactMapping!.expressionIdentifier.type!.__$$text,
      id: descriptionFactMapping!.expressionIdentifier.name!.__$$text,
      isRowIndexColumn: false,
      label: descriptionFactMapping!.factAlias.__$$text,
      minWidth: descriptionFactMapping!.columnWidth?.__$$text ?? 300,
      width: descriptionFactMapping!.columnWidth?.__$$text ?? 300,
    };

    const givenSection = {
      accessor: SimulationTableColumnGroup.Given,
      groupType: SimulationTableColumnGroup.Given,
      id: SimulationTableColumnGroup.Given,
      isRowIndexColumn: false,
      label: SimulationTableColumnGroup.Given,
      columns: [...givenFactMappingToFactMap.entries()].map((entry) => {
        return {
          accessor: entry[0].factName,
          dataType: entry[0].factType != "java.lang.Void" ? entry[0].factType : undefined,
          groupType: SimulationTableColumnGroup.Given,
          id: entry[0].factName,
          isRowIndexColumn: false,
          label: entry[0].factName,
          columns: entry[1].map((factMapping) => {
            return {
              accessor: factMapping.expressionIdentifier.name!.__$$text,
              dataType:
                factMapping.className!.__$$text != "java.lang.Void" ? factMapping.className!.__$$text : undefined,
              groupType: factMapping.expressionIdentifier.type!.__$$text,
              label: factMapping.expressionAlias!.__$$text,
              id: factMapping.expressionIdentifier.name!.__$$text,
              isRowIndexColumn: false,
              minWidth: 150,
              width: factMapping.columnWidth?.__$$text ?? 150,
            };
          }),
        };
      }),
    };

    const expectSection = {
      accessor: SimulationTableColumnGroup.Expect,
      groupType: SimulationTableColumnGroup.Expect,
      id: SimulationTableColumnGroup.Expect,
      isRowIndexColumn: false,
      label: SimulationTableColumnGroup.Expect,
      columns: [...expectFactMappingToFactMap.entries()].map((entry) => {
        return {
          accessor: entry[0].factName,
          dataType: entry[0].factType != "java.lang.Void" ? entry[0].factType : undefined,
          groupType: SimulationTableColumnGroup.Expect,
          id: entry[0].factName,
          isRowIndexColumn: false,
          label: entry[0].factName,
          columns: entry[1].map((factMapping) => {
            return {
              accessor: factMapping.expressionIdentifier.name!.__$$text,
              dataType:
                factMapping.className!.__$$text != "java.lang.Void" ? factMapping.className!.__$$text : undefined,
              groupType: factMapping.expressionIdentifier.type!.__$$text,
              id: factMapping.expressionIdentifier.name!.__$$text,
              isRowIndexColumn: false,
              label: factMapping.expressionAlias!.__$$text,
              minWidth: 150,
              width: factMapping.columnWidth?.__$$text ?? 150,
            };
          }),
        };
      }),
    };

    return [descriptionSection, givenSection, expectSection];
  }, [SimulationTableColumnGroup, simulationData.scesimModelDescriptor.factMappings?.FactMapping]);

  const simulationRows = useMemo(
    () =>
      (simulationData.scesimData.Scenario ?? []).map((scenario, index) => {
        const factMappingValues = scenario.factMappingValues.FactMappingValue ?? [];

        const tableRow = getColumnsAtLastLevel(simulationColumns, 2).reduce(
          (tableRow: ROWTYPE, column: ReactTable.Column<ROWTYPE>) => {
            const factMappingValue = factMappingValues.filter(
              (fmv) => fmv.expressionIdentifier.name?.__$$text === column.accessor
            );
            tableRow[column.accessor] = factMappingValue[0]?.rawValue?.__$$text ?? "";
            return tableRow;
          },
          { id: index }
        );
        return tableRow;
      }),
    [simulationColumns, simulationData.scesimData.Scenario]
  );

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
        return [];
      }

      const columnIndex = conditions.selection.selectionStart.columnIndex;

      const atLeastTwoColumnsOfTheSameGroupType = conditions.column?.groupType
        ? _.groupBy(conditions.columns, (column) => column?.groupType)[conditions.column.groupType].length > 1
        : true;

      const columnCanBeDeleted =
        columnIndex > 0 &&
        atLeastTwoColumnsOfTheSameGroupType &&
        (conditions.columns?.length ?? 0) > 2 && // That's a regular column and the rowIndex column
        (conditions.column?.columns?.length ?? 0) <= 0;

      const columnOperations =
        columnIndex in [0, 1] // This is the rowIndex column
          ? []
          : [
              BeeTableOperation.ColumnInsertLeft,
              BeeTableOperation.ColumnInsertRight,
              BeeTableOperation.ColumnInsertN,
              ...(columnCanBeDeleted ? [BeeTableOperation.ColumnDelete] : []),
            ];

      return [
        ...(columnIndex >= 0 && conditions.selection.selectionStart.rowIndex < 0 ? columnOperations : []),
        ...(conditions.selection.selectionStart.rowIndex >= 0 && columnIndex > 0
          ? [
              BeeTableOperation.SelectionCopy,
              BeeTableOperation.SelectionCut,
              BeeTableOperation.SelectionPaste,
              BeeTableOperation.SelectionReset,
            ]
          : []),
        ...(conditions.selection.selectionStart.rowIndex >= 0
          ? [
              BeeTableOperation.RowInsertAbove,
              BeeTableOperation.RowInsertBelow,
              BeeTableOperation.RowInsertN,
              ...(simulationRows.length > 1 ? [BeeTableOperation.RowDelete] : []),
              BeeTableOperation.RowReset,
              BeeTableOperation.RowDuplicate,
            ]
          : []),
      ];
    },
    [simulationRows.length]
  );

  const generateOperationConfig = useCallback(
    (groupName: string) => [
      {
        group: groupName,
        items: [
          { name: i18n.table.insertLeft, type: BeeTableOperation.ColumnInsertLeft },
          { name: i18n.table.insertRight, type: BeeTableOperation.ColumnInsertRight },
          { name: i18n.table.insert, type: BeeTableOperation.ColumnInsertN },
          { name: i18n.table.delete, type: BeeTableOperation.ColumnDelete },
        ],
      },
      {
        group: i18n.table.simulation.singleEntry.toUpperCase(),
        items: [
          { name: i18n.table.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.table.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.table.insert, type: BeeTableOperation.RowInsertN },
          { name: i18n.table.delete, type: BeeTableOperation.RowDelete },
          { name: i18n.table.duplicate, type: BeeTableOperation.RowDuplicate },
        ],
      },
      {
        group: i18n.table.selection.toUpperCase(),
        items: [
          { name: i18n.table.copy, type: BeeTableOperation.SelectionCopy },
          { name: i18n.table.cut, type: BeeTableOperation.SelectionCut },
          { name: i18n.table.paste, type: BeeTableOperation.SelectionPaste },
          { name: i18n.table.reset, type: BeeTableOperation.SelectionReset },
        ],
      },
    ],
    [i18n]
  );

  const simulationOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    const config: BeeTableOperationConfig = {};
    config[""] = generateOperationConfig("");
    config[SimulationTableColumnGroup.Expect] = generateOperationConfig(SimulationTableColumnGroup.Expect);
    config[SimulationTableColumnGroup.Given] = generateOperationConfig(SimulationTableColumnGroup.Given);
    config[SimulationTableColumnGroup.Other] = generateOperationConfig(SimulationTableColumnGroup.Other);
    return config;
  }, [SimulationTableColumnGroup, generateOperationConfig]);

  return (
    <StandaloneBeeTable
      allowedOperations={allowedOperations}
      columns={simulationColumns}
      enableKeyboardNavigation={true}
      headerLevelCountForAppendingRowIndexColumn={2}
      headerVisibility={BeeTableHeaderVisibility.AllLevels}
      isEditableHeader={assetType === TestScenarioType[TestScenarioType.RULE]}
      isReadOnly={false}
      onRowAdded={onRowAdded}
      onRowDeleted={onRowDeleted}
      operationConfig={simulationOperationConfig}
      resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
      rows={simulationRows}
      scrollableParentRef={tableScrollableElementRef.current}
      shouldRenderRowIndexColumn={true}
      shouldShowColumnsInlineControls={true}
      shouldShowRowsInlineControls={true}
    />
  );
}

export default TestScenarioTable;
