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

import { Button } from "@patternfly/react-core/dist/js/components/Button/Button";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider/Divider";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { Toolbar } from "@patternfly/react-core/dist/js/components/Toolbar/Toolbar";
import { ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar/ToolbarItem";
import { ToolbarContent } from "@patternfly/react-core/dist/js/components/Toolbar/ToolbarContent";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip/Tooltip";
import { TreeView, TreeViewDataItem } from "@patternfly/react-core/dist/js/components/TreeView/TreeView";
import { TreeViewSearch } from "@patternfly/react-core/dist/js/components/TreeView/TreeViewSearch";

import { Icon } from "@patternfly/react-core/dist/esm/components/Icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/esm/icons/info-circle-icon";

import { TestScenarioDataObject, TestScenarioType } from "../TestScenarioEditor";
import { useTestScenarioEditorI18n } from "../i18n";

import "./TestScenarioDrawerDataObjectsPanel.css";

function TestScenarioDataObjectsPanel({
  assetType,
  dataObjects,
}: {
  assetType: string;
  dataObjects: TestScenarioDataObject[];
}) {
  const { i18n } = useTestScenarioEditorI18n();

  const [allExpanded, setAllExpanded] = useState(false);
  const [filteredItems, setFilteredItems] = useState({ items: dataObjects, isFiltered: false });
  const [treeViewActiveItems, setTreeViewActiveItems] = useState<TreeViewDataItem[]>([]);

  useEffect(() => {
    setFilteredItems({ items: dataObjects, isFiltered: false });
    setAllExpanded(false);
  }, [dataObjects]);

  const filterItems = useCallback((item, input) => {
    if (item.name.toLowerCase().includes(input.toLowerCase())) {
      return true;
    }

    if (item.children) {
      return (
        (item.children = item.children
          .map((opt: TestScenarioDataObject) => Object.assign({}, opt))
          .filter((child: TestScenarioDataObject) => filterItems(child, input))).length > 0
      );
    }
  }, []);

  const onSearchTreeView = useCallback(
    (evt) => {
      const input = evt.target.value;
      if (input === "") {
        setFilteredItems({ items: dataObjects, isFiltered: false });
      } else {
        const filtered = dataObjects.map((opt) => Object.assign({}, opt)).filter((item) => filterItems(item, input));
        setFilteredItems({ items: filtered, isFiltered: true });
      }
    },
    [dataObjects, filterItems]
  );

  const onSelectTreeViewItem = useCallback((_event, treeViewItem: TreeViewDataItem) => {
    console.log(treeViewItem);
    setTreeViewActiveItems([treeViewItem]);
  }, []);

  const onAllExpandedToggle = useCallback((_evt) => {
    setAllExpanded((prev) => !prev);
  }, []);

  const toolbar = (
    <Toolbar style={{ padding: 0 }}>
      <ToolbarContent style={{ padding: 0 }}>
        <ToolbarItem widths={{ default: "100%" }}>
          <TreeViewSearch
            onSearch={onSearchTreeView}
            id="input-search"
            name="search-input"
            aria-label="Search input example"
          />
        </ToolbarItem>
      </ToolbarContent>
    </Toolbar>
  );

  return (
    <>
      <Text className="kie-scesim-editor-drawer-data-objects--text">
        {i18n.drawer.dataObjects.description}
        <Tooltip
          content={
            assetType === TestScenarioType[TestScenarioType.DMN]
              ? i18n.drawer.dataObjects.dataObjectsDescriptionDMN
              : i18n.drawer.dataObjects.dataObjectsDescriptionRule
          }
        >
          <Icon className={"kie-scesim-editor-drawer-data-objects--info-icon"} size="sm" status="info">
            <InfoCircleIcon />
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
          <Text>OOOpps</Text>
        )}
      </div>
      <Divider />
      <div className={"kie-scesim-editor-drawer-data-objects--button-container"}>
        <Button isDisabled={treeViewActiveItems.length < 1} variant="primary">
          {i18n.drawer.dataObjects.insertDataObject}
        </Button>
        <Button
          onClick={() => setTreeViewActiveItems([])}
          isDisabled={treeViewActiveItems.length < 1}
          variant="secondary"
        >
          {i18n.drawer.dataObjects.clearSelection}
        </Button>
        <Button onClick={onAllExpandedToggle} isDisabled={filteredItems.isFiltered} variant="link">
          {allExpanded ? i18n.drawer.dataObjects.collapseAll : i18n.drawer.dataObjects.expandAll}
        </Button>
      </div>
    </>
  );
}

export default TestScenarioDataObjectsPanel;
