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

import org.uberfire.client.workbench.WorkbenchPart;
import org.uberfire.client.workbench.widgets.panels.WorkbenchTabLayoutPanel;

/**
 * 
 */
public class WorkbenchDragContext {

    private final WorkbenchPart part;

    private final WorkbenchTabLayoutPanel origin;

    public WorkbenchDragContext(final WorkbenchPart part,
                                final WorkbenchTabLayoutPanel origin) {
        this.part = part;
        this.origin = origin;
    }

    public WorkbenchPart getWorkbenchPart() {
        return this.part;
    }

    public WorkbenchTabLayoutPanel getOrigin() {
        return this.origin;
    }

}
