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
package org.uberfire.client.workbench.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * 
 */
@Portable
public class PartDefinition {

    private String          title;

    private PlaceRequest    place;

    private PanelDefinition parentPanel;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return the place
     */
    public PlaceRequest getPlace() {
        return place;
    }

    /**
     * @param place
     *            the place to set
     */
    public void setPlace(final PlaceRequest place) {
        this.place = place;
    }

    /**
     * @return the parentPanel
     */
    public PanelDefinition getParentPanel() {
        return parentPanel;
    }

    /**
     * @param parentPanel
     *            the parentPanel to set
     */
    public void setParentPanel(final PanelDefinition parentPanel) {
        this.parentPanel = parentPanel;
    }

}
