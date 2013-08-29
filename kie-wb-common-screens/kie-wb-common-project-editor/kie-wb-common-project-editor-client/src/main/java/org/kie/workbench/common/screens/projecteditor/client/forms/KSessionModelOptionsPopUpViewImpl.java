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

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Container;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.Style;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.client.common.Popup;

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
    private static final String WORKING_MEMORY_EVENT_LISTENER = ProjectEditorConstants.INSTANCE.WorkingMemoryEventListener();
    private static final String AGENDA_EVENT_LISTENER = ProjectEditorConstants.INSTANCE.AgendaEventListener();
    private static final String PROCESS_EVENT_LISTENER = ProjectEditorConstants.INSTANCE.ProcessEventListener();

    private final Widget content;
    private final String CONSOLE_LOGGER = ProjectEditorConstants.INSTANCE.ConsoleLogger();
    private final String FILE_LOGGER = ProjectEditorConstants.INSTANCE.FileLogger();
    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, KSessionModelOptionsPopUpViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    //    @UiField
//    CheckBox loggerCheckBox;

    //    @UiField
//    ListBox loggerTypeListBox;

    //    @UiField
//    Container loggerEditorPanel;

    //    @UiField
//    Container loggerContainer;

    @UiField
    CheckBox listenerCheckBox;

    @UiField
    ListBox listenerKindListBox;

    @UiField
    TextBox listenerTypeTextBox;

    @UiField
    Container listenerContainer;

    public KSessionModelOptionsPopUpViewImpl() {
        content = uiBinder.createAndBindUi(this);
        listenerKindListBox.addItem(WORKING_MEMORY_EVENT_LISTENER);
        listenerKindListBox.addItem(AGENDA_EVENT_LISTENER);
        listenerKindListBox.addItem(PROCESS_EVENT_LISTENER);

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
    public void enableListenerPanel() {
        listenerCheckBox.setValue(true);
        listenerKindListBox.setEnabled(true);
        listenerTypeTextBox.setEnabled(true);
        listenerContainer.setStyle(PANEL_ENABLED);
    }

    @Override
    public void disableListenerPanel() {
        listenerCheckBox.setValue(false);
        listenerKindListBox.setEnabled(false);
        listenerTypeTextBox.setEnabled(false);
        listenerContainer.setStyle(PANEL_DISABLED);
    }

    @UiHandler("listenerCheckBox")
    public void onListenerPanelToggle(ValueChangeEvent<Boolean> event) {
        presenter.onToggleListenerPanel(event.getValue());
    }

    @UiHandler("listenerKindListBox")
    public void onListenerTypeSelected(ChangeEvent event) {

        if (listenerKindListBox.getValue().equals(WORKING_MEMORY_EVENT_LISTENER)) {
            presenter.onWorkingMemoryEventListenerSelected();
        } else if (listenerKindListBox.getValue().equals(AGENDA_EVENT_LISTENER)) {
            presenter.onAgendaEventListenerSelected();
        } else if (listenerKindListBox.getValue().equals(PROCESS_EVENT_LISTENER)) {
            presenter.onProcessEventListenerSelected();
        }
    }

    @UiHandler("listenerTypeTextBox")
    public void onListenerNameChange(KeyUpEvent event) {
        presenter.onListenerNameChange(listenerTypeTextBox.getValue());
    }

    @Override
    public void setListenerTypeName(String type) {
        listenerTypeTextBox.setText(type);
    }

    @Override
    public void selectWorkingMemoryEventListener() {
        listenerKindListBox.setSelectedValue(WORKING_MEMORY_EVENT_LISTENER);
    }

    @Override
    public void selectAgendaEventListener() {
        listenerKindListBox.setSelectedValue(AGENDA_EVENT_LISTENER);
    }

    @Override
    public void selectProcessEventListener() {
        listenerKindListBox.setSelectedValue(PROCESS_EVENT_LISTENER);
    }
}
