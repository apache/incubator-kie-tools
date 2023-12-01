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
import { v4 as uuid } from "uuid";

import {
  SceSim__expressionIdentifierType,
  SceSim__factIdentifierType,
  SceSim__FactMappingType,
  SceSim__ScenarioType,
  SceSim__simulationType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import {
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
} from "@kie-tools/boxed-expression-component/dist/api/BeeTable";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";
import {
  BeeTableCellUpdate,
  StandaloneBeeTable,
  getColumnsAtLastLevel,
} from "@kie-tools/boxed-expression-component/dist/table/BeeTable";

import { SceSimModel } from "@kie-tools/scesim-marshaller";

import { useTestScenarioEditorI18n } from "../i18n";
import { TestScenarioType } from "../TestScenarioEditor";

import "./TestScenarioTable.css";

function TestScenarioTable({
  assetType,
  tableData,
  updateTestScenarioModel,
}: {
  assetType: string;
  tableData: SceSim__simulationType;
  updateTestScenarioModel: React.Dispatch<React.SetStateAction<SceSimModel>>;
}) {
  enum SimulationTableColumnHeaderGroup {
    EXPECT = "expect-header",
    GIVEN = "given-header",
  }
  enum SimulationTableColumnInstanceGroup {
    EXPECT = "expect-instance",
    GIVEN = "given-instance",
  }
  enum SimulationTableColumnFieldGroup {
    EXPECT = "expect",
    GIVEN = "given",
    OTHER = "other",
  }

  type ROWTYPE = any; // FIXME: https://github.com/kiegroup/kie-issues/issues/169

  const { i18n } = useTestScenarioEditorI18n();

  const tableScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });

  useEffect(() => {
    tableScrollableElementRef.current.current = document.querySelector(".kie-scesim-editor--table-container") ?? null;
  }, []);

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

  /* It determines the column data based on the given FactMapping (Scesim column representation).
     In case of the Description column, the behavior is slightly different (column dimension, label and no datatype label) 
  */
  const generateColumnFromFactMapping = useCallback(
    (factMapping: SceSim__FactMappingType, isDescriptionColumn?: boolean) => {
      return {
        accessor: factMapping.expressionIdentifier.name!.__$$text,
        dataType: isDescriptionColumn ? undefined : determineDataTypeLabel(factMapping.className.__$$text),
        groupType: factMapping.expressionIdentifier.type!.__$$text.toLowerCase(),
        id: factMapping!.expressionIdentifier.name!.__$$text,
        isRowIndexColumn: false,
        label: isDescriptionColumn ? factMapping.factAlias.__$$text : factMapping.expressionAlias!.__$$text,
        minWidth: isDescriptionColumn ? 300 : 150,
        width: factMapping.columnWidth?.__$$text ?? (isDescriptionColumn ? 300 : 150),
      };
    },
    [determineDataTypeLabel]
  );

  /* It determines the Instance Section (the header row in the middle) based on the given FactMapping (Scesim column representation)
     and the groupType. 
  */
  const generateInstanceSectionFromFactMapping = useCallback(
    (factMapping: SceSim__FactMappingType, groupType: SimulationTableColumnInstanceGroup) => {
      const instanceID =
        factMapping.expressionIdentifier.type?.__$$text + "." + factMapping.factIdentifier.className?.__$$text;

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

  /*
    It generates the columns of the TestScenarioTable, based on the following logic:
    +------------------------------------------------------+----------------------------------------+
    |  Description  |  givenSection (given-header)         |   expectSection (expect-header)        |
    |               +--------------------------------+-----+----------------------------------+-----+
    |               | givenInstance (given-instance) | ... | expectGroup (expect-instance)    | ... |
    |               +----------------+---------------+-----+----------------------------------+-----+
    |               | field (given)  | field (given)| ...  | field (expect)  | field  (expect)| ... |
    +---------------+----------------+---------------+-----+-----------------+----------------+-----+
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

    (tableData.scesimModelDescriptor.factMappings?.FactMapping ?? []).forEach((factMapping) => {
      const instanceID =
        factMapping.expressionIdentifier.type?.__$$text + "." + factMapping.factIdentifier.className?.__$$text;
      if (factMapping.expressionIdentifier.type?.__$$text === SimulationTableColumnFieldGroup.GIVEN.toUpperCase()) {
        const instance = givenInstances.find((instanceColumn) => instanceColumn.id === instanceID);
        if (instance) {
          instance.columns?.push(generateColumnFromFactMapping(factMapping));
        } else {
          const newInstance = generateInstanceSectionFromFactMapping(
            factMapping,
            SimulationTableColumnInstanceGroup.GIVEN
          );
          newInstance.columns.push(generateColumnFromFactMapping(factMapping));
          givenInstances.push(newInstance);
        }
      } else if (
        factMapping.expressionIdentifier.type!.__$$text === SimulationTableColumnFieldGroup.EXPECT.toUpperCase()
      ) {
        const instance = expectInstances.find((instanceColumn) => instanceColumn.id === instanceID);
        if (instance) {
          instance.columns?.push(generateColumnFromFactMapping(factMapping));
        } else {
          const newInstance = generateInstanceSectionFromFactMapping(
            factMapping,
            SimulationTableColumnInstanceGroup.EXPECT
          );
          newInstance.columns.push(generateColumnFromFactMapping(factMapping));
          expectInstances.push(newInstance);
        }
      } else if (
        factMapping.expressionIdentifier.type!.__$$text === SimulationTableColumnFieldGroup.OTHER.toUpperCase() &&
        factMapping.expressionIdentifier.name!.__$$text === "Description"
      ) {
        descriptionColumns.push(generateColumnFromFactMapping(factMapping, true));
      }
    });

    const givenSection = [
      {
        accessor: SimulationTableColumnHeaderGroup.GIVEN,
        groupType: SimulationTableColumnHeaderGroup.GIVEN,
        id: SimulationTableColumnHeaderGroup.GIVEN,
        isRowIndexColumn: false,
        label: i18n.table.given.toUpperCase(),
        columns: givenInstances,
      },
    ];

    const expectSection =
      expectInstances.length > 0
        ? [
            {
              accessor: SimulationTableColumnHeaderGroup.EXPECT,
              groupType: SimulationTableColumnHeaderGroup.EXPECT,
              id: SimulationTableColumnHeaderGroup.EXPECT,
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
    SimulationTableColumnHeaderGroup,
    SimulationTableColumnFieldGroup,
    SimulationTableColumnInstanceGroup,
    tableData.scesimModelDescriptor.factMappings?.FactMapping,
  ]);

  /* Retrieving the Rows from the Test Scenario model */
  const tableRows = useMemo(
    () =>
      (tableData.scesimData.Scenario ?? []).map((scenario, index) => {
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
    [tableColumns, tableData.scesimData.Scenario]
  );

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
        return [];
      }
      if (
        conditions.column?.groupType === SimulationTableColumnHeaderGroup.EXPECT ||
        conditions.column?.groupType === SimulationTableColumnHeaderGroup.GIVEN
      ) {
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
        (conditions.columns?.length ?? 0) > 2 &&
        (conditions.column?.columns?.length ?? 0) <= 0;

      const columnOperations = (
        groupType === SimulationTableColumnInstanceGroup.GIVEN ||
        groupType === SimulationTableColumnInstanceGroup.EXPECT
          ? columnIndex in [0]
          : columnIndex in [0, 1]
      )
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
              ...(tableRows.length > 1 ? [BeeTableOperation.RowDelete] : []),
              BeeTableOperation.RowReset,
              BeeTableOperation.RowDuplicate,
            ]
          : []),
      ];
    },
    [SimulationTableColumnHeaderGroup, SimulationTableColumnInstanceGroup, tableRows.length]
  );

  const generateOperationConfig = useCallback(
    (groupName: string) => {
      const isInstance =
        groupName === SimulationTableColumnInstanceGroup.EXPECT ||
        groupName === SimulationTableColumnInstanceGroup.GIVEN;

      return [
        {
          group: groupName,
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
    [SimulationTableColumnInstanceGroup, i18n]
  );

  const simulationOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    const config: BeeTableOperationConfig = {};
    config[""] = generateOperationConfig("");
    config[SimulationTableColumnHeaderGroup.EXPECT] = generateOperationConfig("");
    config[SimulationTableColumnHeaderGroup.GIVEN] = generateOperationConfig("");
    config[SimulationTableColumnInstanceGroup.EXPECT] = generateOperationConfig(
      SimulationTableColumnInstanceGroup.EXPECT
    );
    config[SimulationTableColumnInstanceGroup.GIVEN] = generateOperationConfig(
      SimulationTableColumnInstanceGroup.GIVEN
    );
    config[SimulationTableColumnFieldGroup.EXPECT] = generateOperationConfig(SimulationTableColumnFieldGroup.EXPECT);
    config[SimulationTableColumnFieldGroup.GIVEN] = generateOperationConfig(SimulationTableColumnFieldGroup.GIVEN);
    config[SimulationTableColumnFieldGroup.OTHER] = generateOperationConfig(SimulationTableColumnFieldGroup.OTHER);
    return config;
  }, [
    SimulationTableColumnFieldGroup,
    SimulationTableColumnHeaderGroup,
    SimulationTableColumnInstanceGroup,
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
          const factMapping =
            prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings!.FactMapping![
              update.columnIndex + 1
            ];

          const deepClonedScenarios: SceSim__ScenarioType[] = JSON.parse(
            JSON.stringify(prevState.ScenarioSimulationModel.simulation.scesimData.Scenario)
          );
          const factMappingValues = deepClonedScenarios[update.rowIndex].factMappingValues.FactMappingValue!;
          const newFactMappingValues = [...factMappingValues];

          factMappingValues.forEach((factMappingValue, index) => {
            if (
              factMapping.factIdentifier.name?.__$$text === factMappingValue.factIdentifier.name?.__$$text &&
              factMapping.factIdentifier.className?.__$$text === factMappingValue.factIdentifier.className?.__$$text &&
              factMapping.expressionIdentifier.name?.__$$text ===
                factMappingValue.expressionIdentifier.name?.__$$text &&
              factMapping.expressionIdentifier.type?.__$$text === factMappingValue.expressionIdentifier.type?.__$$text
            ) {
              if (factMappingValues[index].rawValue) {
                newFactMappingValues[index].rawValue!.__$$text = update.value;
              } else {
                newFactMappingValues[index] = {
                  ...factMappingValues[index],
                  rawValue: {
                    __$$text: update.value,
                  },
                };
              }
            }
          });

          deepClonedScenarios[update.rowIndex].factMappingValues.FactMappingValue = newFactMappingValues;

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
      });
    },
    [updateTestScenarioModel]
  );

  const retrieveColumnIndexbyIdentifiers = useCallback(
    (factIdentifier: SceSim__factIdentifierType, expressionIdentifier: SceSim__expressionIdentifierType) => {
      return tableData.scesimModelDescriptor.factMappings!.FactMapping?.findIndex((factMapping) => {
        return (
          factMapping.factIdentifier.name?.__$$text == factIdentifier.name?.__$$text &&
          factMapping.factIdentifier.className?.__$$text == factIdentifier.className?.__$$text &&
          factMapping.expressionIdentifier.name?.__$$text == expressionIdentifier.name?.__$$text &&
          factMapping.expressionIdentifier.type?.__$$text == expressionIdentifier.type?.__$$text
        );
      });
    },
    [tableData.scesimModelDescriptor.factMappings]
  );

  const getSectionIndexForGroupType = useCallback(
    (addingRight: boolean, selectedColumnIndex: number, groupType: string) => {
      if (groupType === SimulationTableColumnFieldGroup.EXPECT || groupType === SimulationTableColumnFieldGroup.GIVEN) {
        if (addingRight) {
          return selectedColumnIndex + 1;
        } else {
          return selectedColumnIndex;
        }
      }
      if (
        groupType === SimulationTableColumnInstanceGroup.EXPECT ||
        groupType === SimulationTableColumnInstanceGroup.GIVEN
      ) {
        const selectedFactMapping = tableData.scesimModelDescriptor.factMappings!.FactMapping![selectedColumnIndex];

        const factMappingsSize = tableData.scesimModelDescriptor.factMappings!.FactMapping!.length;
        const groupType = selectedFactMapping.expressionIdentifier.type!.__$$text;
        const instanceName = selectedFactMapping.factIdentifier.name!.__$$text;
        const instanceType = selectedFactMapping.factIdentifier.className!.__$$text;

        let newInstanceIndex = selectedColumnIndex;

        console.log("SELECTED");
        console.log(selectedFactMapping);

        if (addingRight) {
          for (let i = newInstanceIndex; i < factMappingsSize; i++) {
            const currentFM = tableData.scesimModelDescriptor.factMappings!.FactMapping![i];
            console.log("CURRENT");
            console.log(currentFM);
            if (
              currentFM.expressionIdentifier.type!.__$$text === groupType &&
              currentFM.factIdentifier.name?.__$$text === instanceName &&
              currentFM.factIdentifier.className?.__$$text === instanceType
            ) {
              continue;
            } else {
              newInstanceIndex = i;
              break;
            }
          }
        } else {
          for (let i = newInstanceIndex; i >= 0; i--) {
            const currentFM = tableData.scesimModelDescriptor.factMappings!.FactMapping![i];
            console.log("CURRENT");
            console.log(currentFM);

            if (
              currentFM.expressionIdentifier.type!.__$$text === groupType &&
              currentFM.factIdentifier.name?.__$$text === instanceName &&
              currentFM.factIdentifier.className?.__$$text === instanceType
            ) {
              continue;
            } else {
              newInstanceIndex = i + 1;
              break;
            }
          }
        }

        return newInstanceIndex;
      }

      return selectedColumnIndex;
    },
    [
      SimulationTableColumnFieldGroup,
      SimulationTableColumnInstanceGroup,
      retrieveColumnIndexbyIdentifiers,
      tableColumns.instancesGroup,
      tableData.scesimModelDescriptor.factMappings,
    ]
  );

  const getNextAvailablePrefixedName = useCallback(
    (names: string[], namePrefix: string, lastIndex: number = names.length): string => {
      const candidate = `${namePrefix}-${lastIndex + 1}`;
      const elemWithCandidateName = names.indexOf(candidate);
      return elemWithCandidateName >= 0 ? getNextAvailablePrefixedName(names, namePrefix, lastIndex + 1) : candidate;
    },
    []
  );

  /**
   * It adds a new FactMapping (Column) in the Model Descriptor structure and adds the new column related FactMapping Value (Cell)
   */
  const onColumnAdded = useCallback(
    (args: { beforeIndex: number; currentIndex: number; groupType: string }) => {
      /* GIVEN and EXPECTED column types can be added only */
      if (SimulationTableColumnFieldGroup.OTHER === args.groupType) {
        return;
      }
      const isInstance =
        args.groupType === SimulationTableColumnInstanceGroup.EXPECT ||
        args.groupType === SimulationTableColumnInstanceGroup.GIVEN;
      let selectedIndex = args.currentIndex;

      if (isInstance) {
        console.log("=======");
        console.log(tableColumns);
        console.log(selectedIndex - 1);
        console.log("=======");

        const firstColumnID = tableColumns.instancesGroup[selectedIndex - 1].columns![0].id;

        console.log(tableColumns);

        const selectedFactMapping = tableData.scesimModelDescriptor.factMappings!.FactMapping!.filter(
          (fm) => fm.expressionIdentifier.name!.__$$text === firstColumnID
        )[0];

        selectedIndex = retrieveColumnIndexbyIdentifiers(
          selectedFactMapping.factIdentifier,
          selectedFactMapping.expressionIdentifier
        )!;
      }

      console.log("AFTER " + args.beforeIndex);
      console.log("SELECTED " + selectedIndex);
      const sectionIndex = getSectionIndexForGroupType(
        args.beforeIndex === args.currentIndex,
        selectedIndex,
        args.groupType
      );
      console.log(args.groupType);
      console.log(isInstance);
      console.log(sectionIndex);

      updateTestScenarioModel((prevState) => {
        /* Creating the new FactMapping based on the original selected column's FactMapping */
        const originColumnFactMapping =
          prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings!.FactMapping![selectedIndex];
        const instanceDefaultNames = prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor
          .factMappings!.FactMapping!.filter((factMapping) =>
            factMapping.factIdentifier.name!.__$$text.startsWith("INSTANCE-")
          )
          .map((factMapping) => factMapping.factIdentifier.name!.__$$text);

        console.log(originColumnFactMapping);

        const newFactMapping = {
          expressionIdentifier: {
            name: { __$$text: `_${uuid()}`.toLocaleUpperCase() },
            type: { __$$text: originColumnFactMapping.expressionIdentifier.type!.__$$text },
          },
          factIdentifier: {
            name: {
              __$$text: isInstance
                ? getNextAvailablePrefixedName(instanceDefaultNames, "INSTANCE")
                : originColumnFactMapping.factIdentifier.name!.__$$text,
            },
            className: {
              __$$text: isInstance ? "java.lang.Void" : originColumnFactMapping.factIdentifier.className!.__$$text,
            },
          },
          className: { __$$text: "java.lang.Void" },
          factAlias: {
            __$$text: isInstance
              ? getNextAvailablePrefixedName(instanceDefaultNames, "INSTANCE")
              : originColumnFactMapping.factAlias.__$$text,
          },
          expressionAlias: { __$$text: "PROPERTY" },
          columnWidth: { __$$text: 150 },
          factMappingValueType: { __$$text: "NOT_EXPRESSION" },
        };

        /* Cloning the FactMapping list and putting the new one in the user defined index */
        const deepClonedFactMappings = JSON.parse(
          JSON.stringify(prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings?.FactMapping)
        );
        console.log("adding in " + sectionIndex);
        deepClonedFactMappings.splice(sectionIndex, 0, newFactMapping);

        /* Creating and adding a new FactMappingValue (cell) in every row, as a consequence of the new FactMapping (column) 
           we're going to introduce. The FactMappingValue will be linked with its related FactMapping via expressionIdentifier
           and factIdentier data. That means, the column index of new FactMappingValue could be different in other Scenario (rows) */
        const deepClonedScenarios: SceSim__ScenarioType[] = JSON.parse(
          JSON.stringify(prevState.ScenarioSimulationModel.simulation.scesimData.Scenario)
        );
        deepClonedScenarios.forEach((scenario) => {
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
                  FactMapping: deepClonedFactMappings,
                },
              },
              scesimData: {
                Scenario: deepClonedScenarios,
              },
            },
          },
        };
      });
    },
    [SimulationTableColumnFieldGroup, getSectionIndexForGroupType, updateTestScenarioModel]
  );

  /**
   * It removes a FactMapping (Column) at the given column index toghter with its related Data Cells
   */
  const onColumnDeleted = useCallback(
    (args: { columnIndex: number }) => {
      updateTestScenarioModel((prevState) => {
        /* Retriving the FactMapping (Column to remove). This is required to find its related Data Cell in the 
           Scenarios (Row)*/
        const factMappingToRemove =
          prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings!.FactMapping![
            args.columnIndex + 1
          ];

        /* Cloning the FactMappings list (Columns) and and removing the FactMapping (Column) at given index */
        const deepClonedFactMappings = JSON.parse(
          JSON.stringify(prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings?.FactMapping)
        );
        deepClonedFactMappings.splice(args.columnIndex + 1, 1);

        /* Cloning the Scenario List (Rows) and finding the Cell to remove accordingly to the factMapping data of 
          the remove column */
        const deepClonedScenarios = JSON.parse(
          JSON.stringify(prevState.ScenarioSimulationModel.simulation.scesimData.Scenario ?? [])
        );
        deepClonedScenarios.forEach((scenario: SceSim__ScenarioType) => {
          const factMappingValueColumnIndexToRemove = retrieveColumnIndexbyIdentifiers(
            factMappingToRemove.factIdentifier,
            factMappingToRemove.expressionIdentifier
          )!;

          return {
            factMappingValues: {
              FactMappingValue: scenario.factMappingValues.FactMappingValue!.splice(
                factMappingValueColumnIndexToRemove,
                1
              ),
            },
          };
        });

        return {
          ScenarioSimulationModel: {
            ...prevState.ScenarioSimulationModel,
            simulation: {
              scesimModelDescriptor: {
                factMappings: {
                  FactMapping: deepClonedFactMappings,
                },
              },
              scesimData: {
                Scenario: deepClonedScenarios,
              },
            },
          },
        };
      });
    },
    [retrieveColumnIndexbyIdentifiers, updateTestScenarioModel]
  );

  /**
   * It adds a Scenario (Row) at the given row index
   */
  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      updateTestScenarioModel((prevState) => {
        /* Creating a new Scenario (Row) composed by a list of FactMappingValues. The list order is not relevant. */
        const factMappings =
          prevState.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings?.FactMapping ?? [];
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
    [updateTestScenarioModel]
  );

  /**
   * It deletes a Scenario (Row) at the given row index
   */
  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
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
    [updateTestScenarioModel]
  );

  /**
   * It duplicates a Scenario (Row) at the given row index
   */
  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
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
    [updateTestScenarioModel]
  );

  return (
    <div className={"test-scenario-table"}>
      <StandaloneBeeTable
        allowedOperations={allowedOperations}
        columns={tableColumns.allColumns}
        enableKeyboardNavigation={true}
        headerLevelCountForAppendingRowIndexColumn={2}
        headerVisibility={BeeTableHeaderVisibility.AllLevels}
        isEditableHeader={assetType === TestScenarioType[TestScenarioType.RULE]}
        isReadOnly={false}
        onCellUpdates={onCellUpdates}
        onColumnAdded={onColumnAdded}
        onColumnDeleted={onColumnDeleted}
        onRowAdded={onRowAdded}
        onRowDeleted={onRowDeleted}
        onRowDuplicated={onRowDuplicated}
        operationConfig={simulationOperationConfig}
        resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
        rows={tableRows}
        scrollableParentRef={tableScrollableElementRef.current}
        shouldRenderRowIndexColumn={true}
        shouldShowColumnsInlineControls={true}
        shouldShowRowsInlineControls={true}
      />
    </div>
  );
}

export default TestScenarioTable;
