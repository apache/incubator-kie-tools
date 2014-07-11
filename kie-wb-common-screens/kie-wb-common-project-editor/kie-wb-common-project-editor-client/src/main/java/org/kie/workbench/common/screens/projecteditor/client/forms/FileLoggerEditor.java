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
import org.kie.workbench.common.services.shared.kmodule.FileLogger;

public class FileLoggerEditor
        implements LoggerEditorPanel {

    private final FileLoggerEditorView view;
    private FileLogger model;

    @Inject
    public FileLoggerEditor(FileLoggerEditorView view) {
        this.view = view;
    }

    public void setModel(FileLogger fileLogger) {
        model = fileLogger;
        view.setName(fileLogger.getName());
        view.setFile(fileLogger.getFile());
        view.setInterval(fileLogger.getInterval());
    }

    @Override
    public void setEnabled(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
