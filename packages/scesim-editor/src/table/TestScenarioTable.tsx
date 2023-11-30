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
  simulationData,
  updateTestScenarioModel,
}: {
  assetType: string;
  simulationData: SceSim__simulationType;
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

  const retrieveColumnIndexbyIdentifiers = useCallback(
    (factIdentifier: SceSim__factIdentifierType, expressionIdentifier: SceSim__expressionIdentifierType) => {
      return simulationData.scesimModelDescriptor.factMappings!.FactMapping?.findIndex((factMapping) => {
        return (
          factMapping.factIdentifier.name?.__$$text == factIdentifier.name?.__$$text &&
          factMapping.factIdentifier.className?.__$$text == factIdentifier.className?.__$$text &&
          factMapping.expressionIdentifier.name?.__$$text == expressionIdentifier.name?.__$$text &&
          factMapping.expressionIdentifier.type?.__$$text == expressionIdentifier.type?.__$$text
        );
      });
    },
    [simulationData.scesimModelDescriptor.factMappings]
  );

  const simulationColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    const givenFactMappingToFactMap: Map<{ factName: string; factType: string }, SceSim__FactMappingType[]> = new Map();
    const expectFactMappingToFactMap: Map<{ factName: string; factType: string }, SceSim__FactMappingType[]> =
      new Map();
    let descriptionFactMapping: SceSim__FactMappingType | undefined;

    (simulationData.scesimModelDescriptor.factMappings?.FactMapping ?? []).forEach((factMapping) => {
      if (factMapping.expressionIdentifier.type?.__$$text === SimulationTableColumnFieldGroup.GIVEN.toUpperCase()) {
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
      } else if (
        factMapping.expressionIdentifier.type!.__$$text === SimulationTableColumnFieldGroup.EXPECT.toUpperCase()
      ) {
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
        factMapping.expressionIdentifier.type!.__$$text === SimulationTableColumnFieldGroup.OTHER.toUpperCase() &&
        factMapping.expressionIdentifier.name!.__$$text === "Description"
      ) {
        descriptionFactMapping = factMapping;
      }
    });

    const descriptionSection: ReactTable.Column<ROWTYPE> = {
      accessor: descriptionFactMapping!.expressionIdentifier.name!.__$$text,
      groupType: descriptionFactMapping!.expressionIdentifier.type!.__$$text.toLowerCase(),
      id: descriptionFactMapping!.expressionIdentifier.name!.__$$text,
      isRowIndexColumn: false,
      label: descriptionFactMapping!.factAlias.__$$text,
      minWidth: descriptionFactMapping!.columnWidth?.__$$text ?? 300,
      width: descriptionFactMapping!.columnWidth?.__$$text ?? 300,
    };

    const givenSection = {
      accessor: SimulationTableColumnHeaderGroup.GIVEN,
      groupType: SimulationTableColumnHeaderGroup.GIVEN,
      id: SimulationTableColumnHeaderGroup.GIVEN,
      isRowIndexColumn: false,
      label: i18n.table.given.toUpperCase(),
      columns: [...givenFactMappingToFactMap.entries()].map((entry) => {
        return {
          accessor: entry[0].factName,
          dataType: entry[0].factType != "java.lang.Void" ? entry[0].factType : "<Undefined>",
          groupType: SimulationTableColumnInstanceGroup.GIVEN,
          id: entry[0].factName,
          isRowIndexColumn: false,
          label: entry[0].factName,
          columns: entry[1].map((factMapping) => {
            return {
              accessor: factMapping.expressionIdentifier.name!.__$$text,
              dataType:
                factMapping.className!.__$$text != "java.lang.Void" ? factMapping.className!.__$$text : "<Undefined>",
              groupType: factMapping.expressionIdentifier.type!.__$$text.toLowerCase(),
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
      accessor: SimulationTableColumnHeaderGroup.EXPECT,
      groupType: SimulationTableColumnHeaderGroup.EXPECT,
      id: SimulationTableColumnHeaderGroup.EXPECT,
      isRowIndexColumn: false,
      label: i18n.table.expect.toUpperCase(),
      columns: [...expectFactMappingToFactMap.entries()].map((entry) => {
        return {
          accessor: entry[0].factName,
          dataType: entry[0].factType != "java.lang.Void" ? entry[0].factType : "<Undefined>",
          groupType: SimulationTableColumnInstanceGroup.EXPECT,
          id: entry[0].factName,
          isRowIndexColumn: false,
          label: entry[0].factName,
          columns: entry[1].map((factMapping) => {
            return {
              accessor: factMapping.expressionIdentifier.name!.__$$text,
              dataType:
                factMapping.className!.__$$text != "java.lang.Void" ? factMapping.className!.__$$text : "<Undefined>",
              groupType: factMapping.expressionIdentifier.type!.__$$text.toLowerCase(),
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
  }, [
    i18n,
    SimulationTableColumnFieldGroup,
    SimulationTableColumnHeaderGroup,
    SimulationTableColumnInstanceGroup,
    simulationData.scesimModelDescriptor.factMappings?.FactMapping,
  ]);

  /* Retrieving the Rows from the Test Scenario model */
  const simulationRows = useMemo(
    () =>
      (simulationData.scesimData.Scenario ?? []).map((scenario, index) => {
        const factMappingValues = scenario.factMappingValues.FactMappingValue ?? [];

        const tableRow = getColumnsAtLastLevel(simulationColumns, 2).reduce(
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
    [simulationColumns, simulationData.scesimData.Scenario]
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

      const atLeastTwoColumnsOfTheSameGroupType = conditions.column?.groupType
        ? _.groupBy(conditions.columns, (column) => column?.groupType)[conditions.column.groupType].length > 1
        : true;

      const columnCanBeDeleted =
        columnIndex > 0 &&
        atLeastTwoColumnsOfTheSameGroupType &&
        (conditions.columns?.length ?? 0) > 2 && // That's a regular column and the rowIndex column
        (conditions.column?.columns?.length ?? 0) <= 0;

      const columnOperations =
        columnIndex in [0, 1]
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
    [SimulationTableColumnHeaderGroup, simulationRows.length]
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

  const getSectionIndexForGroupType = useCallback(
    (targetColumnIndex: number, selectedColumnIndex: number, groupType: string) => {
      const addingOnTheRight = targetColumnIndex === selectedColumnIndex;
      if (groupType === SimulationTableColumnFieldGroup.EXPECT || groupType === SimulationTableColumnFieldGroup.GIVEN) {
        if (addingOnTheRight) {
          return selectedColumnIndex + 1;
        } else {
          return selectedColumnIndex;
        }
      }
      // if (
      //   groupType === SimulationTableColumnInstanceGroup.EXPECT ||
      //   groupType === SimulationTableColumnInstanceGroup.GIVEN
      // ) {
      //   const selectedFactMapping =
      //     simulationData.scesimModelDescriptor.factMappings!.FactMapping![selectedColumnIndex + 1]; //TODO REMOVE +1
      //   const factMappingsSize = simulationData.scesimModelDescriptor.factMappings!.FactMapping!.length;
      //   const groupType = selectedFactMapping.expressionIdentifier.type!.__$$text;
      //   const instanceName = selectedFactMapping.factIdentifier.name!.__$$text;
      //   const instanceType = selectedFactMapping.factIdentifier.className!.__$$text;

      //   let newInstanceIndex = selectedColumnIndex + 1; //TODO REMOVE +1

      //   console.log("SELECTED");
      //   console.log(selectedFactMapping);

      //   if (addingOnTheRight) {
      //     for (let i = selectedColumnIndex + 1; i < factMappingsSize; i++) {
      //       //TODO REMOVE +1
      //       const currentFM = simulationData.scesimModelDescriptor.factMappings!.FactMapping![i];
      //       console.log("CURRENT");
      //       console.log(currentFM);
      //       if (
      //         currentFM.expressionIdentifier.type!.__$$text === groupType &&
      //         currentFM.factIdentifier.name?.__$$text === instanceName &&
      //         currentFM.factIdentifier.className?.__$$text === instanceType
      //       ) {
      //         continue;
      //       } else {
      //         newInstanceIndex = i;
      //         break;
      //       }
      //     }
      //   } else {
      //     for (let i = selectedColumnIndex + 1; i >= 0; i--) {
      //       //TODO REMOVE +1
      //       const currentFM = simulationData.scesimModelDescriptor.factMappings!.FactMapping![i];
      //       console.log("CURRENT");
      //       console.log(currentFM);

      //       if (
      //         currentFM.expressionIdentifier.type!.__$$text === groupType &&
      //         currentFM.factIdentifier.name?.__$$text === instanceName &&
      //         currentFM.factIdentifier.className?.__$$text === instanceType
      //       ) {
      //         continue;
      //       } else {
      //         newInstanceIndex = i + 1;
      //         break;
      //       }
      //     }
      //   }

      //   return newInstanceIndex;
      // }

      return targetColumnIndex;
    },
    [SimulationTableColumnFieldGroup] //, SimulationTableColumnInstanceGroup, simulationData.scesimModelDescriptor.factMappings]
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
      const selectedIndex = args.currentIndex;

      console.log("AFTER " + args.beforeIndex);
      console.log("SELECTED " + args.currentIndex);
      const sectionIndex = getSectionIndexForGroupType(args.beforeIndex, args.currentIndex, args.groupType);
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
        columns={simulationColumns}
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
        rows={simulationRows}
        scrollableParentRef={tableScrollableElementRef.current}
        shouldRenderRowIndexColumn={true}
        shouldShowColumnsInlineControls={true}
        shouldShowRowsInlineControls={true}
      />
    </div>
  );
}

export default TestScenarioTable;
