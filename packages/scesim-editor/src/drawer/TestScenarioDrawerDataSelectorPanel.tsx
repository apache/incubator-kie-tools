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
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar/";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip/";
import { TreeView, TreeViewDataItem, TreeViewSearch } from "@patternfly/react-core/dist/js/components/TreeView/";
import { WarningTriangleIcon } from "@patternfly/react-icons/dist/esm/icons/warning-triangle-icon";

import { SceSim__expressionElementsType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { useTestScenarioEditorI18n } from "../i18n";

import "./TestScenarioDrawerDataSelectorPanel.css";
import { useExternalModels } from "../externalModels/TestScenarioEditorDependenciesContext";
import { useTestScenarioEditorStore, useTestScenarioEditorStoreApi } from "../store/TestScenarioStoreContext";
import { TestScenarioDataObject, TestScenarioEditorTab } from "../store/TestScenarioEditorStore";
import { updateColumn } from "../mutations/updateColumn";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner/Spinner";

const enum TestScenarioDataSelectorState {
  DISABLED, // All subcomponents are DISABLED
  ENABLED, // All subcomponents are ENABLED
  TREEVIEW_ENABLED_ONLY, // TreeView component is enabled only, in a read only mode (when a column is selected)
}

function TestScenarioDataSelectorPanel() {
  const { i18n } = useTestScenarioEditorI18n();
  const { externalModelsByNamespace } = useExternalModels();
  const dataObjects = useTestScenarioEditorStore((state) =>
    state.computed(state).getDataObjects(externalModelsByNamespace)
  );
  const scesimModel = useTestScenarioEditorStore((state) => state.scesim.model);
  const tableStatus = useTestScenarioEditorStore((state) => state.table);
  const tabStatus = useTestScenarioEditorStore((state) => state.navigation.tab);
  const testScenarioEditorStoreApi = useTestScenarioEditorStoreApi();
  const testScenarioType = scesimModel.ScenarioSimulationModel.settings.type?.__$$text.toUpperCase();
  const referencedDmnNamespace = scesimModel.ScenarioSimulationModel.settings.dmnNamespace?.__$$text;

  const selectedColumnMetadata =
    tabStatus === TestScenarioEditorTab.SIMULATION
      ? tableStatus.simulation.selectedColumn
      : tableStatus.background.selectedColumn;

  const [allExpanded, setAllExpanded] = useState(false);
  const [dataSelectorStatus, setDataSelectorStatus] = useState(TestScenarioDataSelectorState.DISABLED);
  const [filteredItems, setFilteredItems] = useState(dataObjects);
  const [treeViewStatus, setTreeViewStatus] = useState({
    activeItems: [] as TreeViewDataItem[],
    searchKey: "",
    isExpanded: false,
  });

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

  const findDataObjectRootParent = useCallback(
    (dataObjects: TestScenarioDataObject[], itemId: string) => {
      const filtered = dataObjects
        .map((object) => Object.assign({}, object))
        .filter((item) => filterDataObjectsByID(item, itemId));

      return filtered[0];
    },
    [filterDataObjectsByID]
  );

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
            const selected: TestScenarioDataObject[] = dataObject.children.filter((dataObjectChild) => {
              const lastElementName = expressionElements.ExpressionElement!.at(-1)!.step.__$$text;
              const isSimpleType = expressionElements.ExpressionElement!.length === 1;
              const fieldName = isSimpleType ? "value" : lastElementName;

              return dataObjectChild.name !== fieldName;
            });
            dataObject.children = selected;
          }
        }
      }
    },
    []
  );

  /** It filters out all the Data Objects and their Children already assigned in the table */
  const filterOutAlreadyAssignedDataObjectsAndChildren = useCallback(
    (expressionElement: SceSim__expressionElementsType, isBackground: boolean) => {
      const testScenarioDescriptor = isBackground
        ? scesimModel.ScenarioSimulationModel.background.scesimModelDescriptor
        : scesimModel.ScenarioSimulationModel.simulation.scesimModelDescriptor;
      const assignedExpressionElements = testScenarioDescriptor.factMappings.FactMapping!.map(
        (factMapping) => factMapping.expressionElements!
      );

      const filteredDataObjects: TestScenarioDataObject[] = dataObjects
        .map((object) => Object.assign({}, object)) // Deep copy: the Objects may mutate due to children filtering
        .filter((dataObject) => !filterDataObjectByExpressionElements(dataObject, [expressionElement]));
      filteredDataObjects.forEach((dataObject) =>
        filterDataObjectChildrenByExpressionElements(dataObject, assignedExpressionElements)
      );
      return filteredDataObjects;
    },
    [
      dataObjects,
      filterDataObjectByExpressionElements,
      filterDataObjectChildrenByExpressionElements,
      scesimModel.ScenarioSimulationModel,
    ]
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

  /* It returns the TestScenarioDataObject that matches the provided ID serching over all TestScenarioDataObjects and its children */
  const findTestScenarioDataObjectById = useCallback(
    (dataObjects: TestScenarioDataObject[], id: string): TestScenarioDataObject => {
      let dataObjectToReturn = dataObjects.find((dataObject) => id.startsWith(dataObject.id));
      if (dataObjectToReturn?.id !== id && dataObjectToReturn?.children) {
        dataObjectToReturn = findTestScenarioDataObjectById(dataObjectToReturn.children, id);
      }
      return dataObjectToReturn!;
    },
    []
  );

  useEffect(() => {
    console.debug("========SELECTOR PANEL USE EFFECT===========");
    console.debug("Selected Column:");
    console.debug(selectedColumnMetadata);
    console.debug("All Data Objects:");
    console.debug(dataObjects);

    /**
     * Case 1: No columns selected OR a column of OTHER type (eg. Description column).
     * In such a case, the selector status is disabled with no filtered items and no active TreeViewItems
     */
    if (!selectedColumnMetadata || selectedColumnMetadata.factMapping.expressionIdentifier.type?.__$$text == "OTHER") {
      setDataSelectorStatus(TestScenarioDataSelectorState.DISABLED);
      setFilteredItems(dataObjects);
      setTreeViewStatus({ activeItems: [], searchKey: "", isExpanded: false });
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
    if (selectedColumnMetadata.factMapping.className.__$$text === "java.lang.Void") {
      const isFactIdentifierAssigned =
        selectedColumnMetadata.factMapping.factIdentifier.className!.__$$text !== "java.lang.Void";

      let filteredDataObjects: TestScenarioDataObject[] = [];
      if (isFactIdentifierAssigned) {
        const expressionElement = selectedColumnMetadata.factMapping.expressionElements!;
        filteredDataObjects = filterOutAlreadyAssignedDataObjectsAndChildren(
          expressionElement,
          selectedColumnMetadata.isBackground
        );
      } else {
        const testScenarioDescriptor = selectedColumnMetadata.isBackground
          ? scesimModel.ScenarioSimulationModel.background.scesimModelDescriptor
          : scesimModel.ScenarioSimulationModel.simulation.scesimModelDescriptor;
        const assignedExpressionElements = testScenarioDescriptor.factMappings.FactMapping!.map(
          (factMapping) => factMapping.expressionElements!
        );

        filteredDataObjects = dataObjects
          .map((object) => Object.assign({}, object)) // Deep copy: the Objects may mutate due to children filtering
          .filter((dataObject) => filterDataObjectByExpressionElements(dataObject, assignedExpressionElements));
      }

      /** Applying User search key to the filteredDataObjects, if present */
      const isUserFilterPresent = treeViewStatus.searchKey.trim() !== "";
      if (isUserFilterPresent) {
        filteredDataObjects = filteredDataObjects.filter((item) => filterItems(item, treeViewStatus.searchKey));
      }

      setDataSelectorStatus(TestScenarioDataSelectorState.ENABLED);
      setFilteredItems(filteredDataObjects);
      setTreeViewStatus((prev) => {
        return {
          ...prev,
          activeItems: [],
          isExpanded: isFactIdentifierAssigned || isUserFilterPresent,
        };
      });
      console.debug("Case 2");
      console.debug("Filtered Data Objects:");
      console.debug(filteredDataObjects);
      console.debug("=============USE EFFECT END===============");
      return;
    }

    /**
     * Case 3 (Final): A GIVEN / EXPECT column with a defined INSTANCE and FIELD
     * In such a case, the selector enabled the TreeView only the Instance and Field of the selected columns is activated TreeViewItems and shown (filtered):
     */
    const factIdentifier = selectedColumnMetadata.factMapping.expressionElements!.ExpressionElement![0].step.__$$text;
    const filteredDataObjects = dataObjects.filter((dataObject) => filterTypesItems(dataObject, factIdentifier));
    const isExpressionType = selectedColumnMetadata.factMapping.factMappingValueType!.__$$text === "EXPRESSION";
    const isSimpleTypeFact =
      selectedColumnMetadata.factMapping.expressionElements!.ExpressionElement!.length === 1 &&
      selectedColumnMetadata.factMapping.className.__$$text !== "java.lang.Void";
    let fieldID: string;
    if (isExpressionType) {
      fieldID = selectedColumnMetadata.factMapping.expressionElements!.ExpressionElement![0].step.__$$text;
    } else if (isSimpleTypeFact) {
      fieldID = selectedColumnMetadata.factMapping
        .expressionElements!.ExpressionElement![0].step.__$$text.concat(".")
        .concat("value");
    } else {
      fieldID = selectedColumnMetadata.factMapping
        .expressionElements!.ExpressionElement!.map((expressionElement) => expressionElement.step.__$$text)
        .join(".");
    }

    const treeViewItemToActivate = findTestScenarioDataObjectById(filteredDataObjects, fieldID)!;

    setDataSelectorStatus(TestScenarioDataSelectorState.TREEVIEW_ENABLED_ONLY);
    setFilteredItems(filteredDataObjects);
    setTreeViewStatus({ activeItems: [treeViewItemToActivate], searchKey: "", isExpanded: true });
    console.debug("Case 3");
    console.debug("=============USE EFFECT END===============");
  }, [
    dataObjects,
    filterDataObjectByExpressionElements,
    filterDataObjectChildrenByExpressionElements,
    filterOutAlreadyAssignedDataObjectsAndChildren,
    filterItems,
    filterTypesItems,
    findTestScenarioDataObjectById,
    scesimModel,
    selectedColumnMetadata,
    treeViewStatus.searchKey,
  ]);

  const treeViewEmptyStatus = useMemo(() => {
    const isReferencedFileLoaded =
      testScenarioType === "RULE" || externalModelsByNamespace?.has(referencedDmnNamespace!);
    const isTreeViewNotEmpty = dataObjects.length > 0;
    const treeViewVisibleStatus = isReferencedFileLoaded ? (isTreeViewNotEmpty ? "visible" : "hidden") : "loading";
    const treeViewEmptyIcon = filteredItems.length === 0 ? WarningTriangleIcon : WarningTriangleIcon;
    const title =
      dataObjects.length === 0
        ? testScenarioType === "DMN"
          ? i18n.drawer.dataSelector.emptyDataObjectsTitleDMN
          : i18n.drawer.dataSelector.emptyDataObjectsTitleRule
        : i18n.drawer.dataSelector.emptyDataObjectsTitle;
    const description =
      dataObjects.length === 0
        ? testScenarioType === "DMN"
          ? i18n.drawer.dataSelector.emptyDataObjectsDescriptionDMN
          : i18n.drawer.dataSelector.emptyDataObjectsDescriptionRule
        : i18n.drawer.dataSelector.emptyDataObjectsDescription;

    {
      testScenarioType === "DMN"
        ? i18n.drawer.dataSelector.emptyDataObjectsTitleDMN
        : i18n.drawer.dataSelector.emptyDataObjectsTitleRule;
    }

    return { description: description, icon: treeViewEmptyIcon, title: title, visibility: treeViewVisibleStatus };
  }, [
    externalModelsByNamespace,
    referencedDmnNamespace,
    filteredItems.length,
    dataObjects.length,
    testScenarioType,
    i18n.drawer.dataSelector,
  ]);

  const insertDataObjectButtonStatus = useMemo(() => {
    if (!selectedColumnMetadata) {
      return {
        message: i18n.drawer.dataSelector.insertDataObjectTooltipColumnSelectionMessage,
        enabled: false,
      };
    }

    const oneActiveTreeViewItem = treeViewStatus.activeItems.length === 1;
    if (!oneActiveTreeViewItem) {
      return { message: i18n.drawer.dataSelector.insertDataObjectTooltipDataObjectSelectionMessage, enabled: false };
    }

    const expressionElement = selectedColumnMetadata.factMapping.expressionElements!;

    const filteredDataObjects: TestScenarioDataObject[] = filterOutAlreadyAssignedDataObjectsAndChildren(
      expressionElement,
      selectedColumnMetadata.isBackground
    );
    const isAlreadyAssigined =
      filteredDataObjects.length === 1 &&
      !filteredDataObjects[0].children?.find((child) => child.id === treeViewStatus.activeItems[0].id);

    if (oneActiveTreeViewItem && isAlreadyAssigined) {
      return {
        message: i18n.drawer.dataSelector.insertDataObjectTooltipDataObjectAlreadyAssignedMessage,
        enabled: false,
      };
    }

    return { message: i18n.drawer.dataSelector.insertDataObjectTooltipDataObjectAssignMessage, enabled: true };
  }, [filterOutAlreadyAssignedDataObjectsAndChildren, i18n, selectedColumnMetadata, treeViewStatus.activeItems]);

  const onAllExpandedToggle = useCallback((_event) => {
    setAllExpanded((prev) => !prev);
  }, []);

  const onInsertDataObjectClick = useCallback(() => {
    const isBackground = selectedColumnMetadata!.isBackground;
    const selectedTestScenarioDataObject = findTestScenarioDataObjectById(
      dataObjects,
      treeViewStatus.activeItems[0].id!
    )!;
    const rootSelectedTestScenarioDataObject = findDataObjectRootParent(
      dataObjects,
      treeViewStatus.activeItems[0].id!.toString()
    );
    const isRootType = isDataObjectRootParent(dataObjects, treeViewStatus.activeItems[0].id!.toString());
    const className = selectedTestScenarioDataObject.className!;
    const expressionAlias = isRootType ? "expression </>" : selectedTestScenarioDataObject.name;
    const expressionElementsSteps = selectedTestScenarioDataObject.expressionElements?.filter((step) => !!step.trim());
    const factName = rootSelectedTestScenarioDataObject.name;
    const factClassName = isRootType
      ? selectedTestScenarioDataObject.className!
      : rootSelectedTestScenarioDataObject.className!;
    const factMappingValueType = isRootType ? "EXPRESSION" : "NOT_EXPRESSION";

    testScenarioEditorStoreApi.setState((state) => {
      const factMappings = isBackground
        ? state.scesim.model.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping!
        : state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
      const factMappingValuesTypes = isBackground
        ? state.scesim.model.ScenarioSimulationModel.background.scesimData.BackgroundData!
        : state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!;

      const { updatedFactMapping } = updateColumn({
        className: className,
        expressionAlias: expressionAlias,
        expressionElementsSteps: expressionElementsSteps,
        expressionIdentifierName: selectedColumnMetadata!.factMapping.expressionIdentifier.name?.__$$text,
        expressionIdentifierType: selectedColumnMetadata!.factMapping.expressionIdentifier.type?.__$$text,
        factMappings: factMappings,
        factClassName: factClassName,
        factIdentifierClassName: selectedColumnMetadata!.factMapping.factIdentifier.className?.__$$text,
        factIdentifierName: selectedColumnMetadata!.factMapping.factIdentifier.name?.__$$text,
        factMappingValuesTypes: factMappingValuesTypes,
        factMappingValueType: factMappingValueType,
        factName: factName,
        selectedColumnIndex: selectedColumnMetadata!.index,
      });

      state.dispatch(state).table.updateSelectedColumn({
        factMapping: updatedFactMapping,
        index: selectedColumnMetadata!.index,
        isBackground: isBackground,
      });
    });
  }, [
    dataObjects,
    findDataObjectRootParent,
    findTestScenarioDataObjectById,
    isDataObjectRootParent,
    selectedColumnMetadata,
    testScenarioEditorStoreApi,
    treeViewStatus.activeItems,
  ]);

  const onClearSelectionClicked = useCallback((_event) => {
    setTreeViewStatus((prev) => {
      return {
        ...prev,
        activeItems: [],
      };
    });
  }, []);

  const onSearchTreeView = useCallback(
    (event) =>
      setTreeViewStatus((prev) => {
        return {
          ...prev,
          searchKey: event.target.value,
        };
      }),
    []
  );

  const onSelectTreeViewItem = useCallback((_event, treeViewItem: TreeViewDataItem) => {
    setTreeViewStatus((prev) => {
      return {
        ...prev,
        activeItems: [treeViewItem],
      };
    });
  }, []);

  const treeViewSearchToolbar = (
    <Toolbar style={{ padding: 0 }}>
      <ToolbarContent style={{ padding: 0 }}>
        <ToolbarItem widths={{ default: "100%" }}>
          <TreeViewSearch
            disabled={dataSelectorStatus !== TestScenarioDataSelectorState.ENABLED}
            id="input-search"
            name="search-input"
            onSearch={onSearchTreeView}
            value={treeViewStatus.searchKey}
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );

  return (
    <Stack>
      <StackItem>
        <Text className="kie-scesim-editor-drawer-data-objects--text">
          {testScenarioType === "DMN"
            ? i18n.drawer.dataSelector.descriptionDMN
            : i18n.drawer.dataSelector.descriptionRule}
          <Tooltip
            content={
              testScenarioType === "DMN"
                ? i18n.drawer.dataSelector.dataObjectsDescriptionDMN
                : i18n.drawer.dataSelector.dataObjectsDescriptionRule
            }
          >
            <Icon className={"kie-scesim-editor-drawer-data-objects--info-icon"} size="sm" status="info">
              <HelpIcon />
            </Icon>
          </Tooltip>
        </Text>
      </StackItem>
      <Divider />
      <StackItem isFilled>
        <div className={"kie-scesim-editor-drawer-data-objects--selector"}>
          {(treeViewEmptyStatus.visibility === "visible" && (
            <div aria-disabled={true}>
              <TreeView
                activeItems={treeViewStatus.activeItems}
                allExpanded={allExpanded || treeViewStatus.isExpanded}
                className={
                  dataSelectorStatus !== TestScenarioDataSelectorState.DISABLED
                    ? undefined
                    : "kie-scesim-editor-drawer-data-objects--selector-disabled"
                }
                data={filteredItems}
                hasBadges
                hasSelectableNodes
                onSelect={onSelectTreeViewItem}
                toolbar={treeViewSearchToolbar}
              />
            </div>
          )) ||
            (treeViewEmptyStatus.visibility === "hidden" && (
              <Bullseye>
                <EmptyState>
                  <EmptyStateIcon icon={treeViewEmptyStatus.icon} />
                  <Title headingLevel="h4" size="lg">
                    {treeViewEmptyStatus.title}
                  </Title>
                  <EmptyStateBody>{treeViewEmptyStatus.description}</EmptyStateBody>
                </EmptyState>
              </Bullseye>
            )) ||
            (treeViewEmptyStatus.visibility === "loading" && (
              <Bullseye style={{ paddingTop: "10px" }}>
                <Spinner aria-label="Contents of the basic example" />
              </Bullseye>
            ))}
        </div>
      </StackItem>
      <Divider />
      <StackItem>
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
            isDisabled={
              treeViewStatus.activeItems.length !== 1 || dataSelectorStatus !== TestScenarioDataSelectorState.ENABLED
            }
            onClick={onClearSelectionClicked}
            variant="secondary"
          >
            {i18n.drawer.dataSelector.clearSelection}
          </Button>
          <Button
            isDisabled={
              filteredItems.length < 1 ||
              treeViewStatus.isExpanded ||
              dataSelectorStatus !== TestScenarioDataSelectorState.ENABLED
            }
            onClick={onAllExpandedToggle}
            variant="link"
          >
            {allExpanded ? i18n.drawer.dataSelector.collapseAll : i18n.drawer.dataSelector.expandAll}
          </Button>
        </div>
      </StackItem>
    </Stack>
  );
}

export default TestScenarioDataSelectorPanel;
