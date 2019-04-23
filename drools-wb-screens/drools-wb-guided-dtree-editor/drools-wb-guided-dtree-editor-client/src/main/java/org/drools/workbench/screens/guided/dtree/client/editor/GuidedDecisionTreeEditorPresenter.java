/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtree.client.editor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.screens.guided.dtree.client.type.GuidedDTreeResourceType;
import org.drools.workbench.screens.guided.dtree.client.widget.popups.EditActionInsertPopup;
import org.drools.workbench.screens.guided.dtree.client.widget.popups.EditActionRetractPopup;
import org.drools.workbench.screens.guided.dtree.client.widget.popups.EditActionUpdatePopup;
import org.drools.workbench.screens.guided.dtree.client.widget.popups.EditConstraintPopup;
import org.drools.workbench.screens.guided.dtree.client.widget.popups.EditTypePopup;
import org.drools.workbench.screens.guided.dtree.client.widget.popups.ParserMessagesPopup;
import org.drools.workbench.screens.guided.dtree.model.GuidedDecisionTreeEditorContent;
import org.drools.workbench.screens.guided.dtree.service.GuidedDecisionTreeEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.datamodel.ImportAddedEvent;
import org.kie.workbench.common.widgets.client.datamodel.ImportRemovedEvent;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.callbacks.CommandErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Guided Decision Tree Editor Presenter
 */
@WorkbenchEditor(identifier = GuidedDecisionTreeEditorPresenter.EDITOR_ID, supportedTypes = {GuidedDTreeResourceType.class}, priority = 101)
public class GuidedDecisionTreeEditorPresenter
        extends KieEditor<GuidedDecisionTree> {

    public static final String EDITOR_ID = "GuidedDecisionTreeEditorPresenter";

    @Inject
    private ImportsWidgetPresenter importsWidget;

    @Inject
    protected Caller<GuidedDecisionTreeEditorService> service;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    protected ValidationPopup validationPopup;

    @Inject
    private GuidedDTreeResourceType type;

    @Inject
    private AsyncPackageDataModelOracleFactory oracleFactory;

    private GuidedDecisionTree model;
    private AsyncPackageDataModelOracle oracle;

    private GuidedDecisionTreeEditorContent content;

    private GuidedDecisionTreeEditorView view;

    public GuidedDecisionTreeEditorPresenter() {
    }

    @Inject
    public GuidedDecisionTreeEditorPresenter(final GuidedDecisionTreeEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    @PostConstruct
    public void init() {
        //KieEditorView (the base view of all KieEditors and extended here) does not implement UberView.
        //We therefore need to manually set-up the Presenter for our  widgets nested in KieEditorView
        view.init(this);
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
    }

    protected void loadContent() {
        view.showLoading();
        service.call(getModelSuccessCallback(),
                     getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    @Override
    protected Supplier<GuidedDecisionTree> getContentSupplier() {
        return () -> view.getModel();
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<GuidedDecisionTree, Metadata>> getSaveAndRenameServiceCaller() {
        return service;
    }

    private RemoteCallback<GuidedDecisionTreeEditorContent> getModelSuccessCallback() {
        return new RemoteCallback<GuidedDecisionTreeEditorContent>() {

            @Override
            public void callback(final GuidedDecisionTreeEditorContent content) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if (versionRecordManager.getCurrentPath() == null) {
                    return;
                }

                GuidedDecisionTreeEditorPresenter.this.content = content;

                model = content.getModel();
                metadata = content.getOverview().getMetadata();
                final PackageDataModelOracleBaselinePayload dataModel = content.getDataModel();
                oracle = oracleFactory.makeAsyncPackageDataModelOracle(versionRecordManager.getCurrentPath(),
                                                                       model,
                                                                       dataModel);

                resetEditorPages(content.getOverview());

                addSourcePage();
                addImportsTab(importsWidget);
                importsWidget.setContent(oracle,
                                         model.getImports(),
                                         isReadOnly);

                view.setModel(model,
                              isReadOnly);
                view.setDataModel(oracle,
                                  isReadOnly);

                view.hideBusyIndicator();

                //If there were any parsing errors give the User the option to remove the broken DRL or ignore it
                if (!model.getParserErrors().isEmpty()) {
                    final ParserMessagesPopup popup = new ParserMessagesPopup(model);
                    popup.show();
                }

                createOriginalHash(model);
            }
        };
    }

    @Override
    public void onSourceTabSelected() {
        service.call(new RemoteCallback<String>() {
            @Override
            public void callback(final String source) {
                updateSource(source);
            }
        }).toSource(versionRecordManager.getCurrentPath(),
                    model);
    }

    @Override
    protected void onValidate(final Command finished) {
        service.call(
                validationPopup.getValidationCallback(finished),
                new CommandErrorCallback(finished)).validate(versionRecordManager.getCurrentPath(),
                                                             model);
    }

    @Override
    protected void save(String commitMessage) {
        service.call(getSaveSuccessCallback(model.hashCode()),
                     new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                                                          model,
                                                                          metadata,
                                                                          commitMessage);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnClose
    @Override
    public void onClose() {
        this.versionRecordManager.clear();
        super.onClose();
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(model);
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }

    public void handleImportAddedEvent(@Observes ImportAddedEvent event) {
        if (!event.getDataModelOracle().equals(this.oracle)) {
            return;
        }
        view.setDataModel(oracle,
                          isReadOnly);
    }

    public void handleImportRemovedEvent(@Observes ImportRemovedEvent event) {
        if (!event.getDataModelOracle().equals(this.oracle)) {
            return;
        }
        view.setDataModel(oracle,
                          isReadOnly);
    }

    public void editModelNode(final Node node,
                              final Command callback) {
        if (node instanceof TypeNode) {
            final EditTypePopup popup = new EditTypePopup((TypeNode) node,
                                                          new com.google.gwt.user.client.Command() {
                                                              @Override
                                                              public void execute() {
                                                                  callback.execute();
                                                              }
                                                          });
            popup.show();
        } else if (node instanceof ConstraintNode) {
            final EditConstraintPopup popup = new EditConstraintPopup((ConstraintNode) node,
                                                                      oracle,
                                                                      new com.google.gwt.user.client.Command() {
                                                                          @Override
                                                                          public void execute() {
                                                                              callback.execute();
                                                                          }
                                                                      });
            popup.show();
        } else if (node instanceof ActionInsertNode) {
            final EditActionInsertPopup popup = new EditActionInsertPopup((ActionInsertNode) node,
                                                                          oracle,
                                                                          new com.google.gwt.user.client.Command() {
                                                                              @Override
                                                                              public void execute() {
                                                                                  callback.execute();
                                                                              }
                                                                          });
            popup.show();
        } else if (node instanceof ActionUpdateNode) {
            final EditActionUpdatePopup popup = new EditActionUpdatePopup((ActionUpdateNode) node,
                                                                          oracle,
                                                                          new com.google.gwt.user.client.Command() {
                                                                              @Override
                                                                              public void execute() {
                                                                                  callback.execute();
                                                                              }
                                                                          });
            popup.show();
        } else if (node instanceof ActionRetractNode) {
            final EditActionRetractPopup popup = new EditActionRetractPopup((ActionRetractNode) node,
                                                                            new com.google.gwt.user.client.Command() {
                                                                                @Override
                                                                                public void execute() {
                                                                                    callback.execute();
                                                                                }
                                                                            });
            popup.show();
        }
    }
}
