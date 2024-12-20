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
import { useCallback } from "react";

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import CogIcon from "@patternfly/react-icons/dist/esm/icons/cog-icon";
import EditIcon from "@patternfly/react-icons/dist/esm/icons/edit-alt-icon";
import InfoIcon from "@patternfly/react-icons/dist/esm/icons/info-icon";

import { useTestScenarioEditorI18n } from "../i18n";
import { TestScenarioEditorDock } from "../store/TestScenarioEditorStore";
import { useTestScenarioEditorStore, useTestScenarioEditorStoreApi } from "../store/TestScenarioStoreContext";

import "./TestScenarioSideBarMenu.css";

function TestScenarioSideBarMenu() {
  const { i18n } = useTestScenarioEditorI18n();
  const navigation = useTestScenarioEditorStore((state) => state.navigation);
  const testScenarioEditorStoreApi = useTestScenarioEditorStoreApi();

  const isSelectedMenuItem = useCallback(
    (item: TestScenarioEditorDock) => {
      return navigation.dock.isOpen && navigation.dock.selected === item;
    },
    [navigation.dock]
  );

  const updateSelectedDock = useCallback(
    (selected: TestScenarioEditorDock) => {
      testScenarioEditorStoreApi.setState((state) => {
        state.navigation.dock.isOpen = true;
        state.navigation.dock.selected = selected;
      });
    },
    [testScenarioEditorStoreApi]
  );

  return (
    <div className="kie-scesim-editor--side-bar">
      <Tooltip content={i18n.sidebar.dataSelectorTooltip}>
        <Button
          className={
            isSelectedMenuItem(TestScenarioEditorDock.DATA_OBJECT)
              ? "kie-scesim-editor-side-bar-menu--button-selected"
              : "kie-scesim-editor-side-bar-menu--button"
          }
          variant="plain"
          onClick={() => updateSelectedDock(TestScenarioEditorDock.DATA_OBJECT)}
          icon={<EditIcon />}
        />
      </Tooltip>
      <Tooltip content={i18n.sidebar.cheatSheetTooltip}>
        <Button
          className={
            isSelectedMenuItem(TestScenarioEditorDock.CHEATSHEET)
              ? "kie-scesim-editor-side-bar-menu--button-selected"
              : "kie-scesim-editor-side-bar-menu--button"
          }
          icon={<InfoIcon />}
          onClick={() => updateSelectedDock(TestScenarioEditorDock.CHEATSHEET)}
          variant="plain"
        />
      </Tooltip>
      <Tooltip content={<div>{i18n.sidebar.settingsTooltip}</div>}>
        <Button
          className={
            isSelectedMenuItem(TestScenarioEditorDock.SETTINGS)
              ? "kie-scesim-editor-side-bar-menu--button-selected"
              : "kie-scesim-editor-side-bar-menu--button"
          }
          icon={<CogIcon />}
          onClick={() => updateSelectedDock(TestScenarioEditorDock.SETTINGS)}
          variant="plain"
        />
      </Tooltip>
    </div>
  );
}

export default TestScenarioSideBarMenu;
