/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ContextUtils;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants;
import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateDiagramFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateProcessFormsSessionCommand;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateSelectedFormsSessionCommand;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.ProjectDiagramEditorMenuItemsBuilder;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@DiagramEditor
@WorkbenchEditor(identifier = BPMNDiagramEditor.EDITOR_ID, supportedTypes = {BPMNDiagramResourceType.class})
public class BPMNDiagramEditor extends AbstractProjectDiagramEditor<BPMNDiagramResourceType> {

    public static final String EDITOR_ID = "BPMNDiagramEditor";

    private final ManagedInstance<GenerateProcessFormsSessionCommand> generateProcessFormSessionCommands;
    private final ManagedInstance<GenerateDiagramFormsSessionCommand> generateDiagramFormsSessionCommands;
    private final ManagedInstance<GenerateSelectedFormsSessionCommand> generateSelectedFormsSessionCommands;

    @Inject
    public BPMNDiagramEditor(final View view,
                             final PlaceManager placeManager,
                             final ErrorPopupPresenter errorPopupPresenter,
                             final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                             final SavePopUpPresenter savePopUpPresenter,
                             final BPMNDiagramResourceType resourceType,
                             final ClientProjectDiagramService projectDiagramServices,
                             final SessionManager sessionManager,
                             final SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> sessionPresenterFactory,
                             final SessionCommandFactory sessionCommandFactory,
                             final ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder,
                             final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                             final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                             final ProjectMessagesListener projectMessagesListener,
                             final DiagramClientErrorHandler diagramClientErrorHandler,
                             final ManagedInstance<GenerateProcessFormsSessionCommand> generateProcessFormSessionCommands,
                             final ManagedInstance<GenerateDiagramFormsSessionCommand> generateDiagramFormsSessionCommands,
                             final ManagedInstance<GenerateSelectedFormsSessionCommand> generateSelectedFormsSessionCommands,
                             final ClientTranslationService translationService) {
        super(view,
              placeManager,
              errorPopupPresenter,
              changeTitleNotificationEvent,
              savePopUpPresenter,
              resourceType,
              projectDiagramServices,
              sessionManager,
              sessionPresenterFactory,
              sessionCommandFactory,
              menuItemsBuilder,
              onDiagramFocusEvent,
              onDiagramLostFocusEvent,
              projectMessagesListener,
              diagramClientErrorHandler,
              translationService);
        this.generateProcessFormSessionCommands = generateProcessFormSessionCommands;
        this.generateDiagramFormsSessionCommands = generateDiagramFormsSessionCommands;
        this.generateSelectedFormsSessionCommands = generateSelectedFormsSessionCommands;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.doStartUp(path,
                        place);
    }

    @Override
    protected void initializeCommands(final Map<Class, ClientSessionCommand> commands) {
        super.initializeCommands(commands);
        commands.put(GenerateProcessFormsSessionCommand.class,
                     generateProcessFormSessionCommands.get());
        commands.put(GenerateDiagramFormsSessionCommand.class,
                     generateDiagramFormsSessionCommands.get());
        commands.put(GenerateSelectedFormsSessionCommand.class,
                     generateSelectedFormsSessionCommands
                             .get()
                             .setElementAcceptor(ContextUtils::isFormGenerationSupported));
    }

    @Override
    protected void makeAdditionalStunnerMenus(final FileMenuBuilder fileMenuBuilder) {
        super.makeAdditionalStunnerMenus(fileMenuBuilder);
        fileMenuBuilder
                .addNewTopLevelMenu(newFormsGenerationMenuItem(() -> executeFormsCommand(GenerateProcessFormsSessionCommand.class),
                                                               () -> executeFormsCommand(GenerateDiagramFormsSessionCommand.class),
                                                               () -> executeFormsCommand(GenerateSelectedFormsSessionCommand.class)));
    }

    @Override
    protected int getCanvasWidth() {
        return (int) BPMNGraphFactory.GRAPH_DEFAULT_WIDTH;
    }

    @Override
    protected int getCanvasHeight() {
        return (int) BPMNGraphFactory.GRAPH_DEFAULT_HEIGHT;
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }

    @OnOpen
    public void onOpen() {
        super.doOpen();
    }

    @OnClose
    public void onClose() {
        super.doClose();
    }

    @OnFocus
    public void onFocus() {
        super.doFocus();
    }

    @OnLostFocus
    public void onLostFocus() {
        super.doLostFocus();
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
    public Menus getMenus() {
        return super.getMenus();
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return getView().asWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose(getCurrentDiagramHash());
    }

    private void executeFormsCommand(Class<? extends AbstractClientSessionCommand> type) {
        showLoadingViews();
        getCommand(type)
                .execute(new ClientSessionCommand.Callback<ClientRuntimeError>() {
                    @Override
                    public void onSuccess() {
                        hideLoadingViews();
                    }

                    @Override
                    public void onError(ClientRuntimeError error) {
                        showError(error);
                    }
                });
    }

    private MenuItem newFormsGenerationMenuItem(final Command generateProcessForm,
                                                final Command generateAllForms,
                                                final Command generateSelectedForms) {
        final DropDownMenu menu = new DropDownMenu() {{
            setPull(Pull.RIGHT);
        }};

        menu.add(new AnchorListItem(getTranslationService().getKeyValue(BPMNClientConstants.EditorGenerateProcessForm)) {{
            setIcon(IconType.LIST_ALT);
            setIconPosition(IconPosition.LEFT);
            setTitle(getTranslationService().getKeyValue(BPMNClientConstants.EditorGenerateProcessForm));
            addClickHandler(event -> generateProcessForm.execute());
        }});
        menu.add(new AnchorListItem(getTranslationService().getKeyValue(BPMNClientConstants.EditorGenerateAllForms)) {{
            setIcon(IconType.LIST_ALT);
            setIconPosition(IconPosition.LEFT);
            setTitle(getTranslationService().getKeyValue(BPMNClientConstants.EditorGenerateAllForms));
            addClickHandler(event -> generateAllForms.execute());
        }});
        menu.add(new AnchorListItem(getTranslationService().getKeyValue(BPMNClientConstants.EditorGenerateSelectionForms)) {{
            setIcon(IconType.LIST_ALT);
            setIconPosition(IconPosition.LEFT);
            setTitle(getTranslationService().getKeyValue(BPMNClientConstants.EditorGenerateSelectionForms));
            addClickHandler(event -> generateSelectedForms.execute());
        }});
        final IsWidget group = new ButtonGroup() {{
            add(new Button() {{
                setToggleCaret(true);
                setDataToggle(Toggle.DROPDOWN);
                setIcon(IconType.LIST_ALT);
                setSize(ButtonSize.SMALL);
                setTitle(getTranslationService().getKeyValue(BPMNClientConstants.EditorFormGenerationTitle));
            }});
            add(menu);
        }};
        return ProjectDiagramEditorMenuItemsBuilder.buildItem(group);
    }
}
