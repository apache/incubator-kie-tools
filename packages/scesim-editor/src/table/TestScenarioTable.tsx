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
import { useCallback, useMemo } from "react";

import * as ReactTable from "react-table";
import _ from "lodash";

import {
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  InsertRowColumnsDirection,
} from "@kie-tools/boxed-expression-component/dist/api/BeeTable";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";
import {
  BeeTableCellUpdate,
  StandaloneBeeTable,
  getColumnsAtLastLevel,
} from "@kie-tools/boxed-expression-component/dist/table/BeeTable";

import {
  SceSim__backgroundDatasType,
  SceSim__backgroundType,
  SceSim__FactMappingType,
  SceSim__scenariosType,
  SceSim__simulationType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { useTestScenarioEditorI18n } from "../i18n";
import { useTestScenarioEditorStore, useTestScenarioEditorStoreApi } from "../store/TestScenarioStoreContext";
import { addColumnWithEmptyInstanceAndProperty, addColumnWithEmptyProperty } from "../mutations/addColumn";
import { deleteColumn } from "../mutations/deleteColumn";

import "./TestScenarioTable.css";
import { addRow } from "../mutations/addRow";
import { deleteRow } from "../mutations/deleteRow";
import { dupliacteRow } from "../mutations/duplicateRow";
import { updateCell } from "../mutations/updateCell";
import { updateColumnWidth } from "../mutations/updateColumnWidth";

function TestScenarioTable({
  tableData,
  scrollableParentRef,
}: {
  tableData: SceSim__simulationType | SceSim__backgroundType;
  scrollableParentRef: React.RefObject<HTMLElement>;
}) {
  enum TestScenarioTableColumnHeaderGroup {
    EXPECT = "expect-header",
    GIVEN = "given-header",
  }
  enum TestScenarioTableColumnInstanceGroup {
    EXPECT = "expect-instance",
    GIVEN = "given-instance",
  }
  enum TestScenarioTableColumnFieldGroup {
    EXPECT = "expect",
    GIVEN = "given",
    OTHER = "other",
  }

  type ROWTYPE = any; // FIXME: https://github.com/apache/incubator-kie-issues/issues/169

  const { i18n } = useTestScenarioEditorI18n();
  const testScenarioEditorStoreApi = useTestScenarioEditorStoreApi();
  const settingsModel = useTestScenarioEditorStore((state) => state.scesim.model.ScenarioSimulationModel.settings);
  const testScenarioType = settingsModel.type?.__$$text.toUpperCase();

  /** BACKGROUND TABLE MANAGMENT */

  const isBackground = useMemo(() => {
    return "BackgroundData" in tableData.scesimData;
  }, [tableData]);

  const columnIndexStart = useMemo(() => {
    return isBackground ? 0 : 1;
  }, [isBackground]);

  const retrieveRowsData = useCallback(
    (rowData: SceSim__backgroundDatasType | SceSim__scenariosType) => {
      if (isBackground) {
        return (rowData as SceSim__backgroundDatasType).BackgroundData;
      } else {
        return (rowData as SceSim__scenariosType).Scenario;
      }
    },
    [isBackground]
  );

  /** TABLE COLUMNS AND ROWS POPULATION */

  /* It determines the Data Type Label based on the given Data Type.
     In case of RULE Scenario, the Data Type is a FQCN (eg. java.lang.String). So, the label will take the class name only
     In any case, if the Data Type ends with a "Void", that means the type has not been assigned, so we show Undefined. */
  const determineDataTypeLabel = useCallback(
    (dataType: string, genericTypes: string[]) => {
      let dataTypeLabel = dataType;
      if (testScenarioType === "RULE") {
        dataTypeLabel = dataTypeLabel.split(".").pop() ?? dataTypeLabel;
      }
      /* List Type */
      if (genericTypes.length == 1) {
        dataTypeLabel = testScenarioType === "RULE" ? `${dataTypeLabel}<${genericTypes[0]}>` : `${genericTypes[0]}[]`;
      }
      /* Map Type */
      if (testScenarioType === "RULE" && genericTypes.length == 2) {
        dataTypeLabel = `${dataTypeLabel}<${genericTypes[0]},${genericTypes[1]}>`;
      }
      return !dataTypeLabel || dataTypeLabel.endsWith("Void") ? "<Undefined>" : dataTypeLabel;
    },
    [testScenarioType]
  );

  /* It updates any column width change in the Model */
  const setColumnWidth = useCallback(
    (inputIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      testScenarioEditorStoreApi.setState((state) => {
        const factMappings = isBackground
          ? state.scesim.model.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping!
          : state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
        const oldWidth = factMappings[inputIndex].columnWidth?.__$$text;
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(oldWidth) : newWidthAction;

        updateColumnWidth({
          factMappings: factMappings,
          columnIndex: inputIndex,
          newWidth: newWidth,
          oldWidth: oldWidth,
        });
      });
    },
    [isBackground, testScenarioEditorStoreApi]
  );

  /* It determines the column data based on the given FactMapping (Scesim column representation).
     In case of the Description column, the behavior is slightly different (column dimension, label and no datatype label) 
  */
  const generateColumnFromFactMapping = useCallback(
    (factMapping: SceSim__FactMappingType, factMappingIndex: number, isDescriptionColumn?: boolean) => {
      return {
        accessor: factMapping.expressionIdentifier.name!.__$$text,
        dataType:
          isDescriptionColumn || factMapping.factMappingValueType?.__$$text === "EXPRESSION"
            ? undefined
            : determineDataTypeLabel(
                factMapping.className.__$$text,
                factMapping.genericTypes?.string?.map((genericType) => genericType.__$$text) ?? []
              ),
        groupType: factMapping.expressionIdentifier.type!.__$$text.toLowerCase(),
        id: factMapping!.expressionIdentifier.name!.__$$text,
        isRowIndexColumn: false,
        label: isDescriptionColumn ? factMapping.factAlias.__$$text : factMapping.expressionAlias!.__$$text,
        minWidth: isDescriptionColumn ? 300 : 100,
        setWidth: setColumnWidth(factMappingIndex),
        width: factMapping.columnWidth?.__$$text ?? (isDescriptionColumn ? 300 : factMapping.columnWidth?.__$$text),
      };
    },
    [determineDataTypeLabel, setColumnWidth]
  );

  /* It determines the Instance Section (the header row in the middle) based on the given FactMapping (Scesim column representation)
     and the groupType. An Instance represents the a real implementation of a DMN Type (DMN-based SCESIM) / Java Class (Rule-based Scesim)
  */
  const generateInstanceSectionFromFactMapping = useCallback(
    (factMapping: SceSim__FactMappingType, groupType: TestScenarioTableColumnInstanceGroup) => {
      /* RULE Test Scenarios can have the same instance in both GIVEN and EXPECT section. Therefore, using the following 
         pattern to identify it */
      const instanceID =
        factMapping.expressionIdentifier.type?.__$$text + "." + factMapping.factIdentifier.name!.__$$text;

      return {
        accessor: instanceID,
        dataType: determineDataTypeLabel(factMapping.factIdentifier.className!.__$$text, []),
        groupType: groupType.toLowerCase(),
        id: instanceID,
        isRowIndexColumn: false,
        label: factMapping.factAlias.__$$text,
        columns: [] as ReactTable.Column<ROWTYPE>[],
      };
    },
    [determineDataTypeLabel]
  );

  /**
    It generates the columns of the TestScenarioTable, based on the following logic:
    +---+------------------------------------------------------+----------------------------------------+
    |   |  Description  |  givenSection (given-header)         |   expectSection (expect-header)        |
    |   |               +--------------------------------+-----+----------------------------------+-----+
    | # |               | givenInstance (given-instance) | ... | expectGroup (expect-instance)    | ... |
    |   |               +----------------+---------------+-----+----------------------------------+-----+
    |   |               | field (given)  | field (given) | ... | field (expect)  | field  (expect)| ... |
    +---+---------------+----------------+---------------+-----+-----------------+----------------+-----+
    Every section has its related groupType in the rounded brackets, that are crucial to determine 
    the correct context menu behavior (adding/removing an instance requires a different logic than
    adding/removing a field)
    Background table shows the givenSection only.
    The returned object contains all the columns in the above structure and the instancesGroup (givenInstance + expectInstance)
    required to correctly manage the instance header's context menu behavior.
   */

  const tableColumns = useMemo<{
    allColumns: ReactTable.Column<ROWTYPE>[];
    instancesGroup: ReactTable.Column<ROWTYPE>[];
  }>(() => {
    const descriptionColumns: ReactTable.Column<ROWTYPE>[] = [];
    const givenInstances: ReactTable.Column<ROWTYPE>[] = [];
    const expectInstances: ReactTable.Column<ROWTYPE>[] = [];

    (tableData.scesimModelDescriptor.factMappings.FactMapping ?? []).forEach((factMapping, index) => {
      /* RULE Test Scenarios can have the same instance in both GIVEN and EXPECT section. Therefore, using the following 
         pattern to identify it */
      const instanceID =
        factMapping.expressionIdentifier.type?.__$$text + "." + factMapping.factIdentifier.name!.__$$text;
      if (factMapping.expressionIdentifier.type?.__$$text === TestScenarioTableColumnFieldGroup.GIVEN.toUpperCase()) {
        const instance = givenInstances.find((instanceColumn) => instanceColumn.id === instanceID);
        if (instance) {
          instance.columns?.push(generateColumnFromFactMapping(factMapping, index));
        } else {
          const newInstance = generateInstanceSectionFromFactMapping(
            factMapping,
            TestScenarioTableColumnInstanceGroup.GIVEN
          );
          newInstance.columns.push(generateColumnFromFactMapping(factMapping, index));
          givenInstances.push(newInstance);
        }
      } else if (
        factMapping.expressionIdentifier.type!.__$$text === TestScenarioTableColumnFieldGroup.EXPECT.toUpperCase()
      ) {
        const instance = expectInstances.find((instanceColumn) => instanceColumn.id === instanceID);
        if (instance) {
          instance.columns?.push(generateColumnFromFactMapping(factMapping, index));
        } else {
          const newInstance = generateInstanceSectionFromFactMapping(
            factMapping,
            TestScenarioTableColumnInstanceGroup.EXPECT
          );
          newInstance.columns.push(generateColumnFromFactMapping(factMapping, index));
          expectInstances.push(newInstance);
        }
      } else if (
        factMapping.expressionIdentifier.type!.__$$text === TestScenarioTableColumnFieldGroup.OTHER.toUpperCase() &&
        factMapping.expressionIdentifier.name!.__$$text === "Description"
      ) {
        descriptionColumns.push(generateColumnFromFactMapping(factMapping, index, true));
      }
    });

    const givenSection = [
      {
        accessor: TestScenarioTableColumnHeaderGroup.GIVEN,
        groupType: TestScenarioTableColumnHeaderGroup.GIVEN,
        id: TestScenarioTableColumnHeaderGroup.GIVEN,
        isRowIndexColumn: false,
        label: i18n.table.given.toUpperCase(),
        columns: givenInstances,
      },
    ];

    const expectSection =
      expectInstances.length > 0
        ? [
            {
              accessor: TestScenarioTableColumnHeaderGroup.EXPECT,
              groupType: TestScenarioTableColumnHeaderGroup.EXPECT,
              id: TestScenarioTableColumnHeaderGroup.EXPECT,
              isRowIndexColumn: false,
              label: i18n.table.expect.toUpperCase(),
              columns: expectInstances,
            },
          ]
        : [];

    return {
      allColumns: [...descriptionColumns, ...givenSection, ...expectSection],
      instancesGroup: [...givenInstances, ...expectInstances],
    };
  }, [
    generateColumnFromFactMapping,
    generateInstanceSectionFromFactMapping,
    i18n,
    TestScenarioTableColumnHeaderGroup,
    TestScenarioTableColumnFieldGroup,
    TestScenarioTableColumnInstanceGroup,
    tableData.scesimModelDescriptor.factMappings.FactMapping,
  ]);

  /* It generates the columns of the TestScenarioTable */
  const tableRows = useMemo(
    () =>
      (retrieveRowsData(tableData.scesimData) ?? []).map((scenario, index) => {
        const factMappingValues = scenario.factMappingValues.FactMappingValue ?? [];

        const tableRow = getColumnsAtLastLevel(tableColumns.allColumns, 2).reduce(
          (tableRow: ROWTYPE, column: ReactTable.Column<ROWTYPE>) => {
            const factMappingValue = factMappingValues.filter(
              (fmv) => fmv.expressionIdentifier.name!.__$$text === column.accessor
            );
            tableRow[column.accessor] = factMappingValue[0]?.rawValue?.__$$text ?? "";
            return tableRow;
          },
          { id: index }
        );
        return tableRow;
      }),
    [retrieveRowsData, tableColumns.allColumns, tableData.scesimData]
  );

  /** TABLE'S CONTEXT MENU MANAGEMENT */

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      const isHeader =
        conditions.column?.groupType === TestScenarioTableColumnHeaderGroup.EXPECT ||
        conditions.column?.groupType === TestScenarioTableColumnHeaderGroup.GIVEN;
      const isInstance =
        conditions.column?.groupType === TestScenarioTableColumnInstanceGroup.EXPECT ||
        conditions.column?.groupType === TestScenarioTableColumnInstanceGroup.GIVEN;
      const isOther = conditions.column?.groupType === TestScenarioTableColumnFieldGroup.OTHER;

      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd || isHeader) {
        return [];
      }

      const columnIndex = conditions.selection.selectionStart.columnIndex;

      const columnCanBeDeleted =
        !isOther &&
        columnIndex > 0 &&
        ((isBackground && (conditions.columns?.length ?? 0) > 1) ||
          (!isBackground && columnIndex > 0 && (conditions.columns?.length ?? 0) > 2));

      const columnsWithNoOperations = isBackground ? [0] : [0, 1];
      const columnOperations = (isInstance ? columnIndex in [0] : columnIndex in columnsWithNoOperations)
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
        ...(conditions.selection.selectionStart.rowIndex >= 0 && !isBackground
          ? [
              BeeTableOperation.RowInsertAbove,
              BeeTableOperation.RowInsertBelow,
              BeeTableOperation.RowInsertN,
              BeeTableOperation.RowDelete,
              BeeTableOperation.RowReset,
              BeeTableOperation.RowDuplicate,
            ]
          : []),
      ];
    },
    [
      TestScenarioTableColumnHeaderGroup,
      TestScenarioTableColumnInstanceGroup,
      TestScenarioTableColumnFieldGroup,
      isBackground,
    ]
  );

  const generateOperationConfig = useCallback(
    (groupName: string) => {
      const isInstance =
        groupName === TestScenarioTableColumnInstanceGroup.EXPECT ||
        groupName === TestScenarioTableColumnInstanceGroup.GIVEN;

      const groupLabel = (!isInstance ? i18n.table.field : i18n.table.instance).toUpperCase();

      return [
        {
          group: groupLabel,
          items: [
            {
              name: isInstance ? i18n.table.insertLeftInstance : i18n.table.insertLeftField,
              type: BeeTableOperation.ColumnInsertLeft,
            },
            {
              name: isInstance ? i18n.table.insertRightInstance : i18n.table.insertRightField,
              type: BeeTableOperation.ColumnInsertRight,
            },
            { name: i18n.table.insert, type: BeeTableOperation.ColumnInsertN },
            {
              name: isInstance ? i18n.table.deleteInstance : i18n.table.deleteField,
              type: BeeTableOperation.ColumnDelete,
            },
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
      ];
    },
    [TestScenarioTableColumnInstanceGroup, i18n]
  );

  const simulationOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    const config: BeeTableOperationConfig = {};
    config[""] = generateOperationConfig("");
    config[TestScenarioTableColumnHeaderGroup.EXPECT] = generateOperationConfig("");
    config[TestScenarioTableColumnHeaderGroup.GIVEN] = generateOperationConfig("");
    config[TestScenarioTableColumnInstanceGroup.EXPECT] = generateOperationConfig(
      TestScenarioTableColumnInstanceGroup.EXPECT
    );
    config[TestScenarioTableColumnInstanceGroup.GIVEN] = generateOperationConfig(
      TestScenarioTableColumnInstanceGroup.GIVEN
    );
    config[TestScenarioTableColumnFieldGroup.EXPECT] = generateOperationConfig(
      TestScenarioTableColumnFieldGroup.EXPECT
    );
    config[TestScenarioTableColumnFieldGroup.GIVEN] = generateOperationConfig(TestScenarioTableColumnFieldGroup.GIVEN);
    config[TestScenarioTableColumnFieldGroup.OTHER] = generateOperationConfig(TestScenarioTableColumnFieldGroup.OTHER);
    return config;
  }, [
    TestScenarioTableColumnFieldGroup,
    TestScenarioTableColumnHeaderGroup,
    TestScenarioTableColumnInstanceGroup,
    generateOperationConfig,
  ]);

  /** TABLE UPDATES FUNCTIONS */

  /**
   * It updates every changed Cell in its related FactMappingValue
   */
  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      cellUpdates.forEach((update) => {
        testScenarioEditorStoreApi.setState((state) => {
          const factMappings = isBackground
            ? state.scesim.model.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping!
            : state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
          const factMappingValuesTypes = isBackground
            ? state.scesim.model.ScenarioSimulationModel.background.scesimData.BackgroundData!
            : state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!;

          updateCell({
            columnIndex: update.columnIndex + columnIndexStart,
            factMappings: factMappings,
            factMappingValuesTypes: factMappingValuesTypes,
            rowIndex: update.rowIndex,
            value: update.value,
          });
        });
      });
    },
    [columnIndexStart, isBackground, testScenarioEditorStoreApi]
  );

  /**
     This logic determines the selected column index in the table's multi-layer headers. This is required because currently
     every layer handles apply a different logic to setting the index. Last-level header follows the natural columns index order, 
     while the 1th and 2th not. The following schema should clarify the current logic:
    +---+------------------------------------------------------+----------------------------------------+
    |   |       1       |                1                     |                 2                      |
    |   |               +--------------------------------+-----+----------------------------------+-----+
    | 0 |               |                1               |  2  |                 3                |  4  |
    |   |               +----------------+---------------+-----+----------------------------------+-----+
    |   |               |       2        |       3       |  4  |       5         |        6       |  7  |
    +---+---------------+----------------+---------------+-----+-----------------+----------------+-----+
    The First layer doesn't perform any operation, so it's not managed here.
   */
  const determineSelectedColumnIndex = useCallback(
    (factMappings: SceSim__FactMappingType[], originalSelectedColumnIndex: number, isInstance: boolean) => {
      if (isInstance) {
        const instanceSectionID = tableColumns.instancesGroup[originalSelectedColumnIndex - 1].id;

        return (
          factMappings.findIndex(
            (factMapping) =>
              factMapping.expressionIdentifier.type?.__$$text + "." + factMapping.factIdentifier.name!.__$$text ===
              instanceSectionID
          ) ?? -1
        );
      }

      /* In case of background, the rowIndex column is not present */
      return originalSelectedColumnIndex - (isBackground ? 1 : 0);
    },
    [isBackground, tableColumns.instancesGroup]
  );

  /* It determines in which index position a column should be added. In case of a field, the new column index is simply
   in the right or in the left of the selected column. In case of a new instance, it's required to find the first column
   index outside the selected Instance group. */
  const determineNewColumnTargetIndex = (
    factMappings: SceSim__FactMappingType[],
    insertDirection: InsertRowColumnsDirection,
    isInstance: boolean,
    selectedColumnIndex: number,
    selectedFactMapping: SceSim__FactMappingType
  ) => {
    const groupType = selectedFactMapping.expressionIdentifier.type!.__$$text;
    const instanceName = selectedFactMapping.factIdentifier.name!.__$$text;
    const instanceType = selectedFactMapping.factIdentifier.className!.__$$text;

    if (!isInstance) {
      if (insertDirection === InsertRowColumnsDirection.AboveOrRight) {
        return selectedColumnIndex + 1;
      } else {
        return selectedColumnIndex;
      }
    }

    let newColumnTargetColumn = -1;

    if (insertDirection === InsertRowColumnsDirection.AboveOrRight) {
      for (let i = selectedColumnIndex; i < factMappings.length; i++) {
        const currentFM = factMappings[i];
        if (
          currentFM.expressionIdentifier.type!.__$$text === groupType &&
          currentFM.factIdentifier.name?.__$$text === instanceName &&
          currentFM.factIdentifier.className?.__$$text === instanceType
        ) {
          if (i == factMappings.length - 1) {
            newColumnTargetColumn = i + 1;
          }
        } else {
          newColumnTargetColumn = i;
          break;
        }
      }
    } else {
      for (let i = selectedColumnIndex; i >= 0; i--) {
        const currentFM = factMappings[i];

        if (
          currentFM.expressionIdentifier.type!.__$$text === groupType &&
          currentFM.factIdentifier.name?.__$$text === instanceName &&
          currentFM.factIdentifier.className?.__$$text === instanceType
        ) {
          if (i == 0) {
            newColumnTargetColumn = 0;
          }
        } else {
          newColumnTargetColumn = i + 1;
          break;
        }
      }
    }

    return newColumnTargetColumn;
  };

  /**
   * It adds a new FactMapping (Column) in the Model Descriptor structure and adds the new column related FactMapping Value (Cell)
   */
  const onColumnAdded = useCallback(
    (args: {
      beforeIndex: number;
      currentIndex: number;
      groupType: string;
      columnsCount: number;
      insertDirection: InsertRowColumnsDirection;
    }) => {
      /* GIVEN and EXPECTED of FIELD and INSTANCE column types can be added only */
      if (
        TestScenarioTableColumnFieldGroup.OTHER === args.groupType ||
        TestScenarioTableColumnHeaderGroup.EXPECT === args.groupType ||
        TestScenarioTableColumnHeaderGroup.GIVEN === args.groupType
      ) {
        console.error("Can't add a " + args.groupType + " type column.");
        return;
      }
      const isInstance =
        args.groupType === TestScenarioTableColumnInstanceGroup.EXPECT ||
        args.groupType === TestScenarioTableColumnInstanceGroup.GIVEN;

      testScenarioEditorStoreApi.setState((state) => {
        const factMappings = isBackground
          ? state.scesim.model.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping!
          : state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
        const factMappingValues = isBackground
          ? state.scesim.model.ScenarioSimulationModel.background.scesimData.BackgroundData!
          : state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!;
        const selectedColumnFactMappingIndex = determineSelectedColumnIndex(
          factMappings,
          args.currentIndex,
          isInstance
        );

        const selectedColumnFactMapping = factMappings[selectedColumnFactMappingIndex];
        const targetColumnIndex = determineNewColumnTargetIndex(
          factMappings,
          args.insertDirection,
          isInstance,
          selectedColumnFactMappingIndex,
          selectedColumnFactMapping
        );
        const isNewInstance =
          isInstance || selectedColumnFactMapping.factIdentifier.className?.__$$text === "java.lang.Void";

        for (let columnIndex = 0; columnIndex < args.columnsCount; columnIndex++) {
          isNewInstance
            ? addColumnWithEmptyInstanceAndProperty({
                expressionIdentifierType: selectedColumnFactMapping.expressionIdentifier.type!.__$$text,
                factMappings: factMappings,
                factMappingValuesTypes: factMappingValues,
                targetColumnIndex: targetColumnIndex + columnIndex,
              })
            : addColumnWithEmptyProperty({
                expressionElementsSteps: [
                  selectedColumnFactMapping.expressionElements!.ExpressionElement![0].step.__$$text,
                ],
                expressionIdentifierType: selectedColumnFactMapping.expressionIdentifier.type!.__$$text,
                factAlias: selectedColumnFactMapping.factAlias.__$$text,
                factIdentifierClassName: selectedColumnFactMapping.factIdentifier.className!.__$$text,
                factIdentifierName: selectedColumnFactMapping.factIdentifier.name!.__$$text,
                factMappings: factMappings,
                factMappingValuesTypes: factMappingValues,
                targetColumnIndex: targetColumnIndex + columnIndex,
              });
        }
      });
    },
    [
      determineSelectedColumnIndex,
      isBackground,
      testScenarioEditorStoreApi,
      TestScenarioTableColumnFieldGroup,
      TestScenarioTableColumnHeaderGroup,
      TestScenarioTableColumnInstanceGroup,
    ]
  );

  /**
   * It removes a FactMapping (Column) at the given column index toghter with its related Data Cells.
   */
  const onColumnDeleted = useCallback(
    (args: { columnIndex: number; groupType: string }) => {
      /* GIVEN and EXPECTED of FIELD and INSTANCE column types can be deleted only */
      if (
        TestScenarioTableColumnFieldGroup.OTHER === args.groupType ||
        TestScenarioTableColumnHeaderGroup.EXPECT === args.groupType ||
        TestScenarioTableColumnHeaderGroup.GIVEN === args.groupType
      ) {
        console.error("Can't delete a " + args.groupType + " type column.");
        return;
      }
      const isInstance =
        args.groupType === TestScenarioTableColumnInstanceGroup.EXPECT ||
        args.groupType === TestScenarioTableColumnInstanceGroup.GIVEN;

      testScenarioEditorStoreApi.setState((state) => {
        const factMappings = isBackground
          ? state.scesim.model.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping!
          : state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
        const factMappingValues = isBackground
          ? state.scesim.model.ScenarioSimulationModel.background.scesimData.BackgroundData!
          : state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!;
        const factMappingIndexToRemove = determineSelectedColumnIndex(factMappings, args.columnIndex + 1, isInstance);
        const factMappingExpressionIdentifierTypeToRemove =
          factMappings[factMappingIndexToRemove].expressionIdentifier.type!.__$$text;

        const { deletedFactMappingIndexs } = deleteColumn({
          factMappingIndexToRemove: factMappingIndexToRemove,
          factMappings: factMappings,
          factMappingValues: factMappingValues,
          isBackground: isBackground,
          isInstance: isInstance,
          selectedColumnIndex: args.columnIndex,
        });

        /* If the last elements of factMappingGroup (i.e. "EXPECT" or "GIVEN") has been removed,
           a new empty Instance must be created */
        const factMappingGroupElementsAfterRemoval = _.groupBy(
          factMappings,
          (factMapping) => factMapping.expressionIdentifier.type!.__$$text
        )[factMappingExpressionIdentifierTypeToRemove];
        const isAtLeastOneGroupElementPresent =
          !!factMappingGroupElementsAfterRemoval && factMappingGroupElementsAfterRemoval.length > 0;

        /* If all element of a group (i.e. "EXPECT" or "GIVEN") are deleted, a new empty column is created for that group */
        if (!isAtLeastOneGroupElementPresent) {
          addColumnWithEmptyInstanceAndProperty({
            expressionIdentifierType: factMappingExpressionIdentifierTypeToRemove,
            factMappings: factMappings,
            factMappingValuesTypes: factMappingValues,
            targetColumnIndex: Math.min(...deletedFactMappingIndexs),
          });
        }

        /** Updating the selectedColumn. When deleting, BEETable automatically shifts the selected cell in the left. */
        const firstRemovedIndex = Math.min(...deletedFactMappingIndexs);
        const selectedColumnIndex = Math.max(0, firstRemovedIndex - 1);

        state.dispatch(state).table.updateSelectedColumn({
          factMapping: _.cloneDeep(factMappings[selectedColumnIndex]),
          index: selectedColumnIndex,
          isBackground: isBackground,
        });
      });
    },
    [
      determineSelectedColumnIndex,
      isBackground,
      testScenarioEditorStoreApi,
      TestScenarioTableColumnFieldGroup,
      TestScenarioTableColumnHeaderGroup,
      TestScenarioTableColumnInstanceGroup,
    ]
  );

  /**
   * It adds a Scenario (Row) at the given row index
   */
  const onRowAdded = useCallback(
    (args: { beforeIndex: number; insertDirection: InsertRowColumnsDirection; rowsCount: number }) => {
      if (isBackground) {
        throw new Error("Impossible state. Background table can have a single row only");
      }
      testScenarioEditorStoreApi.setState((state) => {
        const factMappings =
          state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
        const factMappingValues = state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!;

        for (let rowIndex = 0; rowIndex < args.rowsCount; rowIndex++) {
          addRow({ beforeIndex: args.beforeIndex, factMappings: factMappings, factMappingValues: factMappingValues });
        }
      });
    },
    [isBackground, testScenarioEditorStoreApi]
  );

  /**
   * It deletes a Scenario (Row) at the given row index
   */
  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      if (isBackground) {
        throw new Error("Impossible state. Background table can have a single row only");
      }
      testScenarioEditorStoreApi.setState((state) => {
        const factMappingValues = state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!;

        deleteRow({ rowIndex: args.rowIndex, factMappingValues: factMappingValues });

        /* If all rows (i.e. factMappingValues) have been deleted, a new row is added */
        if (factMappingValues.length === 0) {
          const factMappings =
            state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
          addRow({ beforeIndex: args.rowIndex, factMappings: factMappings, factMappingValues: factMappingValues });
        }
      });
    },
    [isBackground, testScenarioEditorStoreApi]
  );

  /**
   * It duplicates a Scenario (Row) at the given row index
   */
  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      if (isBackground) {
        throw new Error("Impossible state. Background table can have a single row only");
      }
      testScenarioEditorStoreApi.setState((state) => {
        const factMappingValues = state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!;

        dupliacteRow({ rowIndex: args.rowIndex, factMappingValues: factMappingValues });
      });
    },
    [isBackground, testScenarioEditorStoreApi]
  );

  /**
   * Behavior to apply when a DataCell is clicked
   */
  const onDataCellClick = useCallback(
    (_columnID: string) => {
      testScenarioEditorStoreApi.setState((state) => {
        state.dispatch(state).table.updateSelectedColumn(null);
      });
    },
    [testScenarioEditorStoreApi]
  );

  const onHeaderClick = useCallback(
    (columnKey: string) => {
      console.debug("[TestScenarioTable] columnKey: ", columnKey);
      if (
        columnKey == TestScenarioTableColumnHeaderGroup.EXPECT ||
        columnKey == TestScenarioTableColumnHeaderGroup.GIVEN
      ) {
        testScenarioEditorStoreApi.setState((state) => {
          state.dispatch(state).table.updateSelectedColumn(null);
        });
        return;
      }

      const modelDescriptor = isBackground
        ? (tableData as SceSim__backgroundType).scesimModelDescriptor
        : (tableData as SceSim__simulationType).scesimModelDescriptor;

      if (
        columnKey.startsWith(TestScenarioTableColumnFieldGroup.GIVEN.toUpperCase()) ||
        columnKey.toUpperCase().startsWith(TestScenarioTableColumnFieldGroup.EXPECT.toUpperCase())
      ) {
        const selectedInstanceGroup = tableColumns.instancesGroup.find((instance) => instance.id === columnKey);
        if (
          selectedInstanceGroup?.columns?.length === 1 &&
          selectedInstanceGroup?.columns[0].dataType === "<Undefined>"
        ) {
          const propertyID = selectedInstanceGroup?.columns[0].id;
          const selectedFactMapping = modelDescriptor.factMappings.FactMapping!.find(
            (factMapping) => factMapping.expressionIdentifier.name?.__$$text === propertyID
          );
          const selectedFactIndex = selectedFactMapping
            ? modelDescriptor.factMappings.FactMapping!.indexOf(selectedFactMapping!)
            : -1;
          testScenarioEditorStoreApi.setState((state) => {
            state.dispatch(state).table.updateSelectedColumn({
              factMapping: JSON.parse(JSON.stringify(selectedFactMapping)),
              index: selectedFactIndex ?? -1,
              isBackground: isBackground,
            });
          });
        } else {
          testScenarioEditorStoreApi.setState((state) => {
            state.dispatch(state).table.updateSelectedColumn(null);
          });
        }
        return;
      }

      const selectedFactMapping = modelDescriptor.factMappings.FactMapping!.find(
        (factMapping) => factMapping.expressionIdentifier.name?.__$$text == columnKey
      );
      const selectedFactIndex = selectedFactMapping
        ? modelDescriptor.factMappings.FactMapping!.indexOf(selectedFactMapping!)
        : -1;

      testScenarioEditorStoreApi.setState((state) => {
        state.dispatch(state).table.updateSelectedColumn({
          factMapping: JSON.parse(JSON.stringify(selectedFactMapping)),
          index: selectedFactIndex ?? -1,
          isBackground: isBackground,
        });
      });
    },
    [
      TestScenarioTableColumnFieldGroup,
      TestScenarioTableColumnHeaderGroup,
      isBackground,
      tableColumns.instancesGroup,
      tableData,
      testScenarioEditorStoreApi,
    ]
  );

  return (
    <div className={"test-scenario-table"}>
      <StandaloneBeeTable
        allowedOperations={allowedOperations}
        columns={tableColumns.allColumns}
        enableKeyboardNavigation={true}
        headerLevelCountForAppendingRowIndexColumn={2}
        headerVisibility={BeeTableHeaderVisibility.AllLevels}
        isEditableHeader={false}
        isReadOnly={false}
        onCellUpdates={onCellUpdates}
        onColumnAdded={onColumnAdded}
        onColumnDeleted={onColumnDeleted}
        onDataCellClick={onDataCellClick}
        onDataCellKeyUp={onDataCellClick}
        onHeaderClick={onHeaderClick}
        onHeaderKeyUp={onHeaderClick}
        onRowAdded={onRowAdded}
        onRowDeleted={onRowDeleted}
        onRowDuplicated={onRowDuplicated}
        operationConfig={simulationOperationConfig}
        resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
        rows={tableRows}
        scrollableParentRef={scrollableParentRef}
        shouldRenderRowIndexColumn={!isBackground}
        shouldShowColumnsInlineControls={true}
        shouldShowRowsInlineControls={!isBackground}
      />
    </div>
  );
}

export default TestScenarioTable;
