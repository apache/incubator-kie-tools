/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.persistence;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants.PersistenceXmlConcurrentUpdate;

@Templated
public class PersistenceView implements PersistencePresenter.View {

    @Inject
    @DataField("persistence-unit")
    private HTMLInputElement persistenceUnit;

    @Inject
    @DataField("persistence-provider")
    private HTMLInputElement persistenceProvider;

    @Inject
    @DataField("data-source")
    private HTMLInputElement dataSource;

    @Inject
    @DataField("add-property-button")
    private HTMLButtonElement addPropertyButton;

    @Inject
    @DataField("add-persistable-data-object-button")
    private HTMLButtonElement addPersistableDataObjectButton;

    @Inject
    @DataField("add-all-projects-persistable-data-objects-button")
    private HTMLButtonElement addAllProjectsPersistableDataObjectsButton;

    @Inject
    @Named("tbody")
    @DataField("properties-table")
    private HTMLTableSectionElement propertiesTable;

    @Inject
    @Named("tbody")
    @DataField("persistable-data-objects-table")
    private HTMLTableSectionElement persistableDataObjectsTable;

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Inject
    private TranslationService translationService;

    private PersistencePresenter presenter;

    @Override
    public void init(final PersistencePresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("data-source")
    public void onDataSourceChanged(final ChangeEvent ignore) {
        presenter.setDataSource(dataSource.value);
    }

    @EventHandler("persistence-unit")
    public void onPersistenceUnitChanged(final ChangeEvent ignore) {
        presenter.setPersistenceUnit(persistenceUnit.value);
    }

    @EventHandler("persistence-provider")
    public void onPersistenceProviderChanged(final ChangeEvent ignore) {
        presenter.setPersistenceProvider(persistenceProvider.value);
    }

    @EventHandler("add-property-button")
    public void onAddPropertyButtonClicked(final ClickEvent ignore) {
        presenter.showNewPropertyModal();
    }

    @EventHandler("add-persistable-data-object-button")
    public void onAddPersistableDataObjectButtonClicked(final ClickEvent ignore) {
        presenter.showNewPersistableDataObjectModal();
    }

    @EventHandler("add-all-projects-persistable-data-objects-button")
    public void onAddAllProjectsPersistableDataObjectsButtonClicked(final ClickEvent ignore) {
        presenter.addAllProjectsPersistableDataObjects();
    }

    @Override
    public void setPersistenceUnit(final String persistenceUnit) {
        this.persistenceUnit.value = persistenceUnit;
    }

    @Override
    public void setPersistenceProvider(final String persistenceProvider) {
        this.persistenceProvider.value = persistenceProvider;
    }

    @Override
    public void setDataSource(final String dataSource) {
        this.dataSource.value = dataSource;
    }

    @Override
    public String getConcurrentUpdateMessage() {
        return translationService.format(PersistenceXmlConcurrentUpdate);
    }

    @Override
    public Element getPropertiesTable() {
        return propertiesTable;
    }

    @Override
    public Element getPersistableDataObjectsTable() {
        return persistableDataObjectsTable;
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
