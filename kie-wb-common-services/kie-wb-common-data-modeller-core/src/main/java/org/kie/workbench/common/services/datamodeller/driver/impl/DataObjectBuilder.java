package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.parser.descr.ClassDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.PackageDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.TypeDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.VariableDeclarationDescr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataObjectBuilder {

    private static final Logger logger = LoggerFactory.getLogger( DataObjectBuilder.class );

    /**
     *  Knows how to create a DataObject from the descriptors returned by the parser.
     */
    public DataObject buildDataObject( DataModel dataModel, FileDescr fileDescr ) throws ModelDriverException {

        ClassDescr classDescr = fileDescr.getClassDescr();
        PackageDescr packageDescr = fileDescr.getPackageDescr();
        TypeDescr typeDescr;
        String className = classDescr.getIdentifier().getIdentifier();
        String superClass;
        String packageName = packageDescr != null ? packageDescr.getPackageName() : "";

        if ( logger.isDebugEnabled() ) logger.debug( "Building DataObject for, packageName: " + packageName + ", className: " + className );

        DataObject dataObject = dataModel.addDataObject(packageName, className);
        if ( (typeDescr = classDescr.getSuperClass()) != null) {
            if (typeDescr.isClassOrInterfaceType()) {
                superClass = typeDescr.getClassOrInterfaceType().getClassName();
                dataObject.setSuperClassName( superClass );
            }
        }

        List<FieldDescr> fields = classDescr.getFields( );
        if (fields != null) {
            for (FieldDescr field : fields) {
                buildProperty(dataObject, field);
            }
        }

        return dataObject;
    }

    private void buildProperty( DataObject dataObject, FieldDescr field ) {

        TypeDescr typeDescr;
        List< VariableDeclarationDescr > variableDeclarations;
        String className;

        typeDescr = field.getType();
        className = typeDescr.isPrimitiveType() ? typeDescr.getPrimitiveType().getName() : typeDescr.getClassOrInterfaceType().getClassName();
        variableDeclarations = field.getVariableDeclarations();
        if (variableDeclarations != null) {
            for (VariableDeclarationDescr variable : variableDeclarations) {
                //TODO, detect collections List<SomeThing> the same as we do with the DMO
                dataObject.addProperty( variable.getIdentifier().getIdentifier(), className );
            }
        }
    }

}
