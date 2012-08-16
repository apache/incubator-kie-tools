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

import org.uberfire.client.workbench.Position;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * 
 */
public class PerspectivePartDefinition {

    private Position     position;
    private PlaceRequest place;

    public PerspectivePartDefinition() {
    }

    public PerspectivePartDefinition(final Position position,
                           final PlaceRequest place) {
        this.position = position;
        this.place = place;
    }

    public Position getPosition() {
        return position;
    }

    public PlaceRequest getPlace() {
        return place;
    }

}
