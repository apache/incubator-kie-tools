/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.event;

import org.uberfire.workbench.events.UberFireEvent;

/**
 * <p>Event when a shape set in wizard is selected and a new empty diagram for it must be created and set active.</p>
 */
public class CreateEmptyDiagramEvent implements UberFireEvent {

    private String shapeSetId;

    public CreateEmptyDiagramEvent( final String shapeSetId ) {
        this.shapeSetId = shapeSetId;
    }

    public String getShapeSetId() {
        return shapeSetId;
    }

    @Override
    public String toString() {
        return "ShapeSetSelectedEvent [shapeSet=" + shapeSetId + "]";
    }

}
