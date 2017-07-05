/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.client.explorer.common;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
@Templated
public class DefExplorerContentViewImpl
        implements DefExplorerContentView,
                   IsElement {

    @Inject
    @DataField("content-accordion")
    private Div contentAccordion;

    @Inject
    @DataField("datasources-panel-link")
    private Anchor dataSourcesPanelLink;

    @Inject
    @DataField("datasources-panel")
    private Div dataSourcesPanel;

    @Inject
    @DataField("datasources-list-group")
    private Div dataSourcesListGroup;

    @Inject
    @DataField("add-new-datasource")
    private Anchor newDataSourceLink;

    @Inject
    @DataField("drivers-panel-link")
    private Anchor driversPanelLink;

    @Inject
    @DataField("drivers-panel")
    private Div driversPanel;

    @Inject
    @DataField("drivers-list-group")
    private Div driversListGroup;

    @Inject
    @DataField("add-new-driver")
    private Anchor newDriverLink;

    private Presenter presenter;

    public DefExplorerContentViewImpl() {
    }

    @PostConstruct
    private void init() {

        //recalculate the panel group needed ids in order to enable more than one instance to be instantiated
        // on the same page.
        String contentAccordionId = Document.get().createUniqueId();
        contentAccordion.setId(contentAccordionId);

        String dataSourcesPanelId = Document.get().createUniqueId();
        dataSourcesPanelLink.setAttribute("data-parent",
                                          "#" + contentAccordionId);
        dataSourcesPanelLink.setAttribute("data-target",
                                          "#" + dataSourcesPanelId);
        dataSourcesPanel.setId(dataSourcesPanelId);

        String driversPanelLinkId = Document.get().createUniqueId();
        driversPanelLink.setAttribute("data-parent",
                                      "#" + contentAccordionId);
        driversPanelLink.setAttribute("data-target",
                                      "#" + driversPanelLinkId);
        driversPanel.setId(driversPanelLinkId);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addDataSourceItem(DefItem item) {
        dataSourcesListGroup.appendChild(item.getElement());
    }

    @Override
    public void addDriverItem(DefItem item) {
        driversListGroup.appendChild(item.getElement());
    }

    @Override
    public void clear() {
        DOMUtil.removeAllChildren(dataSourcesListGroup);
        DOMUtil.removeAllChildren(driversListGroup);
    }

    @Override
    public void clearDataSources() {
        DOMUtil.removeAllChildren(dataSourcesListGroup);
    }

    @Override
    public void clearDrivers() {
        DOMUtil.removeAllChildren(driversListGroup);
    }

    @Override
    public PlaceRequest createEditorPlaceRequest(Path path) {
        return new PathPlaceRequest(path);
    }

    @Override
    public PlaceRequest createScreenPlaceRequest(String identifier) {
        return new DefaultPlaceRequest(identifier);
    }

    @EventHandler("add-new-datasource")
    private void onAddNewDataSource(@ForEvent("click") Event event) {
        presenter.onAddDataSource();
    }

    @EventHandler("add-new-driver")
    private void onAddNewDriver(@ForEvent("click") Event event) {
        presenter.onAddDriver();
    }
}