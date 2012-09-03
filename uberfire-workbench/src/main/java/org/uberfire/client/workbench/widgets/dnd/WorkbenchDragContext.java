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
package org.uberfire.client.workbench.widgets.dnd;

import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;

/**
 * The context of a drag and drop operation within the Workbench.
 */
public class WorkbenchDragContext {

    private final PartDefinition  sourcePart;
    private final PanelDefinition sourcePanel;

    public WorkbenchDragContext(final PartDefinition sourcePart,
                                final PanelDefinition sourcePanel) {
        this.sourcePart = sourcePart;
        this.sourcePanel = sourcePanel;
    }

    /**
     * @return the sourcePart
     */
    public PartDefinition getSourcePart() {
        return sourcePart;
    }

    /**
     * @return the sourcePanel
     */
    public PanelDefinition getSourcePanel() {
        return sourcePanel;
    }
    
}