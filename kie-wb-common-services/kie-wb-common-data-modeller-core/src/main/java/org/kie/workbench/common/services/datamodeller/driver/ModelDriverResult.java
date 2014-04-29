package org.kie.workbench.common.services.datamodeller.driver;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.DataModel;

public class ModelDriverResult {

    private DataModel dataModel;

    private List<ModelDriverError> errors = new ArrayList<ModelDriverError>( );

    public ModelDriverResult() {
    }

    public ModelDriverResult( DataModel dataModel ) {
        this.dataModel = dataModel;
    }

    public ModelDriverResult( DataModel dataModel, List<ModelDriverError> errors ) {
        this.dataModel = dataModel;
        this.errors = errors;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel( DataModel dataModel ) {
        this.dataModel = dataModel;
    }

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
