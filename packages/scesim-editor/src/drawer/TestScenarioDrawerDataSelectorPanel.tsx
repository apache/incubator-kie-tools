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

import { useCallback, useEffect, useMemo, useState } from "react";

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Button } from "@patternfly/react-core/dist/js/components/Button/";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider/";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { HelpIcon } from "@patternfly/react-icons/dist/esm/icons/help-icon";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar/";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip/";
import { TreeView, TreeViewDataItem, TreeViewSearch } from "@patternfly/react-core/dist/js/components/TreeView/";
import { WarningTriangleIcon } from "@patternfly/react-icons/dist/esm/icons/warning-triangle-icon";

import { SceSimModel } from "@kie-tools/scesim-marshaller";
import {
  SceSim__ExpressionElementType,
  SceSim__FactMappingType,
  SceSim__FactMappingValuesTypes,
  SceSim__expressionElementsType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { TestScenarioDataObject, TestScenarioSelectedColumnMetaData, TestScenarioType } from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";

import { EMPTY_TYPE } from "../common/TestScenarioCommonConstants";
import {
  retrieveFactMappingValueIndexByIdentifiers,
  retrieveModelDescriptor,
  retrieveRowsDataFromModel,
} from "../common/TestScenarioCommonFunctions";

import "./TestScenarioDrawerDataSelectorPanel.css";

const enum TestScenarioDataSelectorState {
  DISABLED, // All subcomponents are DISABLED
  ENABLED, // All subcomponents are ENABLED
  TREEVIEW_ENABLED_ONLY, // TreeView component is enabled only, in a read only mode (when a column is selected)
}

function TestScenarioDataSelectorPanel({
  assetType,
  dataObjects,
  scesimModel,
  selectedColumnMetadata,
  updateTestScenarioModel,
}: {
  assetType: string;
  dataObjects: TestScenarioDataObject[];
  scesimModel: SceSimModel;
  selectedColumnMetadata: TestScenarioSelectedColumnMetaData | null;
  updateTestScenarioModel: React.Dispatch<React.SetStateAction<SceSimModel>>;
}) {
  const { i18n } = useTestScenarioEditorI18n();

  const [allExpanded, setAllExpanded] = useState(false);
  const [dataSelectorStatus, setDataSelectorStatus] = useState(TestScenarioDataSelectorState.DISABLED);
  const [filteredItems, setFilteredItems] = useState({ items: dataObjects, isFiltered: false }); //TODO don't like isFiltered name
  const [treeViewActiveItems, setTreeViewActiveItems] = useState<TreeViewDataItem[]>([]);

  useEffect(() => {
    console.debug("========SELECTOR PANEL USE EFFECT===========");
    console.debug("Selected Column:");
    console.debug(selectedColumnMetadata);
    console.debug("Current Data Objects:");
    console.debug(dataObjects);

    /**
     * Case 1: No columns selected OR a column of OTHER type (eg. Description column).
     * In such a case, the selector status is disabled with no filtered items and no active TreeViewItems
     */
    if (!selectedColumnMetadata || selectedColumnMetadata.factMapping.expressionIdentifier.type?.__$$text == "OTHER") {
      setDataSelectorStatus(TestScenarioDataSelectorState.DISABLED);
      setFilteredItems({ items: dataObjects, isFiltered: false });
      setTreeViewActiveItems([]);
      console.debug("Case 1");
      console.debug("=============USE EFFECT END===============");
      return;
    }

    /**
     * Case 2: A GIVEN / EXPECT column with EMPTY field (2nd level header) is selected.
     * In such a case, the selector status is enabled with no active TreeViewItems and a filtered items list which shows:
     * - All the NOT-assigned fields of the selected column instance
     * - All the NOT-assigned fields of the NOT-ASSIGNED instances, if the selected column doesn't have an instance (1th level header) assigned
     */
    if (selectedColumnMetadata.factMapping.className.__$$text === EMPTY_TYPE) {
      const isFactIdentifierAssigned =
        selectedColumnMetadata.factMapping.factIdentifier.className!.__$$text !== EMPTY_TYPE;

      const testScenarioDescriptor = retrieveModelDescriptor(
        scesimModel.ScenarioSimulationModel,
        selectedColumnMetadata.isBackground
      );
      const assignedExpressionElements = testScenarioDescriptor.factMappings.FactMapping!.map(
        (factMapping) => factMapping.expressionElements!
      );

      let filteredDataObjects: TestScenarioDataObject[] = [];
      if (isFactIdentifierAssigned) {
        const expressionElement = selectedColumnMetadata.factMapping.expressionElements!;

        filteredDataObjects = dataObjects
          .map((object) => JSON.parse(JSON.stringify(object)))
          .filter((dataObject) => !filterDataObjectByExpressionElements(dataObject, [expressionElement]));
        filteredDataObjects.forEach((dataObject) =>
          filterDataObjectChildrenByExpressionElements(dataObject, assignedExpressionElements)
        );
      } else {
        const assignedExpressionElements = testScenarioDescriptor.factMappings.FactMapping!.map(
          (factMapping) => factMapping.expressionElements!
        );

        filteredDataObjects = dataObjects.filter((dataObject) =>
          filterDataObjectByExpressionElements(dataObject, assignedExpressionElements)
        );
      }

      setDataSelectorStatus(TestScenarioDataSelectorState.ENABLED);
      setFilteredItems({ items: filteredDataObjects, isFiltered: isFactIdentifierAssigned });
      setTreeViewActiveItems([]);
      console.debug("Case 2");
      console.debug("Filtered Data Objects:");
      console.debug(filteredDataObjects);
      console.debug("=============================================");
      return;
    }

    /**
     * Case 3 (Final): A GIVEN / EXPECT column with a defined INSTANCE and FIELD
     * In such a case, the selector enabled the TreeView only the Instance and Field of the selected columns is activated TreeViewItems and shown (filtered):
     */
    // const expressionElements = selectedColumnMetadata?.factMapping.expressionElements?.ExpressionElement?.map(
    //   (element) => element.step.__$$text
    // );
    // expressionElements?.splice(0, 1);
    const factIdentifier = selectedColumnMetadata.factMapping.expressionElements!.ExpressionElement![0].step.__$$text;

    const filtered = dataObjects.filter((dataObject) => filterTypesItems(dataObject, factIdentifier));
    const isExpressionType = selectedColumnMetadata.factMapping.factMappingValueType!.__$$text === "EXPRESSION";
    const isSimpleTypeFact = selectedColumnMetadata.factMapping.expressionElements!.ExpressionElement!.length == 1;
    let fieldID: string;
    if (isExpressionType) {
      fieldID = selectedColumnMetadata.factMapping.expressionElements!.ExpressionElement![0].step.__$$text;
    } else if (isSimpleTypeFact) {
      fieldID = selectedColumnMetadata.factMapping.expressionElements!.ExpressionElement![0].step.__$$text.concat(".");
    } else {
      fieldID = selectedColumnMetadata.factMapping
        .expressionElements!.ExpressionElement!.map((expressionElement) => expressionElement.step.__$$text)
        .join(".");
    }

    //TODO 2 This not work with multiple level and expressions fields.
    const treeViewItemToActivate = filtered
      .reduce((acc: TestScenarioDataObject[], item) => {
        //acc.concat(item);
        return item.children ? acc.concat(item.children) : acc;
      }, [])
      .filter((item) => item.id === fieldID);

    setDataSelectorStatus(TestScenarioDataSelectorState.TREEVIEW_ENABLED_ONLY);
    setFilteredItems({ items: filtered, isFiltered: true });
    setTreeViewActiveItems(treeViewItemToActivate);
    console.debug("Case 3");
    console.debug("=============================================");
  }, [dataObjects, scesimModel, selectedColumnMetadata]);

  const findDataObjectRootParent = useCallback((dataObjects: TestScenarioDataObject[], itemId: string) => {
    const filtered = dataObjects
      .map((object) => Object.assign({}, object))
      .filter((item) => filterDataObjectsByID(item, itemId));

    console.log(filtered[0]);

    return filtered[0];
  }, []);

  const isDataObjectRootParent = useCallback((dataObjects: TestScenarioDataObject[], itemID: string) => {
    return dataObjects.map((object) => Object.assign({}, object)).filter((item) => item.id === itemID).length === 1;
  }, []);

  const filterTypesItems = useCallback((dataObject, factIdentifierName) => {
    return dataObject.name === factIdentifierName;
  }, []);

  const filterDataObjectByExpressionElements = useCallback(
    (dataObject: TestScenarioDataObject, allExpressionElements: SceSim__expressionElementsType[]) => {
      let filtered = true;
      for (const expressionElements of allExpressionElements) {
        if (
          !expressionElements ||
          !expressionElements.ExpressionElement ||
          expressionElements.ExpressionElement.length === 0
        ) {
          continue;
        }
        if (expressionElements.ExpressionElement[0].step.__$$text === dataObject.name) {
          filtered = false;
          break;
        }
      }
      return filtered;
    },
    []
  );

  const filterDataObjectChildrenByExpressionElements = useCallback(
    (dataObject: TestScenarioDataObject, allExpressionElements: SceSim__expressionElementsType[]) => {
      if (dataObject.children) {
        for (const expressionElements of allExpressionElements) {
          if (
            !expressionElements ||
            !expressionElements.ExpressionElement ||
            expressionElements.ExpressionElement.length === 0
          ) {
            continue;
          }
          if (expressionElements.ExpressionElement[0].step.__$$text === dataObject.name) {
            const selected: TestScenarioDataObject[] = dataObject.children.filter(
              (dataObjectChild) => dataObjectChild.name !== expressionElements.ExpressionElement!.at(-1)!.step.__$$text
            );
            dataObject.children = selected;
          }
        }
      }
    },
    []
  );

  const filterItems = useCallback((item, input) => {
    if (item.name.toLowerCase().includes(input.toLowerCase())) {
      return true;
    }

    if (item.children) {
      return (
        (item.children = item.children
          .map((object: TestScenarioDataObject) => Object.assign({}, object))
          .filter((child: TestScenarioDataObject) => filterItems(child, input))).length > 0
      );
    }
  }, []);

  const filterDataObjectsByID = useCallback((item, itemID) => {
    if (item.id === itemID) {
      return true;
    }

    if (item.children) {
      return (
        (item.children = item.children
          .map((object: TestScenarioDataObject) => Object.assign({}, object))
          .filter((child: TestScenarioDataObject) => filterDataObjectsByID(child, itemID))).length > 0
      );
    }

    return false;
  }, []);

  const treeViewEmptyStatus = useMemo(() => {
    const isTreeViewNotEmpty = dataObjects.length > 0 && filteredItems.items.length > 0;
    const treeViewEmptyIcon = filteredItems.items.length === 0 ? WarningTriangleIcon : WarningTriangleIcon;
    const title =
      dataObjects.length === 0
        ? assetType === TestScenarioType[TestScenarioType.DMN]
          ? i18n.drawer.dataSelector.emptyDataObjectsTitleDMN
          : i18n.drawer.dataSelector.emptyDataObjectsTitleRule
        : "No more properties";
    const description =
      dataObjects.length === 0
        ? assetType === TestScenarioType[TestScenarioType.DMN]
          ? i18n.drawer.dataSelector.emptyDataObjectsDescriptionDMN
          : i18n.drawer.dataSelector.emptyDataObjectsDescriptionRule
        : "All the properties have been already assigned";

    {
      assetType === TestScenarioType[TestScenarioType.DMN]
        ? i18n.drawer.dataSelector.emptyDataObjectsTitleDMN
        : i18n.drawer.dataSelector.emptyDataObjectsTitleRule;
    }

    return { description: description, enabled: isTreeViewNotEmpty, icon: treeViewEmptyIcon, title: title };
  }, [dataObjects, filteredItems, i18n]);

  const insertDataObjectButtonStatus = useMemo(() => {
    const propertyID = selectedColumnMetadata?.factMapping.expressionElements?.ExpressionElement?.map(
      (expressionElement) => expressionElement.step.__$$text
    ).join(".");
    if (selectedColumnMetadata == null) {
      return {
        message: i18n.drawer.dataSelector.insertDataObjectTooltipColumnSelectionMessage,
        enabled: false,
      };
    } else if (treeViewActiveItems.length != 1) {
      return { message: i18n.drawer.dataSelector.insertDataObjectTooltipDataObjectSelectionMessage, enabled: false };
    } else if (treeViewActiveItems.length == 1 && treeViewActiveItems[0].id === propertyID) {
      return {
        message: i18n.drawer.dataSelector.insertDataObjectTooltipDataObjectAlreadyAssignedMessage,
        enabled: false,
      };
    } else {
      return { message: i18n.drawer.dataSelector.insertDataObjectTooltipDataObjectAssignMessage, enabled: true };
    }
  }, [i18n, selectedColumnMetadata, treeViewActiveItems]);

  const onAllExpandedToggle = useCallback((_event) => {
    setAllExpanded((prev) => !prev);
  }, []);

  // CHECK
  const onInsertDataObjectClick = useCallback(
    /** TODO 1 : NEED A POPUP ASKING IF WE WANT TO REPLACE VALUES OR NOT */

    () => {
      updateTestScenarioModel((prevState) => {
        const isBackground = selectedColumnMetadata!.isBackground;
        const factMappings = retrieveModelDescriptor(prevState.ScenarioSimulationModel, isBackground).factMappings
          .FactMapping!;
        const deepClonedFactMappings = JSON.parse(JSON.stringify(factMappings));
        const isRootType = isDataObjectRootParent(dataObjects, treeViewActiveItems[0].id!.toString());
        const rootDataObject = findDataObjectRootParent(dataObjects, treeViewActiveItems[0].id!.toString());

        const className = treeViewActiveItems[0].customBadgeContent!.toString();
        const expressionAlias = isRootType ? "Expression </>" : treeViewActiveItems[0].name!.toString();
        const expressionElementsSteps = treeViewActiveItems[0].id!.split(".").filter((step) => !!step.trim()); //WARNING !!!! THIS DOESN'T WORK WITH IMPORTED DATA OBJECTS
        const factName = treeViewActiveItems[0].id?.split(".")[0]!; //WARNING !!!! THIS DOESN'T WORK WITH IMPORTED DATA OBJECTS
        const factClassName = isRootType
          ? treeViewActiveItems[0].customBadgeContent!.toString()
          : rootDataObject.customBadgeContent!.toString();
        const factMappingValueType = isRootType ? "EXPRESSION" : "NOT_EXPRESSION";

        const factMappingToUpdate: SceSim__FactMappingType = deepClonedFactMappings[selectedColumnMetadata!.index];
        factMappingToUpdate.className = { __$$text: className };
        factMappingToUpdate.factAlias = { __$$text: factName };
        factMappingToUpdate.factIdentifier.className = { __$$text: factClassName };
        factMappingToUpdate.factIdentifier.name = { __$$text: factName };
        factMappingToUpdate.factMappingValueType = { __$$text: factMappingValueType };
        factMappingToUpdate.expressionAlias = { __$$text: expressionAlias };
        factMappingToUpdate.expressionElements = {
          ExpressionElement: expressionElementsSteps.map((ee) => {
            return { step: { __$$text: ee } };
          }),
        };

        const deepClonedRowsData: SceSim__FactMappingValuesTypes[] = JSON.parse(
          JSON.stringify(retrieveRowsDataFromModel(prevState.ScenarioSimulationModel, isBackground))
        );

        deepClonedRowsData.forEach((fmv, index) => {
          const factMappingValues = fmv.factMappingValues.FactMappingValue!;
          const newFactMappingValues = [...factMappingValues];

          const factMappingValueToUpdateIndex = retrieveFactMappingValueIndexByIdentifiers(
            newFactMappingValues,
            selectedColumnMetadata!.factMapping.factIdentifier,
            selectedColumnMetadata!.factMapping.expressionIdentifier
          );
          const factMappingValueToUpdate = factMappingValues[factMappingValueToUpdateIndex];
          newFactMappingValues[factMappingValueToUpdateIndex] = {
            ...factMappingValueToUpdate,
            factIdentifier: { className: { __$$text: factClassName }, name: { __$$text: factName } },
            // rawValue: {
            //   __$$text: update.value,  //TODO 1 related
            // },
          };

          deepClonedRowsData[index].factMappingValues.FactMappingValue = newFactMappingValues;
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
    [retrieveModelDescriptor, selectedColumnMetadata, treeViewActiveItems, updateTestScenarioModel]
  );

  const onSearchTreeView = useCallback(
    (event) => {
      const input: string = event.target.value;
      if (input.trim() === "") {
        setFilteredItems({ items: dataObjects, isFiltered: false });
      } else {
        const filtered = dataObjects.filter((item) => filterItems(item, input));

        setFilteredItems({ items: filtered, isFiltered: true });
      }
    },
    [dataObjects, filterItems]
  );

  const onSelectTreeViewItem = useCallback((_event, treeViewItem: TreeViewDataItem) => {
    setTreeViewActiveItems([treeViewItem]);
  }, []);

  const treeViewSearchToolbar = (
    <Toolbar style={{ padding: 0 }}>
      <ToolbarContent style={{ padding: 0 }}>
        <ToolbarItem widths={{ default: "100%" }}>
          <TreeViewSearch
            onSearch={onSearchTreeView}
            id="input-search"
            name="search-input"
            disabled={dataSelectorStatus !== TestScenarioDataSelectorState.ENABLED}
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );

  return (
    <>
      <Text className="kie-scesim-editor-drawer-data-objects--text">
        {assetType === TestScenarioType[TestScenarioType.DMN]
          ? i18n.drawer.dataSelector.descriptionDMN
          : i18n.drawer.dataSelector.descriptionRule}
        <Tooltip
          content={
            assetType === TestScenarioType[TestScenarioType.DMN]
              ? i18n.drawer.dataSelector.dataObjectsDescriptionDMN
              : i18n.drawer.dataSelector.dataObjectsDescriptionRule
          }
        >
          <Icon className={"kie-scesim-editor-drawer-data-objects--info-icon"} size="sm" status="info">
            <HelpIcon />
          </Icon>
        </Tooltip>
      </Text>
      <Divider />
      <div className={"kie-scesim-editor-drawer-data-objects--selector"}>
        {treeViewEmptyStatus.enabled ? (
          <div aria-disabled={true}>
            <TreeView
              activeItems={treeViewActiveItems}
              allExpanded={allExpanded || filteredItems.isFiltered}
              className={
                dataSelectorStatus !== TestScenarioDataSelectorState.DISABLED
                  ? undefined
                  : "kie-scesim-editor-drawer-data-objects--selector-disabled"
              }
              data={filteredItems.items}
              hasBadges
              hasSelectableNodes
              onSelect={onSelectTreeViewItem}
              toolbar={treeViewSearchToolbar}
            />
          </div>
        ) : (
          <Bullseye>
            <EmptyState>
              <EmptyStateIcon icon={treeViewEmptyStatus.icon} />
              <Title headingLevel="h4" size="lg">
                {treeViewEmptyStatus.title}
              </Title>
              <EmptyStateBody>{treeViewEmptyStatus.description}</EmptyStateBody>
            </EmptyState>
          </Bullseye>
        )}
      </div>
      <Divider />
      <div className={"kie-scesim-editor-drawer-data-objects--button-container"}>
        <Tooltip content={insertDataObjectButtonStatus.message}>
          <Button
            isAriaDisabled={!insertDataObjectButtonStatus.enabled}
            onClick={onInsertDataObjectClick}
            variant="primary"
          >
            {i18n.drawer.dataSelector.insertDataObject}
          </Button>
        </Tooltip>
        <Button
          isDisabled={treeViewActiveItems.length !== 1 || dataSelectorStatus !== TestScenarioDataSelectorState.ENABLED}
          onClick={() => setTreeViewActiveItems([])}
          variant="secondary"
        >
          {i18n.drawer.dataSelector.clearSelection}
        </Button>
        <Button
          isDisabled={
            filteredItems.items.length < 1 ||
            filteredItems.isFiltered ||
            dataSelectorStatus !== TestScenarioDataSelectorState.ENABLED
          }
          onClick={onAllExpandedToggle}
          variant="link"
        >
          {allExpanded ? i18n.drawer.dataSelector.collapseAll : i18n.drawer.dataSelector.expandAll}
        </Button>
      </div>
    </>
  );
}

export default TestScenarioDataSelectorPanel;
