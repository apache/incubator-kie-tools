package org.kie.workbench.common.widgets.client.datamodel.testclasses.annotations;

public class SmurfHouse {

    @SmurfFieldDescriptor(gender = "M", description = "Brains")
    private Smurf occupant;

    @SmurfFieldPositionDescriptor(value = 1)
    private Smurf positionedOccupant;

}
