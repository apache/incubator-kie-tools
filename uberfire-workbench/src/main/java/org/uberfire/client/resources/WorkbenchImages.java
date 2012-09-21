/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.resources;


import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * GWT managed images for Workbench
 */
public interface WorkbenchImages
        extends
        ClientBundle {

    //DnD drag proxy
    @Source("images/dragProxy.png")
    ImageResource workbenchPanelDragProxy();

    //CompassDropController North arrow
    @Source("images/compass-north.png")
    ImageResource compassNorth();

    //CompassDropController South arrow
    @Source("images/compass-south.png")
    ImageResource compassSouth();

    //CompassDropController East arrow
    @Source("images/compass-east.png")
    ImageResource compassEast();

    //CompassDropController West arrow
    @Source("images/compass-west.png")
    ImageResource compassWest();

    //CompassDropController Centre
    @Source("images/compass-centre.png")
    ImageResource compassCentre();

    //A warning triangle
    @Source("images/warning-large.png")
    ImageResource warningLarge();

    //Scroll TabPanel left
    @Source("images/tabPanelScrollLeft.png")
    ImageResource tabPanelScrollLeft();

    //Scroll TabPanel right
    @Source("images/tabPanelScrollRight.png")
    ImageResource tabPanelScrollRight();
    
    CollapseExpand collapseExpand();    
}
