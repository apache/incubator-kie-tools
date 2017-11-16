/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.samples;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Templated
public class ImportProjectsView implements ImportProjectsScreen.View,
                                           IsElement {

    private ImportProjectsScreen presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("title")
    Div title;

    @Inject
    @DataField("project-list")
    Div projectList;

    @Inject
    @DataField("filter-text")
    Input filterText;

    @Inject
    @DataField("cancel")
    Button cancel;

    @Inject
    @DataField("ok")
    Button ok;

    @Override
    public void init(final ImportProjectsScreen presenter) {
        this.presenter = presenter;
        filterText.setAttribute("placeholder",
                                ts.getTranslation(LibraryConstants.Search));
    }

    @Override
    public void setTitle(final String title) {
        this.title.setTextContent(title);
    }

    @Override
    public void clearProjects() {
        projectList.setTextContent("");
    }

    @Override
    public void addProject(HTMLElement project) {
        projectList.appendChild(project);
    }

    @Override
    public String getNumberOfAssetsMessage(int numberOfAssets) {
        return ts.format(LibraryConstants.NumberOfAssets,
                         numberOfAssets);
    }

    @Override
    public String getTrySamplesLabel() {
        return ts.format(LibraryConstants.TrySamples);
    }

    @Override
    public String getNoProjectsToImportMessage() {
        return ts.format(LibraryConstants.NoProjectsToImport);
    }

    @Override
    public String getImportingMessage() {
        return ts.format(LibraryConstants.Importing);
    }

    @Override
    public String getLoadingMessage() {
        return ts.format(LibraryConstants.Loading);
    }

    @Override
    public String getNoProjectsSelectedMessage() {
        return ts.format(LibraryConstants.NoProjectsSelected);
    }

    @Override
    public String getImportProjectsSuccessMessage() {
        return ts.format(LibraryConstants.ImportProjectsSuccess);
    }

    @EventHandler("filter-text")
    public void filterTextChange(final KeyUpEvent event) {
        presenter.filterProjects(filterText.getValue());
    }

    @EventHandler("cancel")
    public void cancel(final ClickEvent event) {
        presenter.cancel();
    }

    @EventHandler("ok")
    public void ok(final ClickEvent event) {
        presenter.ok();
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
