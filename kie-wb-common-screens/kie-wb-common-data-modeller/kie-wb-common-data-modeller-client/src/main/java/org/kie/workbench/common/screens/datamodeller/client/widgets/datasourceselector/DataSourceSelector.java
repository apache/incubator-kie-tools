/*
 * Copyright 2016 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.datasourceselector;

import org.guvnor.common.services.project.model.Module;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * Convenient interface for decoupling data sources selection from data modeler module.
 */
public interface DataSourceSelector {

    /**
     * Use this method to restrict the data sources selection to a given project.
     * @param module Module where to select the data sources from.
     */
    void setModuleSelection(Module module);

    /**
     * Use this method to select global data sources.
     */
    void setGlobalSelection();

    /**
     * Opens the data source selector in popup mode.
     * @param onSelectCommand when a data source is selected the popup is automatically closed and the onSelectCommand
     * is executed to let clients now about the selection.
     * @param onCloseCommand executed when the popup is closed with no selection.
     */
    void show(ParameterizedCommand<DataSourceInfo> onSelectCommand, Command onCloseCommand);
}
