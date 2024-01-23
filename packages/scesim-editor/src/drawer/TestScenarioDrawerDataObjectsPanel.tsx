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

import { useCallback, useEffect, useState } from "react";

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Button } from "@patternfly/react-core/dist/js/components/Button/";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider/";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { HelpIcon } from "@patternfly/react-icons/dist/esm/icons/help-icon";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Toolbar } from "@patternfly/react-core/dist/js/components/Toolbar/";
import { ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar/";
import { ToolbarContent } from "@patternfly/react-core/dist/js/components/Toolbar/";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip/";
import { TreeView, TreeViewDataItem } from "@patternfly/react-core/dist/js/components/TreeView/";
import { TreeViewSearch } from "@patternfly/react-core/dist/js/components/TreeView/";
import { WarningTriangleIcon } from "@patternfly/react-icons/dist/esm/icons/warning-triangle-icon";

import { SceSimModel } from "@kie-tools/scesim-marshaller";
import { SceSim__FactMappingType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { TestScenarioDataObject, TestScenarioType } from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";

import "./TestScenarioDrawerDataObjectsPanel.css";

function TestScenarioDataObjectsPanel({
  assetType,
  dataObjects,
  selectedColumnFactMapping,
  updateTestScenarioModel,
}: {
  assetType: string;
  dataObjects: TestScenarioDataObject[];
  selectedColumnFactMapping: SceSim__FactMappingType | null;
  updateTestScenarioModel: React.Dispatch<React.SetStateAction<SceSimModel>>;
}) {
  const { i18n } = useTestScenarioEditorI18n();

  const [allExpanded, setAllExpanded] = useState(false);
  const [filteredItems, setFilteredItems] = useState({ items: dataObjects, isFiltered: false });
  const [treeViewActiveItems, setTreeViewActiveItems] = useState<TreeViewDataItem[]>([]);
  const [selectorEnabled, setSelectorEnabled] = useState(true);

  useEffect(() => {
    if (!selectedColumnFactMapping || selectedColumnFactMapping.expressionIdentifier.type?.__$$text == "OTHER") {
      setFilteredItems({ items: dataObjects, isFiltered: false });
      setTreeViewActiveItems([]);
      setSelectorEnabled(true);
      return;
    }

    const expressionElements = selectedColumnFactMapping?.expressionElements?.ExpressionElement?.map(
      (element) => element.step.__$$text
    );
    expressionElements?.splice(0, 1);
    const fieldName =
      expressionElements && expressionElements.length > 0
        ? expressionElements?.join(".")
        : selectedColumnFactMapping?.expressionAlias!.__$$text;
    const factIdentifier = selectedColumnFactMapping?.factIdentifier!.name!.__$$text;
    console.log(selectedColumnFactMapping);
    console.log(dataObjects);
    const filtered = dataObjects
      .map((object) => Object.assign({}, object))
      .filter((dataObject) => filterTypesItems(dataObject, factIdentifier));
    setFilteredItems({ items: filtered, isFiltered: true });

    console.log(filtered);

    if (selectedColumnFactMapping.className.__$$text !== "java.lang.Void") {
      const propertyID = selectedColumnFactMapping
        .expressionElements!.ExpressionElement!.map((expressionElement) => expressionElement.step.__$$text)
        .join(".");

      const treeViewItemToActivate = filtered
        .reduce((acc: TestScenarioDataObject[], item) => {
          return item.children ? acc.concat(item.children) : acc;
        }, [])
        .filter((item) => item.id === propertyID);

      setTreeViewActiveItems(treeViewItemToActivate);
      setSelectorEnabled(false);
    } else {
      setTreeViewActiveItems([]);
    }
  }, [dataObjects, selectedColumnFactMapping]);

  const filterTypesItems = useCallback((dataObject, factIdentifierName?) => {
    return factIdentifierName && dataObject.name.toLowerCase() === factIdentifierName.toLowerCase();
  }, []);

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

  const onSearchTreeView = useCallback(
    (event) => {
      const input = event.target.value;
      if (input === "") {
        setFilteredItems({ items: dataObjects, isFiltered: false });
      } else {
        const filtered = dataObjects
          .map((object) => Object.assign({}, object))
          .filter((item) => filterItems(item, input));

        setFilteredItems({ items: filtered, isFiltered: true });
      }
    },
    [dataObjects, filterItems]
  );

  const onSelectTreeViewItem = useCallback((_event, treeViewItem: TreeViewDataItem) => {
    setTreeViewActiveItems([treeViewItem]);
  }, []);

  const onInsertDataObjectClick = useCallback((_event) => {
    setTreeViewActiveItems([]);
  }, []);

  const onAllExpandedToggle = useCallback((_event) => {
    setAllExpanded((prev) => !prev);
  }, []);

  const determineInsertDataObjectStatus = useCallback(() => {
    const propertyID = selectedColumnFactMapping?.expressionElements?.ExpressionElement?.map(
      (expressionElement) => expressionElement.step.__$$text
    ).join(".");
    if (selectedColumnFactMapping == null) {
      return {
        message: "Please select an column's field header to add or change a Type in the table.",
        enabled: false,
      };
    } else if (treeViewActiveItems.length != 1) {
      return { message: "Please select a single field to assign it in the selected column", enabled: false };
    } else if (treeViewActiveItems.length == 1 && treeViewActiveItems[0].id === propertyID) {
      return { message: "The column is already assigned to the selected Field.", enabled: false };
    } else {
      return { message: "Click here to assign the selected field to the focused column.", enabled: true };
    }
  }, [selectedColumnFactMapping, treeViewActiveItems]);

  const toolbar = (
    <Toolbar style={{ padding: 0 }}>
      <ToolbarContent style={{ padding: 0 }}>
        <ToolbarItem widths={{ default: "100%" }}>
          <TreeViewSearch
            onSearch={onSearchTreeView}
            id="input-search"
            name="search-input"
            disabled={!selectorEnabled}
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );

  return (
    <>
      <Text className="kie-scesim-editor-drawer-data-objects--text">
        {assetType === TestScenarioType[TestScenarioType.DMN]
          ? i18n.drawer.dataObjects.descriptionDMN
          : i18n.drawer.dataObjects.descriptionRule}
        <Tooltip
          content={
            assetType === TestScenarioType[TestScenarioType.DMN]
              ? i18n.drawer.dataObjects.dataObjectsDescriptionDMN
              : i18n.drawer.dataObjects.dataObjectsDescriptionRule
          }
        >
          <Icon className={"kie-scesim-editor-drawer-data-objects--info-icon"} size="sm" status="info">
            <HelpIcon />
          </Icon>
        </Tooltip>
      </Text>
      <Divider />
      <div className={"kie-scesim-editor-drawer-data-objects--selector"}>
        {dataObjects.length > 0 ? (
          <TreeView
            activeItems={treeViewActiveItems}
            allExpanded={allExpanded || filteredItems.isFiltered}
            data={filteredItems.items}
            hasBadges
            hasSelectableNodes
            onSelect={onSelectTreeViewItem}
            toolbar={toolbar}
          />
        ) : (
          <Bullseye>
            <EmptyState>
              <EmptyStateIcon icon={WarningTriangleIcon} />
              <Title headingLevel="h4" size="lg">
                {assetType === TestScenarioType[TestScenarioType.DMN]
                  ? i18n.drawer.dataObjects.emptyDataObjectsTitleDMN
                  : i18n.drawer.dataObjects.emptyDataObjectsTitleRule}
              </Title>
              <EmptyStateBody>
                {assetType === TestScenarioType[TestScenarioType.DMN]
                  ? i18n.drawer.dataObjects.emptyDataObjectsDescriptionDMN
                  : i18n.drawer.dataObjects.emptyDataObjectsDescriptionRule}
              </EmptyStateBody>
            </EmptyState>
          </Bullseye>
        )}
      </div>
      <Divider />
      <div className={"kie-scesim-editor-drawer-data-objects--button-container"}>
        <Tooltip content={determineInsertDataObjectStatus().message}>
          <Button
            isAriaDisabled={!determineInsertDataObjectStatus().enabled}
            onClick={onInsertDataObjectClick}
            variant="primary"
          >
            {i18n.drawer.dataObjects.insertDataObject}
          </Button>
        </Tooltip>
        <Button
          onClick={() => setTreeViewActiveItems([])}
          isDisabled={treeViewActiveItems.length < 1 || !selectorEnabled}
          variant="secondary"
        >
          {i18n.drawer.dataObjects.clearSelection}
        </Button>
        <Button
          onClick={onAllExpandedToggle}
          isDisabled={filteredItems.items.length < 1 || filteredItems.isFiltered || !selectorEnabled}
          variant="link"
        >
          {allExpanded ? i18n.drawer.dataObjects.collapseAll : i18n.drawer.dataObjects.expandAll}
        </Button>
      </div>
    </>
  );
}

export default TestScenarioDataObjectsPanel;
