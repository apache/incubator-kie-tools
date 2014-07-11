/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.List;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.base.Style;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;
import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.uberfire.client.common.Popup;

public class KSessionModelOptionsPopUpViewImpl
        extends Popup
        implements KSessionModelOptionsPopUpView {

    private static final Style PANEL_ENABLED = new Style() {
        @Override
        public String get() {
            return ProjectEditorResources.INSTANCE.mainCss().panelEnabled();
        }
    };

    private static final Style PANEL_DISABLED = new Style() {
        @Override
        public String get() {
            return ProjectEditorResources.INSTANCE.mainCss().panelDisabled();
        }
    };
    private static final String WORKING_MEMORY_EVENT_LISTENER = ProjectEditorResources.CONSTANTS.WorkingMemoryEventListener();
    private static final String AGENDA_EVENT_LISTENER = ProjectEditorResources.CONSTANTS.AgendaEventListener();
    private static final String PROCESS_EVENT_LISTENER = ProjectEditorResources.CONSTANTS.ProcessEventListener();

    private final Widget content;
    private final String CONSOLE_LOGGER = ProjectEditorResources.CONSTANTS.ConsoleLogger();
    private final String FILE_LOGGER = ProjectEditorResources.CONSTANTS.FileLogger();
    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, KSessionModelOptionsPopUpViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField(provided = true)
    ListenersPanel listenersPanel;

    @UiField(provided = true)
    WorkItemHandlersPanel workItemHandlersPanel;

    @UiField
    Button closeButton;

    //    @UiField
//    CheckBox loggerCheckBox;

    //    @UiField
//    ListBox loggerTypeListBox;

    //    @UiField
//    Container loggerEditorPanel;

    //    @UiField
//    Container loggerContainer;

    @Inject
    public KSessionModelOptionsPopUpViewImpl(ListenersPanel listenersPanel,
            WorkItemHandlersPanel workItemHandlersPanel) {
        this.listenersPanel = listenersPanel;
        this.workItemHandlersPanel = workItemHandlersPanel;

        content = uiBinder.createAndBindUi(this);

//        loggerTypeListBox.addItem(CONSOLE_LOGGER);
//        loggerTypeListBox.addItem(FILE_LOGGER);
    }

    @Override
    public Widget getContent() {
        return content;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLoggerEditor(LoggerEditorPanel loggerEditor) {
        clearLoggerEditor();
//        loggerEditorPanel.add(loggerEditor);
    }

    @Override
    public void clearLoggerEditor() {
//        loggerEditorPanel.clear();
    }

    @Override
    public void enableLoggerPanel() {
//        loggerCheckBox.setValue(true);
//        loggerTypeListBox.setEnabled(true);
//        loggerContainer.setStyle(PANEL_ENABLED);
    }

    @Override
    public void disableLoggerPanel() {
//        loggerCheckBox.setValue(false);
//        loggerTypeListBox.setEnabled(false);
//        loggerContainer.setStyle(PANEL_DISABLED);
    }

//    @UiHandler("loggerCheckBox")
//    public void onLoggerPanelToggle(ValueChangeEvent<Boolean> event) {
//        presenter.onToggleLoggerPanel(event.getValue());
//    }
//
//    @UiHandler("loggerTypeListBox")
//    public void onLoggerTypeSelected(ChangeEvent event) {
//
//        if (loggerTypeListBox.getValue().equals(FILE_LOGGER)) {
//            presenter.onFileLoggerSelected();
//        } else if (loggerTypeListBox.getValue().equals(CONSOLE_LOGGER)) {
//            presenter.onConsoleLoggerSelected();
//        }
//    }

    @Override
    public void setListeners(List<ListenerModel> listeners) {
        listenersPanel.setListeners(listeners);
    }

    @Override
    public void setWorkItemHandlers(List<WorkItemHandlerModel> workItemHandlerModels) {
        workItemHandlersPanel.setHandlerModels(workItemHandlerModels);
    }

    @UiHandler("closeButton")
    public void handleClick(ClickEvent event) {
        hide();
    }
}
