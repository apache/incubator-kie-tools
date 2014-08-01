package org.kie.workbench.common.screens.datamodeller.model;

import java.util.ArrayList;
import java.util.List;

public abstract class DataModelerResult {

    protected DataModelerResult() {
    }

    protected List<DataModelerError> errors = new ArrayList<DataModelerError>(  );

    public List<DataModelerError> getErrors() {
        return errors;
    }

    public void setErrors( List<DataModelerError> errors ) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}
