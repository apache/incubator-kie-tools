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
package org.drools.guvnor.client.workbench.widgets.panels;

import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public interface SplitPanel {

    //TODO {manstis} This needs to be TabPanel's height really
    public static final int MIN_SIZE     = 32;

    //TODO {manstis} This needs to come from the Editor being added to the panel really
    public static final int INITIAL_SIZE = 64;

    public void clear();

    public Widget getWidget(Position position);

}
