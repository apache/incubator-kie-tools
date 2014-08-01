package org.kie.workbench.common.services.datamodeller.driver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.uberfire.java.nio.file.Path;

public class ModelDriverResult extends DriverResult {

    private DataModel dataModel;

    private Map<String, Path> classPaths = new HashMap<String, Path>(  );

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

    public Path getClassPath(String className) {
        return classPaths.get( className );
    }

    public void setClassPath(String className, Path path) {
        classPaths.put( className, path );
    }

    public Map<String, Path> getClassPaths() {
        return classPaths;
    }
}
