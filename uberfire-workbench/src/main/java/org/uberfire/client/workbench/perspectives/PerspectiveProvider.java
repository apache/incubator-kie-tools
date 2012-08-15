/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.perspectives;

import org.uberfire.client.workbench.widgets.panels.PanelManager;

/**
 * A Perspective Provider sets the default layout of the Workbench.
 */
public interface PerspectiveProvider {

    /**
     * The name of the perspective
     * 
     * @return
     */
    String getName();

    /**
     * Called by the Workbench for the Perspective Provider to build the
     * applicable panels etc. Implementations should add WorkbenchPanels to the
     * root panel obtained by calling panelManager.getRoot(). All existing
     * panels will be closed.
     * 
     * @param panelManager
     */
    void buildWorkbench(final PanelManager panelManager);

}
