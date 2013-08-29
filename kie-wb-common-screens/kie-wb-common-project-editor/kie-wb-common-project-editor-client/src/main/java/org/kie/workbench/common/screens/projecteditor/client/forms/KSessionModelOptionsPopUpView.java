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

public interface KSessionModelOptionsPopUpView {


    interface Presenter {

        void onToggleLoggerPanel(Boolean value);

        void onConsoleLoggerSelected();

        void onFileLoggerSelected();

        void onToggleListenerPanel(Boolean value);

        void onWorkingMemoryEventListenerSelected();

        void onAgendaEventListenerSelected();

        void onProcessEventListenerSelected();

        void onListenerNameChange(String value);
    }

    void setPresenter(Presenter presenter);

    void setLoggerEditor(LoggerEditorPanel loggerEditor);

    void clearLoggerEditor();

    void enableLoggerPanel();

    void disableLoggerPanel();

    void enableListenerPanel();

    void disableListenerPanel();

    void setListenerTypeName(String type);

    void selectWorkingMemoryEventListener();

    void selectAgendaEventListener();

    void selectProcessEventListener();

    void show();
}
