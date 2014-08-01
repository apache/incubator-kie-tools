package org.kie.workbench.common.services.datamodeller.driver;

import java.util.ArrayList;
import java.util.List;

public abstract class DriverResult {

    protected List<ModelDriverError> errors = new ArrayList<ModelDriverError>( );

    public List<ModelDriverError> getErrors() {
        return errors;
    }

    public void addError( ModelDriverError error ) {
        errors.add( error );
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

}
