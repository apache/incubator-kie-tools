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

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistableDataObject;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.persistence.persistabledataobjects.PersistableDataObjectsItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.persistence.properties.PropertiesItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

public class PersistencePresenter extends Section<ProjectScreenModel> {

    private final View view;
    private final WorkspaceProjectContext projectContext;
    private final Event<NotificationEvent> notificationEvent;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final Caller<PersistenceDescriptorEditorService> editorService;
    private final Caller<DataModelerService> dataModelerService;

    private final PropertiesListPresenter propertiesListPresenter;
    private final PersistableDataObjectsListPresenter persistableDataObjectsListPresenter;

    private ObservablePath pathToPersistenceXml;
    PersistenceDescriptorEditorContent persistenceDescriptorEditorContent;
    ObservablePath.OnConcurrentUpdateEvent concurrentPersistenceXmlUpdateInfo;

    public interface View extends SectionView<PersistencePresenter> {

        void setPersistenceUnit(String persistenceUnit);

        void setPersistenceProvider(String persistenceProvider);

        void setDataSource(String dataSource);

        String getConcurrentUpdateMessage();

        Element getPropertiesTable();

        Element getPersistableDataObjectsTable();

        void hideError();

        String getEmptyPropertiesMessage();

        void showError(String message);
    }

    @Inject
    public PersistencePresenter(final View view,
                                final WorkspaceProjectContext projectContext,
                                final Promises promises,
                                final MenuItem<ProjectScreenModel> menuItem,
                                final Event<NotificationEvent> notificationEvent,
                                final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                                final ManagedInstance<ObservablePath> observablePaths,
                                final Caller<PersistenceDescriptorEditorService> editorService,
                                final Caller<DataModelerService> dataModelerService,
                                final PropertiesListPresenter propertiesListPresenter,
                                final PersistableDataObjectsListPresenter persistableDataObjectsListPresenter) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.projectContext = projectContext;
        this.notificationEvent = notificationEvent;
        this.observablePaths = observablePaths;
        this.editorService = editorService;
        this.dataModelerService = dataModelerService;
        this.propertiesListPresenter = propertiesListPresenter;
        this.persistableDataObjectsListPresenter = persistableDataObjectsListPresenter;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {
        return setup();
    }

    Promise<Void> setup() {
        view.init(this);

        final String persistenceXmlUri = projectContext.getActiveModule()
                .orElseThrow(() -> new IllegalStateException("Cannot get root path because there is no active module"))
                .getRootPath().toURI() + "src/main/resources/META-INF/persistence.xml";

        pathToPersistenceXml = observablePaths.get().wrap(PathFactory.newPath(
                "persistence.xml",
                persistenceXmlUri,
                new HashMap<String, Object>() {{
                    put(PathFactory.VERSION_PROPERTY, true);
                }}));

        concurrentPersistenceXmlUpdateInfo = null;
        pathToPersistenceXml.onConcurrentUpdate(info -> concurrentPersistenceXmlUpdateInfo = info);

        return promises.promisify(editorService, s -> {
            return s.loadContent(pathToPersistenceXml, true);
        }).then(m -> {
            persistenceDescriptorEditorContent = m;

            view.setPersistenceUnit(getPersistenceUnitModel().getName());
            view.setPersistenceProvider(getPersistenceUnitModel().getProvider());
            view.setDataSource(getPersistenceUnitModel().getJtaDataSource());

            setupPropertiesTable();
            setupPersistableDataObjectsTable();

            return promises.resolve();
        });
    }

    @Override
    public Promise<Object> validate() {
        view.hideError();
        return promises.all(
                validatePropertyIsNotEmpty(propertiesListPresenter.getObjectsList(), view.getEmptyPropertiesMessage())
                        .catch_(this::showErrorAndReject)
        );
    }

    private void setupPropertiesTable() {
        propertiesListPresenter.setup(
                view.getPropertiesTable(),
                getPersistenceUnitModel().getProperties(),
                (property, presenter) -> presenter.setup(property, this));
    }

    private void setupPersistableDataObjectsTable() {
        persistableDataObjectsListPresenter.setup(
                view.getPersistableDataObjectsTable(),
                getPersistenceUnitModel().getClasses(),
                (className, presenter) -> presenter.setup(className, this));
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {

        if (concurrentPersistenceXmlUpdateInfo == null) {
            return save(comment);
        } else {
            notificationEvent.fire(new NotificationEvent(view.getConcurrentUpdateMessage(), WARNING));
            return setup();
        }
    }

    Promise<Void> save(final String comment) {
        return promises.promisify(editorService, s -> {
            s.save(pathToPersistenceXml,
                   persistenceDescriptorEditorContent,
                   persistenceDescriptorEditorContent.getOverview().getMetadata(),
                   comment);
        });
    }

    public void add(final String className) {
        persistableDataObjectsListPresenter.add(new PersistableDataObject(className));
        fireChangeEvent();
    }

    public void add(final Property property) {
        propertiesListPresenter.add(property);
        fireChangeEvent();
    }

    public void addAllProjectsPersistableDataObjects() {
        promises.promisify(dataModelerService, s -> {
            return s.findPersistableClasses(pathToPersistenceXml);
        }).then(classes -> {
            classes.stream()
                    .filter(clazz -> !getPersistenceUnitModel().getClasses().contains(clazz))
                    .forEach(this::add);

            return promises.resolve();
        });
    }

    public void setDataSource(final String dataSource) {
        getPersistenceUnitModel().setJtaDataSource(dataSource);
        fireChangeEvent();
    }

    public void setPersistenceUnit(final String persistenceUnit) {
        getPersistenceUnitModel().setName(persistenceUnit);
        fireChangeEvent();
    }

    public void setPersistenceProvider(final String persistenceProvider) {
        getPersistenceUnitModel().setProvider(persistenceProvider);
        fireChangeEvent();
    }

    private PersistenceUnitModel getPersistenceUnitModel() {
        return persistenceDescriptorEditorContent.getDescriptorModel().getPersistenceUnit();
    }

    public void addNewProperty() {
        add(new Property("", ""));
        fireChangeEvent();
    }

    public void addNewPersistableDataObject() {
        add("");
        fireChangeEvent();
    }

    @Override
    public int currentHashCode() {
        return getPersistenceUnitModel().hashCode();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Dependent
    public static class PersistableDataObjectsListPresenter extends ListPresenter<PersistableDataObject, PersistableDataObjectsItemPresenter> {

        @Inject
        public PersistableDataObjectsListPresenter(final ManagedInstance<PersistableDataObjectsItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class PropertiesListPresenter extends ListPresenter<Property, PropertiesItemPresenter> {

        @Inject
        public PropertiesListPresenter(final ManagedInstance<PropertiesItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    Promise<Object> showErrorAndReject(final Object o) {
        return promises.catchOrExecute(o, e -> {
            view.showError(e.getMessage());
            return promises.reject(this);
        }, (final String errorMessage) -> {
            view.showError(errorMessage);
            return promises.reject(this);
        });
    }

    Promise<Boolean> validatePropertyIsNotEmpty(final List<Property> listProperty,
                                                final String errorMessage) {
        return promises.create((resolve, reject) -> {
            if (isEmpty(listProperty)) {
                reject.onInvoke(errorMessage);
            } else {
                resolve.onInvoke(true);
            }
        });
    }

    private boolean isEmpty(List<Property> listProperty) {
        boolean isEmpty = false;
        for (Property property : listProperty) {
            if (property.getName() == null || property.getName().trim().isEmpty()) {
                isEmpty = true;
            }
            if (property.getValue() == null || property.getValue().trim().isEmpty()) {
                isEmpty = true;
            }
        }
        return isEmpty;
    }
}
