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
import { cloneDeep } from "lodash";

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

/* It checks if the given root Data Object is already assigned */
const isRootDataObjectAssignable = (
  dataObject: TestScenarioDataObject,
  assignedExpressionElements: SceSim__expressionElementsType[]
) => {
  //TODO change this
  let filtered = true;
  for (const expressionElements of assignedExpressionElements) {
    if (
      !expressionElements ||
      !expressionElements.ExpressionElement ||
      expressionElements.ExpressionElement.length === 0
    ) {
      continue;
    }
    if (expressionElements.ExpressionElement[0].step.__$$text === dataObject.expressionElements[0]) {
      filtered = false;
      break;
    }
  }
  return filtered;
};

const filterDataObjectChildrenByExpressionElements = (
  dataObject: TestScenarioDataObject,
  allExpressionElements: string[] | undefined
) => {
  if (dataObject.children) {
    const filteredChildren: TestScenarioDataObject[] = dataObject.children.filter((dataObjectChild) =>
      filterDataObjectChildrenByExpressionElements(dataObjectChild, allExpressionElements)
    );
    dataObject.children = filteredChildren;
    return filteredChildren && filteredChildren.length > 0;
  }
  {
    return !allExpressionElements?.includes(dataObject.expressionElements.join("."));
  }
};

const filterDataObjectsById = (item: TestScenarioDataObject, itemId: string) => {
  if (item.id === itemId) {
    return true;
  }

  if (item.children) {
    const dataObjects: TestScenarioDataObject[] = item.children
      .map((object: TestScenarioDataObject) => Object.assign({}, object))
      .filter((child: TestScenarioDataObject) => filterDataObjectsById(child, itemId));

    return dataObjects.length > 0;
  }

  return false;
};

const filterDataObjectsByName = (item: TestScenarioDataObject, name: string) => {
  if (item.name.toLowerCase().includes(name.toLowerCase())) {
    return true;
  }

  if (item.children) {
    const dataObjects: TestScenarioDataObject[] = item.children
      .map((object: TestScenarioDataObject) => Object.assign({}, object))
      .filter((child: TestScenarioDataObject) => filterDataObjectsByName(child, name));

    return dataObjects.length > 0;
  }

  return false;
};

const findDataObjectRootParent = (dataObjects: TestScenarioDataObject[], itemId: string) => {
  const filtered = dataObjects
    .map((object) => Object.assign({}, object))
    .filter((item) => filterDataObjectsById(item, itemId));

  return filtered[0];
};

const filterTypesItems = (dataObject: TestScenarioDataObject, factIdentifierName: string) => {
  return dataObject.name === factIdentifierName;
};

/* It returns the TestScenarioDataObject that matches the provided ID serching over all TestScenarioDataObjects and its children */
function findTestScenarioDataObjectById(
  testScenarioDataObjects: TestScenarioDataObject[],
  id: string
): TestScenarioDataObject | undefined {
  for (const testScenarioDataObject of testScenarioDataObjects) {
    if (testScenarioDataObject.id === id) {
      return testScenarioDataObject;
    } else if (testScenarioDataObject.children) {
      const testScenarioDataObjectChild = findTestScenarioDataObjectById(testScenarioDataObject.children, id);
      if (testScenarioDataObjectChild) {
        return testScenarioDataObjectChild;
      }
    }
  }
  return undefined;
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

  /** It filters out all the Data Objects and their Children already assigned in the table */
  const filterOutAlreadyAssignedDataObjectsAndChildren = useCallback(
    (selectedColumnExpressionElement: SceSim__expressionElementsType | undefined, isBackground: boolean) => {
      const testScenarioDescriptor = isBackground
        ? scesimModel.ScenarioSimulationModel.background.scesimModelDescriptor
        : scesimModel.ScenarioSimulationModel.simulation.scesimModelDescriptor;
      const assignedExpressionElements = testScenarioDescriptor.factMappings.FactMapping!.map(
        (factMapping) => factMapping.expressionElements!
      );

      const assignedExpressionElements2 = testScenarioDescriptor.factMappings
        .FactMapping!.filter(
          (factMapping) => factMapping.expressionElements && factMapping.expressionElements.ExpressionElement
        )
        .map((factMapping) =>
          factMapping
            .expressionElements!.ExpressionElement!.map((expressionElement) => expressionElement.step.__$$text)
            .join(".")
        )
        .filter((asd) => asd);

      let filteredDataObjects: TestScenarioDataObject[] = [];

      // An Empty column has been selected. Filtering out all assigined Instances
      if (!selectedColumnExpressionElement || selectedColumnExpressionElement?.ExpressionElement?.length === 0) {
        filteredDataObjects = dataObjects
          .map((object) => cloneDeep(object)) // Deep copy: the Objects may mutate due to children filtering
          .filter((dataObject) => isRootDataObjectAssignable(dataObject, assignedExpressionElements));
      } else {
        // In case of not empty column, it keeps the selected root Fact Mapping (Instance) and then filtering out the already
        // assigned children Data Objects.
        filteredDataObjects = dataObjects
          .map((object) => cloneDeep(object)) // Deep copy: the Objects may mutate due to children filtering
          .filter((dataObject) => !isRootDataObjectAssignable(dataObject, [selectedColumnExpressionElement]));
        filteredDataObjects.filter((dataObject) =>
          filterDataObjectChildrenByExpressionElements(dataObject, assignedExpressionElements2)
        );
      }

      return filteredDataObjects;
    },
    [dataObjects, scesimModel.ScenarioSimulationModel]
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

      let filteredDataObjects: TestScenarioDataObject[] = filterOutAlreadyAssignedDataObjectsAndChildren(
        selectedColumnMetadata.factMapping.expressionElements,
        selectedColumnMetadata.isBackground
      );

      /** Applying User search key to the filteredDataObjects, if present */
      const isUserFilterPresent = treeViewStatus.searchKey.trim() !== "";
      if (isUserFilterPresent) {
        filteredDataObjects = filteredDataObjects.filter((item) =>
          filterDataObjectsByName(item, treeViewStatus.searchKey)
        );
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
    let fieldId: string;
    if (isExpressionType) {
      fieldId = selectedColumnMetadata.factMapping.expressionElements!.ExpressionElement![0].step.__$$text;
    } else if (isSimpleTypeFact) {
      fieldId = selectedColumnMetadata.factMapping
        .expressionElements!.ExpressionElement![0].step.__$$text.concat(".")
        .concat("value");
    } else {
      fieldId = selectedColumnMetadata.factMapping
        .expressionElements!.ExpressionElement!.map((expressionElement) => expressionElement.step.__$$text)
        .join(".");
    }

    const treeViewItemToActivate = findTestScenarioDataObjectById(dataObjects, fieldId)!;

    setDataSelectorStatus(TestScenarioDataSelectorState.TREEVIEW_ENABLED_ONLY);
    setFilteredItems(filteredDataObjects);
    setTreeViewStatus({ activeItems: [treeViewItemToActivate], searchKey: "", isExpanded: true });
    console.debug("Case 3");
    console.debug("=============USE EFFECT END===============");
  }, [
    dataObjects,
    filterOutAlreadyAssignedDataObjectsAndChildren,
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
      treeViewStatus.activeItems[0]?.id &&
      !filteredDataObjects[0].children?.find((child) =>
        filterDataObjectsById(child, treeViewStatus.activeItems[0].id!)
      );

    if (oneActiveTreeViewItem && isAlreadyAssigined) {
      return {
        message: i18n.drawer.dataSelector.insertDataObjectTooltipDataObjectAlreadyAssignedMessage,
        enabled: false,
      };
    }

    return { message: i18n.drawer.dataSelector.insertDataObjectTooltipDataObjectAssignMessage, enabled: true };
  }, [filterOutAlreadyAssignedDataObjectsAndChildren, i18n, selectedColumnMetadata, treeViewStatus]);

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
    const isRootType = rootSelectedTestScenarioDataObject.id === selectedTestScenarioDataObject.id;
    const expressionAlias = isRootType
      ? "expression </>"
      : selectedTestScenarioDataObject.id.replace(selectedTestScenarioDataObject.expressionElements[0] + ".", "");
    const factMappingValueType = isRootType ? "EXPRESSION" : "NOT_EXPRESSION";

    testScenarioEditorStoreApi.setState((state) => {
      const factMappings = isBackground
        ? state.scesim.model.ScenarioSimulationModel.background.scesimModelDescriptor.factMappings.FactMapping!
        : state.scesim.model.ScenarioSimulationModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
      const factMappingValuesTypes = isBackground
        ? state.scesim.model.ScenarioSimulationModel.background.scesimData.BackgroundData!
        : state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario!;

      const { updatedFactMapping } = updateColumn({
        className: selectedTestScenarioDataObject.className!,
        expressionAlias: expressionAlias,
        expressionElementsSteps: selectedTestScenarioDataObject.expressionElements,
        expressionIdentifierName: selectedColumnMetadata!.factMapping.expressionIdentifier.name?.__$$text,
        expressionIdentifierType: selectedColumnMetadata!.factMapping.expressionIdentifier.type?.__$$text,
        factMappings: factMappings,
        factClassName: rootSelectedTestScenarioDataObject.className!,
        factIdentifierClassName: selectedColumnMetadata!.factMapping.factIdentifier.className?.__$$text,
        factIdentifierName: selectedColumnMetadata!.factMapping.factIdentifier.name?.__$$text,
        factMappingValuesTypes: factMappingValuesTypes,
        factMappingValueType: factMappingValueType,
        factName: rootSelectedTestScenarioDataObject.name,
        genericTypes: selectedTestScenarioDataObject.collectionGenericType ?? [],
        selectedColumnIndex: selectedColumnMetadata!.index,
      });

      state.dispatch(state).table.updateSelectedColumn({
        factMapping: updatedFactMapping,
        index: selectedColumnMetadata!.index,
        isBackground: isBackground,
      });
    });
  }, [dataObjects, selectedColumnMetadata, testScenarioEditorStoreApi, treeViewStatus.activeItems]);

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
                <Spinner aria-label="Data Objects loading" />
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
