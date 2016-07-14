/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server.testclasses.annotations;

public class SmurfHouse {

    @SmurfFieldDescriptor(gender = "M", description = "Brains")
    private Smurf occupant;

    @SmurfFieldPositionDescriptor(value = 1)
    private Smurf positionedOccupant;

    public Smurf getOccupant() {
        return occupant;
    }

    public void setOccupant( Smurf occupant ) {
        this.occupant = occupant;
    }

    public Smurf getPositionedOccupant() {
        return positionedOccupant;
    }

    public void setPositionedOccupant( Smurf positionedOccupant ) {
        this.positionedOccupant = positionedOccupant;
    }
}
