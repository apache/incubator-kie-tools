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

import org.kie.workbench.common.services.shared.kmodule.ConsoleLogger;
import org.kie.workbench.common.services.shared.kmodule.FileLogger;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;

public class KSessionModelOptionsPopUp
        implements KSessionModelOptionsPopUpView.Presenter {

    private final KSessionModelOptionsPopUpView view;
    private final ConsoleLoggerEditor consoleLoggerEditor;
    private final FileLoggerEditor fileLoggerEditor;
    private KSessionModel model;

    @Inject
    public KSessionModelOptionsPopUp(
            KSessionModelOptionsPopUpView view,
            ConsoleLoggerEditor consoleLoggerEditor,
            FileLoggerEditor fileLoggerEditor) {
        this.view = view;
        this.consoleLoggerEditor = consoleLoggerEditor;
        this.fileLoggerEditor = fileLoggerEditor;
        view.setPresenter(this);
    }

    public void show(KSessionModel kSessionModel) {
        this.model = kSessionModel;

        view.setListeners(kSessionModel.getListeners());
        view.setWorkItemHandlers(kSessionModel.getWorkItemHandelerModels());
//        setUpLoggerPanel();
        view.show();
    }


    private void setUpLoggerPanel() {
        if (model.getLogger() != null) {
            view.enableLoggerPanel();
            if (model.getLogger() instanceof ConsoleLogger) {
                consoleLoggerEditor.setModel((ConsoleLogger) model.getLogger());
                view.setLoggerEditor(consoleLoggerEditor);
            } else if (model.getLogger() instanceof FileLogger) {
                fileLoggerEditor.setModel((FileLogger) model.getLogger());
                view.setLoggerEditor(fileLoggerEditor);
            }
        }
    }

    @Override
    public void onToggleLoggerPanel(Boolean value) {
        if (value) {
            view.enableLoggerPanel();
            onConsoleLoggerSelected();
        } else {
            view.disableLoggerPanel();
            view.clearLoggerEditor();
            model.setLogger(null);
        }
    }

    @Override
    public void onConsoleLoggerSelected() {
        ConsoleLogger consoleLogger = new ConsoleLogger();
        model.setLogger(consoleLogger);
        consoleLoggerEditor.setModel(consoleLogger);
        view.setLoggerEditor(consoleLoggerEditor);
    }

    @Override
    public void onFileLoggerSelected() {
        view.clearLoggerEditor();
        view.setLoggerEditor(fileLoggerEditor);
    }
}
