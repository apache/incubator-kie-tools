/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.resources.css;

import com.google.gwt.resources.client.CssResource;

public interface ScenarioSimulationEditorStylesCss extends CssResource {

    String disabled();

    @ClassName("list-group-item-header")
    String listGroupItemHeader();

    @ClassName("list-group-item-container")
    String listGroupItemContainer();

    @ClassName("list-view-pf-expand-active")
    String listViewPfExpandActive();

    @ClassName("kie-tree-list-view-pf-view--compact")
    String kieTreeListViewPfViewCompact();

    @ClassName("list-view-pf-main-info")
    String listViewPfMainInfo();

    @ClassName("list-group-item")
    String listGroupItem();

    @ClassName("list-view-pf-expand")
    String listViewPfExpand();

    @ClassName("selected")
    String selected();

    @ClassName("ul--plain")
    String ulPlain();

    @ClassName("node-hidden")
    String nodeHidden();

    @ClassName("treeview")
    String treeview();

    @ClassName("list-group")
    String listGroup();

    @ClassName("kie-object-list__field-label")
    String kieObjectList__fieldLabel();

    @ClassName("kie-object-list__separator")
    String kieObjectList__separator();

    @ClassName("kie-object-list")
    String kieObjectList();

    @ClassName("kie-object-list--mapping")
    String kieObjectListMapping();

    @ClassName("kie-object-list__field-value")
    String kieObjectList__fieldValue();

    @ClassName("kie-object-list__expander")
    String kieObjectList__expander();

    @ClassName("kie-tab-pane--scesim-panel")
    String kieTabPaneScesimPanel();

    @ClassName("kie-tab-pane--scesim-panel-footer")
    String kieTabPaneScesimPanelFooter();

    @ClassName("kie-test-tools-content__box")
    String kieTestToolsContentBox();

    @ClassName("kie-test-tools-info__icon")
    String kieTestToolsInfoIcon();

    @ClassName("kie-test-tools-search__box")
    String kieTestToolsSearchBox();

    @ClassName("kie-test-tools-instances-list__label")
    String kieTestToolsInstancesListLabel();

    @ClassName("kie-test-tools-clear-selection")
    String kieTestToolsClearSelection();

    @ClassName("kie-list-view-pf-main-field-element")
    String kieListViewPfMainFieldElement();

    @ClassName("kie-list-view-pf-main-info__check")
    String kieListViewPfMainInfoCheck();

    @ClassName("kie-list-view-pf-main-info__text")
    String kieListViewPfMainInfoText();

    @ClassName("kie-test-tools-insert-object-label")
    String kieTestToolsInsertObjectLabel();

    @ClassName("list-group-item-container-fact-property")
    String listGroupItemContainerFactProperty();

    @ClassName("kie-coverage__dl-horizontal")
    String kieCoverageDlHorizontal();
}
