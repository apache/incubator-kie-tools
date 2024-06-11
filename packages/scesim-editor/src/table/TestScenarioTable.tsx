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
import _, { isNumber } from "lodash";
import { v4 as uuid } from "uuid";

import {
  SceSim__backgroundDatasType,
  SceSim__backgroundType,
  SceSim__FactMappingType,
  SceSim__FactMappingValuesTypes,
  SceSim__scenariosType,
  SceSim__simulationType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

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

import { SceSimModel } from "@kie-tools/scesim-marshaller";

import { useTestScenarioEditorI18n } from "../i18n";
import { TestScenarioSelectedColumnMetaData, TestScenarioType } from "../TestScenarioEditor";

import "./TestScenarioTable.css";
import {
  retrieveFactMappingValueIndexByIdentifiers,
  retrieveModelDescriptor,
  retrieveRowsDataFromModel,
} from "../common/TestScenarioCommonFunctions";

function TestScenarioTable({
  assetType,
  tableData,
  scrollableParentRef,
  updateSelectedColumnMetaData,
  updateTestScenarioModel,
}: {
  assetType: string;
  tableData: SceSim__simulationType | SceSim__backgroundType;
  scrollableParentRef: React.RefObject<HTMLElement>;
  updateSelectedColumnMetaData: React.Dispatch<React.SetStateAction<TestScenarioSelectedColumnMetaData | null>>;
  updateTestScenarioModel: React.Dispatch<React.SetStateAction<SceSimModel>>;
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
    (dataType: string) => {
      let dataTypeLabel = dataType;
      if (assetType === TestScenarioType[TestScenarioType.RULE]) {
        dataTypeLabel = dataTypeLabel.split(".").pop() ?? dataTypeLabel;
      }
      return dataTypeLabel.endsWith("Void") ? "<Undefined>" : dataTypeLabel;
    },
    [assetType]
  );

  /* It updates any column width change in the Model */
  const setColumnWidth = useCallback(
    (inputIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      updateTestScenarioModel((prevState) => {
        const oldWidth = retrieveModelDescriptor(prevState.ScenarioSimulationModel, isBackground).factMappings
          .FactMapping![inputIndex].columnWidth?.__$$text;
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(oldWidth) : newWidthAction;

        let model = prevState;
        if (newWidth && oldWidth !== newWidth) {
          /* Cloning the FactMapping list and updating the new width */
          const deepClonedFactMappings: SceSim__FactMappingType[] = JSON.parse(
            JSON.stringify(
              retrieveModelDescriptor(prevState.ScenarioSimulationModel, isBackground).factMappings.FactMapping
            )
          );
          const factMappingToUpdate = deepClonedFactMappings[inputIndex];

          if (factMappingToUpdate.columnWidth?.__$$text) {
            factMappingToUpdate.columnWidth.__$$text = newWidth;
          } else {
            factMappingToUpdate.columnWidth = {
              __$$text: newWidth,
            };
          }

          model = {
            ScenarioSimulationModel: {
              ...prevState.ScenarioSimulationModel,
              simulation: {
                ...prevState.ScenarioSimulationModel.simulation,
                scesimModelDescriptor: {
                  factMappings: {
                    FactMapping: isBackground
                      ? prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping
                      : deepClonedFactMappings,
                  },
                },
              },
              background: {
                ...prevState.ScenarioSimulationModel.background,
                scesimModelDescriptor: {
                  factMappings: {
                    FactMapping: isBackground
                      ? deepClonedFactMappings
                      : prevState.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping,
                  },
                },
              },
            },
          };
        }

        return model;
      });
    },
    [isBackground, updateTestScenarioModel]
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
            : determineDataTypeLabel(factMapping.className.__$$text),
        groupType: factMapping.expressionIdentifier.type!.__$$text.toLowerCase(),
        id: factMapping!.expressionIdentifier.name!.__$$text,
        isRowIndexColumn: false,
        // isInlineEditable: isDescriptionColumn         **TODO NOT SURE IF IT MAKES SENSE TO IMPLEMENT IT
        //   ? false
        //   : assetType === TestScenarioType[TestScenarioType.RULE]
        //   ? true
        //   : false,
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
        dataType: determineDataTypeLabel(factMapping.factIdentifier.className!.__$$text),
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
    |   |               | field (given)  | field (given)| ...  | field (expect)  | field  (expect)| ... |
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

      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd || isHeader) {
        return [];
      }

      const columnIndex = conditions.selection.selectionStart.columnIndex;
      const groupType = conditions.column?.groupType;

      const atLeastTwoColumnsOfTheSameGroupType = groupType
        ? _.groupBy(conditions.columns, (column) => column?.groupType)[groupType].length > 1
        : true;

      const columnCanBeDeleted =
        columnIndex > 0 &&
        atLeastTwoColumnsOfTheSameGroupType &&
        ((isBackground && (conditions.columns?.length ?? 0) > 1) ||
          (!isBackground && columnIndex > 0 && (conditions.columns?.length ?? 0) > 4));

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
              ...(tableRows.length > 1 ? [BeeTableOperation.RowDelete] : []),
              BeeTableOperation.RowReset,
              BeeTableOperation.RowDuplicate,
            ]
          : []),
      ];
    },
    [isBackground, TestScenarioTableColumnHeaderGroup, TestScenarioTableColumnInstanceGroup, tableRows.length]
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
        updateTestScenarioModel((prevState) => {
          /* To update the related FactMappingValue, it compares every FactMappingValue associated with the Scenario (Row)
             that contains the cell with the FactMapping (Column) fields factIdentifier and expressionIdentifier */
          const factMapping = retrieveModelDescriptor(prevState.ScenarioSimulationModel, isBackground).factMappings
            .FactMapping![update.columnIndex + columnIndexStart];

          const deepClonedRowsData: SceSim__FactMappingValuesTypes[] = JSON.parse(
            JSON.stringify(retrieveRowsDataFromModel(prevState.ScenarioSimulationModel, isBackground))
          );
          const factMappingValues = deepClonedRowsData[update.rowIndex].factMappingValues.FactMappingValue!;
          const newFactMappingValues = [...factMappingValues];

          const factMappingValueToUpdateIndex = retrieveFactMappingValueIndexByIdentifiers(
            newFactMappingValues,
            factMapping.factIdentifier,
            factMapping.expressionIdentifier
          );
          const factMappingValueToUpdate = factMappingValues[factMappingValueToUpdateIndex];

          if (factMappingValueToUpdate.rawValue) {
            factMappingValueToUpdate.rawValue!.__$$text = update.value;
          } else {
            newFactMappingValues[factMappingValueToUpdateIndex] = {
              ...factMappingValueToUpdate,
              rawValue: {
                __$$text: update.value,
              },
            };
          }

          deepClonedRowsData[update.rowIndex].factMappingValues.FactMappingValue = newFactMappingValues;

          return {
            ScenarioSimulationModel: {
              ...prevState.ScenarioSimulationModel,
              simulation: {
                ...prevState.ScenarioSimulationModel.simulation,
                scesimData: {
                  Scenario: isBackground
                    ? prevState.ScenarioSimulationModel.simulation.scesimData.Scenario
                    : deepClonedRowsData,
                },
              },
              background: {
                ...prevState.ScenarioSimulationModel.background,
                scesimData: {
                  BackgroundData: isBackground
                    ? deepClonedRowsData
                    : prevState.ScenarioSimulationModel.background.scesimData.BackgroundData,
                },
              },
            },
          };
        });
      });
    },
    [columnIndexStart, isBackground, updateTestScenarioModel]
  );

  const getNextAvailablePrefixedName = useCallback(
    (names: string[], namePrefix: string, lastIndex: number = names.length): string => {
      const candidate = `${namePrefix}-${lastIndex + 1}`;
      const elemWithCandidateName = names.indexOf(candidate);
      return elemWithCandidateName >= 0 ? getNextAvailablePrefixedName(names, namePrefix, lastIndex + 1) : candidate;
    },
    []
  );

  /* It determines in which index position a column should be added. In case of a field, the new column index
     is simply in the right or in the left of the selected column. In case of a new instance, it's required to 
     find the first column index outside the selected Instance group. */
  const determineNewColumnTargetIndex = useCallback(
    (
      factMappings: SceSim__FactMappingType[],
      insertDirection: InsertRowColumnsDirection,
      selectedColumnIndex: number,
      selectedColumnGroupType: string,
      selectedFactMapping: SceSim__FactMappingType
    ) => {
      const groupType = selectedFactMapping.expressionIdentifier.type!.__$$text;
      const instanceName = selectedFactMapping.factIdentifier.name!.__$$text;
      const instanceType = selectedFactMapping.factIdentifier.className!.__$$text;

      if (
        selectedColumnGroupType === TestScenarioTableColumnFieldGroup.EXPECT ||
        selectedColumnGroupType === TestScenarioTableColumnFieldGroup.GIVEN
      ) {
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
    },
    [TestScenarioTableColumnFieldGroup]
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
      /* GIVEN and EXPECTED column types can be added only */
      if (TestScenarioTableColumnFieldGroup.OTHER === args.groupType) {
        return;
      }
      const isInstance =
        args.groupType === TestScenarioTableColumnInstanceGroup.EXPECT ||
        args.groupType === TestScenarioTableColumnInstanceGroup.GIVEN;

      updateTestScenarioModel((prevState) => {
        const factMappingList = retrieveModelDescriptor(prevState.ScenarioSimulationModel, isBackground).factMappings
          .FactMapping!;
        const selectedColumnIndex = determineSelectedColumnIndex(factMappingList, args.currentIndex, isInstance);

        /* Creating the new FactMapping based on the original selected column's FactMapping */
        const selectedColumnFactMapping = factMappingList[selectedColumnIndex];
        const targetColumnIndex = determineNewColumnTargetIndex(
          factMappingList,
          args.insertDirection,
          selectedColumnIndex,
          args.groupType,
          selectedColumnFactMapping
        );

        const instanceDefaultNames = factMappingList
          .filter((factMapping) => factMapping.factAlias!.__$$text.startsWith("INSTANCE-"))
          .map((factMapping) => factMapping.factAlias!.__$$text);

        const isNewInstance =
          isInstance || selectedColumnFactMapping.factIdentifier.className?.__$$text === "java.lang.Void";

        const newFactMapping = {
          className: { __$$text: "java.lang.Void" },
          columnWidth: { __$$text: 150 },
          expressionAlias: { __$$text: "PROPERTY" },
          expressionElements: isNewInstance
            ? undefined
            : {
                ExpressionElement: [
                  {
                    step: {
                      __$$text: selectedColumnFactMapping.expressionElements!.ExpressionElement![0].step.__$$text,
                    },
                  },
                ],
              },
          expressionIdentifier: {
            name: { __$$text: `_${uuid()}`.toLocaleUpperCase() },
            type: { __$$text: selectedColumnFactMapping.expressionIdentifier.type!.__$$text },
          },
          factAlias: {
            __$$text: isNewInstance
              ? getNextAvailablePrefixedName(instanceDefaultNames, "INSTANCE")
              : selectedColumnFactMapping.factAlias.__$$text,
          },
          factIdentifier: {
            name: {
              __$$text: isNewInstance
                ? getNextAvailablePrefixedName(instanceDefaultNames, "INSTANCE")
                : selectedColumnFactMapping.factIdentifier.name!.__$$text,
            },
            className: {
              __$$text: isNewInstance ? "java.lang.Void" : selectedColumnFactMapping.factIdentifier.className!.__$$text,
            },
          },
          factMappingValueType: { __$$text: "NOT_EXPRESSION" },
        };

        /* Cloning the FactMapping list and putting the new one in the user defined index */
        const deepClonedFactMappings = JSON.parse(
          JSON.stringify(
            retrieveModelDescriptor(prevState.ScenarioSimulationModel, isBackground).factMappings.FactMapping
          )
        );
        deepClonedFactMappings.splice(targetColumnIndex, 0, newFactMapping);

        /* Creating and adding a new FactMappingValue (cell) in every row, as a consequence of the new FactMapping (column) 
           we're going to introduce. The FactMappingValue will be linked with its related FactMapping via expressionIdentifier
           and factIdentier data. That means, the column index of new FactMappingValue could be different in other Scenario (rows) */
        const deepClonedRowsData: SceSim__FactMappingValuesTypes[] = JSON.parse(
          JSON.stringify(retrieveRowsDataFromModel(prevState.ScenarioSimulationModel, isBackground))
        );
        deepClonedRowsData.forEach((scenario) => {
          scenario.factMappingValues.FactMappingValue!.splice(args.beforeIndex + 1, 0, {
            expressionIdentifier: {
              name: { __$$text: newFactMapping.expressionIdentifier.name.__$$text },
              type: { __$$text: newFactMapping.expressionIdentifier.type.__$$text },
            },
            factIdentifier: {
              name: { __$$text: newFactMapping.factIdentifier.name.__$$text },
              className: { __$$text: newFactMapping.factIdentifier.className.__$$text },
            },
            rawValue: { __$$text: "", "@_class": "string" },
          });
        });

        return {
          ScenarioSimulationModel: {
            ...prevState.ScenarioSimulationModel,
            simulation: {
              scesimModelDescriptor: {
                factMappings: {
                  FactMapping: isBackground
                    ? prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping
                    : deepClonedFactMappings,
                },
              },
              scesimData: {
                Scenario: isBackground
                  ? prevState.ScenarioSimulationModel.simulation.scesimData.Scenario
                  : deepClonedRowsData,
              },
            },
            background: {
              scesimModelDescriptor: {
                factMappings: {
                  FactMapping: isBackground
                    ? deepClonedFactMappings
                    : prevState.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping,
                },
              },
              scesimData: {
                BackgroundData: isBackground
                  ? deepClonedRowsData
                  : prevState.ScenarioSimulationModel.background.scesimData.BackgroundData,
              },
            },
          },
        };
      });
    },
    [
      determineNewColumnTargetIndex,
      determineSelectedColumnIndex,
      getNextAvailablePrefixedName,
      isBackground,
      updateTestScenarioModel,
      TestScenarioTableColumnFieldGroup,
      TestScenarioTableColumnInstanceGroup,
    ]
  );

  /**
   * It removes a FactMapping (Column) at the given column index toghter with its related Data Cells.
   */
  const onColumnDeleted = useCallback(
    (args: { columnIndex: number; groupType: string }) => {
      updateTestScenarioModel((prevState) => {
        const isInstance =
          args.groupType === TestScenarioTableColumnInstanceGroup.EXPECT ||
          args.groupType === TestScenarioTableColumnInstanceGroup.GIVEN;

        const factMappings = retrieveModelDescriptor(prevState.ScenarioSimulationModel, isBackground).factMappings
          .FactMapping!;
        const columnIndexToRemove = determineSelectedColumnIndex(factMappings, args.columnIndex + 1, isInstance);

        /* Retriving the FactMapping (Column) to be removed). If the user selected a single column, it finds the exact
           FactMapping to delete. If the user selected an instance (group of columns), it retrives all the FactMappings
           that belongs to the the instance group */
        const factMappingToRemove = factMappings[columnIndexToRemove];
        const groupType = factMappingToRemove.expressionIdentifier.type!.__$$text;
        const instanceName = factMappingToRemove.factIdentifier.name!.__$$text;
        const instanceType = factMappingToRemove.factIdentifier.className!.__$$text;

        const allFactMappingWithIndexesToRemove = isInstance
          ? factMappings
              .map((factMapping, index) => {
                if (
                  factMapping.expressionIdentifier.type!.__$$text === groupType &&
                  factMapping.factIdentifier.name?.__$$text === instanceName &&
                  factMapping.factIdentifier.className?.__$$text === instanceType
                ) {
                  return { factMappingIndex: index, factMapping: factMapping };
                } else {
                  return {};
                }
              })
              .filter((item) => isNumber(item.factMappingIndex))
          : [{ factMappingIndex: args.columnIndex + columnIndexStart, factMapping: factMappingToRemove }];

        /* Cloning the FactMappings list (Columns) and and removing the FactMapping (Column) at given index */
        const deepClonedFactMappings = JSON.parse(
          JSON.stringify(
            retrieveModelDescriptor(prevState.ScenarioSimulationModel, isBackground).factMappings.FactMapping
          )
        );
        deepClonedFactMappings.splice(
          allFactMappingWithIndexesToRemove[0].factMappingIndex,
          allFactMappingWithIndexesToRemove.length
        );

        /* Cloning the Scenario List (Rows) and finding the Cell(s) to remove accordingly to the factMapping data of 
          the removed columns */
        const deepClonedRowsData: SceSim__FactMappingValuesTypes[] = JSON.parse(
          JSON.stringify(retrieveRowsDataFromModel(prevState.ScenarioSimulationModel, isBackground) ?? [])
        );
        deepClonedRowsData.forEach((rowData) => {
          allFactMappingWithIndexesToRemove.forEach((itemToRemove) => {
            const factMappingValueColumnIndexToRemove = retrieveFactMappingValueIndexByIdentifiers(
              rowData.factMappingValues.FactMappingValue!,
              itemToRemove.factMapping!.factIdentifier,
              itemToRemove.factMapping!.expressionIdentifier
            )!;

            return {
              factMappingValues: {
                FactMappingValue: rowData.factMappingValues.FactMappingValue!.splice(
                  factMappingValueColumnIndexToRemove,
                  1
                ),
              },
            };
          });
        });

        /** Updating the selectedColumn. When deleting, BEETable automatically shifts the selected cell in the left */
        const firstIndexOnTheLeft = Math.min(
          ...allFactMappingWithIndexesToRemove.map((item) => item.factMappingIndex!)
        );
        const selectedColumnIndex = firstIndexOnTheLeft > 0 ? firstIndexOnTheLeft - 1 : 0;
        updateSelectedColumnMetaData({
          factMapping: JSON.parse(JSON.stringify(deepClonedFactMappings[selectedColumnIndex])),
          index: firstIndexOnTheLeft,
          isBackground,
        });

        return {
          ScenarioSimulationModel: {
            ...prevState.ScenarioSimulationModel,
            simulation: {
              scesimModelDescriptor: {
                factMappings: {
                  FactMapping: isBackground
                    ? prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping
                    : deepClonedFactMappings,
                },
              },
              scesimData: {
                Scenario: isBackground
                  ? prevState.ScenarioSimulationModel.simulation.scesimData.Scenario
                  : deepClonedRowsData,
              },
            },
            background: {
              scesimModelDescriptor: {
                factMappings: {
                  FactMapping: isBackground
                    ? deepClonedFactMappings
                    : prevState.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping,
                },
              },
              scesimData: {
                BackgroundData: isBackground
                  ? deepClonedRowsData
                  : prevState.ScenarioSimulationModel.background.scesimData.BackgroundData,
              },
            },
          },
        };
      });
    },
    [
      updateTestScenarioModel,
      TestScenarioTableColumnInstanceGroup,
      isBackground,
      determineSelectedColumnIndex,
      columnIndexStart,
      updateSelectedColumnMetaData,
    ]
  );

  /**
   * It adds a Scenario (Row) at the given row index
   */
  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      if (isBackground) {
        throw new Error("Impossible state. Background table can have a single row only");
      }
      updateTestScenarioModel((prevState) => {
        /* Creating a new Scenario (Row) composed by a list of FactMappingValues. The list order is not relevant. */
        const factMappings =
          prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping ?? [];
        const factMappingValuesItems = factMappings.map((factMapping) => {
          return {
            expressionIdentifier: {
              name: { __$$text: factMapping.expressionIdentifier.name!.__$$text },
              type: { __$$text: factMapping.expressionIdentifier.type!.__$$text },
            },
            factIdentifier: {
              name: { __$$text: factMapping.factIdentifier.name!.__$$text },
              className: { __$$text: factMapping.factIdentifier.className!.__$$text },
            },
            rawValue: { __$$text: "", "@_class": "string" },
          };
        });

        const newScenario = {
          factMappingValues: {
            FactMappingValue: factMappingValuesItems,
          },
        };

        /* Cloning che current Scenario List and adding thew new Scenario previously created */
        const deepClonedScenarios = JSON.parse(
          JSON.stringify(prevState.ScenarioSimulationModel.simulation.scesimData.Scenario)
        );
        deepClonedScenarios.splice(args.beforeIndex, 0, newScenario);

        return {
          ScenarioSimulationModel: {
            ...prevState.ScenarioSimulationModel,
            simulation: {
              ...prevState.ScenarioSimulationModel.simulation,
              scesimData: {
                Scenario: deepClonedScenarios,
              },
            },
          },
        };
      });
    },
    [isBackground, updateTestScenarioModel]
  );

  /**
   * It deletes a Scenario (Row) at the given row index
   */
  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      if (isBackground) {
        throw new Error("Impossible state. Background table can have a single row only");
      }
      updateTestScenarioModel((prevState) => {
        /* Just updating the Scenario List (Rows) cloning the current List and removing the row at the given rowIndex */
        const deepClonedScenarios = JSON.parse(
          JSON.stringify(prevState.ScenarioSimulationModel.simulation.scesimData.Scenario ?? [])
        );
        deepClonedScenarios.splice(args.rowIndex, 1);

        return {
          ScenarioSimulationModel: {
            ...prevState.ScenarioSimulationModel,
            simulation: {
              ...prevState.ScenarioSimulationModel.simulation,
              scesimData: {
                ...prevState.ScenarioSimulationModel.simulation.scesimData,
                Scenario: deepClonedScenarios,
              },
            },
          },
        };
      });
    },
    [isBackground, updateTestScenarioModel]
  );

  /**
   * It duplicates a Scenario (Row) at the given row index
   */
  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      if (isBackground) {
        throw new Error("Impossible state. Background table can have a single row only");
      }
      updateTestScenarioModel((prevState) => {
        /* It simply clones a Scenario (Row) and adds it in a current-cloned Scenario list */
        const clonedFactMappingValues = JSON.parse(
          JSON.stringify(
            prevState.ScenarioSimulationModel.simulation.scesimData.Scenario![args.rowIndex].factMappingValues
              .FactMappingValue
          )
        );

        const factMappingValues = {
          factMappingValues: {
            FactMappingValue: clonedFactMappingValues,
          },
        };

        const deepClonedScenarios = JSON.parse(
          JSON.stringify(prevState.ScenarioSimulationModel.simulation.scesimData.Scenario ?? [])
        );
        deepClonedScenarios.splice(args.rowIndex, 0, factMappingValues);

        return {
          ScenarioSimulationModel: {
            ...prevState.ScenarioSimulationModel,
            simulation: {
              ...prevState.ScenarioSimulationModel.simulation,
              scesimData: {
                ...prevState.ScenarioSimulationModel.simulation.scesimData,
                Scenario: deepClonedScenarios,
              },
            },
          },
        };
      });
    },
    [isBackground, updateTestScenarioModel]
  );

  /**
   * Behavior to apply when a DataCell is clicked
   */
  const onDataCellClick = useCallback(
    (_columnID: string) => {
      updateSelectedColumnMetaData(null);
    },
    [updateSelectedColumnMetaData]
  );

  const onHeaderClick = useCallback(
    (columnKey: string) => {
      console.log(columnKey);
      if (
        columnKey == TestScenarioTableColumnHeaderGroup.EXPECT ||
        columnKey == TestScenarioTableColumnHeaderGroup.GIVEN
      ) {
        updateSelectedColumnMetaData(null);
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
          let selectedFactMapping;
          let selectedFactIndex;
          if (propertyID) {
            selectedFactMapping = modelDescriptor.factMappings.FactMapping!.find(
              (factMapping) => factMapping.expressionIdentifier.name?.__$$text === propertyID
            );
            selectedFactIndex = selectedFactMapping
              ? modelDescriptor.factMappings.FactMapping!.indexOf(selectedFactMapping!)
              : -1;
          }
          const selectedColumnMetaData = {
            factMapping: JSON.parse(JSON.stringify(selectedFactMapping)),
            index: selectedFactIndex ?? -1,
            isBackground: isBackground,
          };

          updateSelectedColumnMetaData(selectedColumnMetaData);
        } else {
          updateSelectedColumnMetaData(null);
        }
        return;
      }

      const selectedFactMapping = modelDescriptor.factMappings.FactMapping!.find(
        (factMapping) => factMapping.expressionIdentifier.name?.__$$text == columnKey
      );
      const selectedFactIndex = selectedFactMapping
        ? modelDescriptor.factMappings.FactMapping!.indexOf(selectedFactMapping!)
        : -1;

      const selectedColumnMetaData = {
        factMapping: JSON.parse(JSON.stringify(selectedFactMapping)),
        index: selectedFactIndex ?? -1,
        isBackground: isBackground,
      };

      updateSelectedColumnMetaData(selectedColumnMetaData ?? null);
    },
    [
      TestScenarioTableColumnFieldGroup,
      TestScenarioTableColumnHeaderGroup,
      isBackground,
      tableColumns.instancesGroup,
      tableData,
      updateSelectedColumnMetaData,
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
