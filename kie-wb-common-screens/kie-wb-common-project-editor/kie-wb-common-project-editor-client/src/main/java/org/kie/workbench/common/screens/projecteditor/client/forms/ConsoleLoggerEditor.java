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

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.shared.kmodule.ConsoleLogger;

public class ConsoleLoggerEditor
        implements LoggerEditorPanel, ConsoleLoggerEditorView.Presenter {

    private final ConsoleLoggerEditorView view;
    private ConsoleLogger model;

    @Inject
    public ConsoleLoggerEditor(ConsoleLoggerEditorView view) {
        this.view = view;
    }

    public void setModel(ConsoleLogger consoleLogger) {
        this.model = consoleLogger;
        view.setName(consoleLogger.getName());
        view.setPresenter(this);
    }

    @Override
    public void setEnabled(boolean b) {
        if (b) {
            view.enableEditing();
        } else {
            view.disableEditing();
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onNameChange(String value) {
        model.setName(value);
    }
}
