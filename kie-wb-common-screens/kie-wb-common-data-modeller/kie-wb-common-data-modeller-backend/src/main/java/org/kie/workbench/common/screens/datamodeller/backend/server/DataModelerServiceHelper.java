/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.messageconsole.events.SystemMessage;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.forge.roaster.model.SyntaxError;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationMemberDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelerError;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.JavaTypeInfoTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationMemberDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.JavaTypeInfo;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.ObjectSource;
import org.kie.workbench.common.services.datamodeller.core.PropertyTypeFactory;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationDefinitionImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationMemberDefinitionImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ModelFactoryImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverError;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.rpc.SessionInfo;

@ApplicationScoped
public class DataModelerServiceHelper {

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private User identity;

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    private ProjectService projectService;

    public DataModel to2Domain( DataModelTO dataModelTO ) {
        DataModel dataModel = ModelFactoryImpl.getInstance().newModel();
        List<DataObjectTO> dataObjects = dataModelTO.getDataObjects();
        DataObject dataObject;

        if ( dataObjects != null ) {
            for ( DataObjectTO dataObjectTO : dataObjects ) {
                dataObject = dataModel.addDataObject( dataObjectTO.getPackageName(), dataObjectTO.getName() );
                to2Domain( dataObjectTO, dataObject );
            }
        }
        return dataModel;
    }

    public DataModelTO domain2To( DataModel dataModel, Map<String, Path> classPaths, DataModelTO.TOStatus initialStatus, boolean calculateFingerprints ) throws Exception {
        DataModelTO dataModelTO = new DataModelTO();
        List<DataObject> dataObjects = new ArrayList<DataObject>();
        List<DataObject> externalDataObjects = new ArrayList<DataObject>();
        Map<String, String> externalClasses = new HashMap<String, String>();

        dataObjects.addAll( dataModel.getDataObjects() );
        externalDataObjects.addAll( dataModel.getDataObjects( ObjectSource.DEPENDENCY ) );

        DataObjectTO dataObjectTO;

        if ( dataObjects != null ) {
            for ( DataObject dataObject : dataObjects ) {
                dataObjectTO = new DataObjectTO( dataObject.getName(), dataObject.getPackageName(), dataObject.getSuperClassName(), dataObject.isAbstract(), dataObject.isInterface(), dataObject.isFinal() );
                if ( initialStatus != null ) {
                    dataObjectTO.setStatus( initialStatus );
                }
                domain2To( dataObject, dataObjectTO, initialStatus );
                dataModelTO.getDataObjects().add( dataObjectTO );
                if ( calculateFingerprints ) {
                    dataObjectTO.setFingerPrint( calculateFingerPrint( dataObjectTO.getStringId() ) );
                }
                if ( classPaths != null && classPaths.get( dataObjectTO.getClassName() ) != null ) {
                    dataObjectTO.setPath( Paths.convert( classPaths.get( dataObjectTO.getClassName() ) ) );
                }
            }
        }

        for ( DataObject externalDataObject : externalDataObjects ) {
            dataObjectTO = new DataObjectTO( externalDataObject.getName(), externalDataObject.getPackageName(), externalDataObject.getSuperClassName(), externalDataObject.isAbstract(), externalDataObject.isInterface(), externalDataObject.isFinal() );
            if ( !externalClasses.containsKey( dataObjectTO.getClassName() ) ) {
                //TODO if needed add the external clases properties.
                //version 6.0.1 will not do anything with external classes properties, so we can
                //skip properties loading.
                dataModelTO.getExternalClasses().add( dataObjectTO );
                externalClasses.put( dataObjectTO.getClassName(), dataObjectTO.getClassName() );
            }

        }

        return dataModelTO;
    }

    public void domain2To( DataObject dataObject, DataObjectTO dataObjectTO, DataModelTO.TOStatus initialStatus ) {
        dataObjectTO.setName( dataObject.getName() );
        dataObjectTO.setPackageName( dataObject.getPackageName() );
        dataObjectTO.setOriginalClassName( dataObject.getClassName() );
        dataObjectTO.setSuperClassName( dataObject.getSuperClassName() );
        List<ObjectProperty> properties = new ArrayList<ObjectProperty>();
        properties.addAll( dataObject.getProperties().values() );

        List<ObjectPropertyTO> propertiesTO = new ArrayList<ObjectPropertyTO>();
        PropertyTypeFactory typeFactory = PropertyTypeFactoryImpl.getInstance();

        //process type level annotations
        for ( Annotation annotation : dataObject.getAnnotations() ) {
            AnnotationTO annotationTO = domain2To( annotation );
            if ( annotationTO != null ) {
                dataObjectTO.addAnnotation( annotationTO );
            }
        }

        ObjectPropertyTO propertyTO;
        for ( ObjectProperty property : properties ) {
            propertyTO = new ObjectPropertyTO( property.getName(), property.getClassName(), property.isMultiple(), typeFactory.isBasePropertyType( property.getClassName() ), property.getBag(), property.getModifiers() );
            propertyTO.setOriginalName( property.getName() );
            if ( initialStatus != null ) {
                propertyTO.setStatus( initialStatus );
            }
            propertiesTO.add( propertyTO );
            //process member level annotations.
            for ( Annotation annotation : property.getAnnotations() ) {
                AnnotationTO annotationTO = domain2To( annotation );
                if ( annotationTO != null ) {
                    propertyTO.addAnnotation( annotationTO );
                }
            }
        }

        dataObjectTO.setProperties( propertiesTO );
    }

    public DataObject to2Domain( DataObjectTO dataObjectTO ) {
        DataObject dataObject = new DataObjectImpl( dataObjectTO.getPackageName(), dataObjectTO.getName() );
        to2Domain( dataObjectTO, dataObject );
        return dataObject;
    }

    public void to2Domain( DataObjectTO dataObjectTO, DataObject dataObject ) {
        dataObject.setName( dataObjectTO.getName() );
        List<ObjectPropertyTO> properties = dataObjectTO.getProperties();
        dataObject.setSuperClassName( dataObjectTO.getSuperClassName() );

        //process type level annotations.
        for ( AnnotationTO annotationTO : dataObjectTO.getAnnotations() ) {
            Annotation annotation = to2Domain( annotationTO );
            if ( annotation != null ) {
                dataObject.addAnnotation( annotation );
            }
        }

        if ( properties != null ) {
            ObjectProperty property;
            for ( ObjectPropertyTO propertyTO : properties ) {
                property = dataObject.addProperty( propertyTO.getName(), propertyTO.getClassName(), propertyTO.isMultiple(), propertyTO.getBag(), propertyTO.getModifiers() );
                //process member level annotations.
                for ( AnnotationTO annotationTO : propertyTO.getAnnotations() ) {
                    Annotation annotation = to2Domain( annotationTO );
                    if ( annotation != null ) {
                        property.addAnnotation( annotation );
                    }
                }
            }
        }
    }

    public ObjectProperty to2Domain( ObjectPropertyTO propertyTO ) {
        ObjectPropertyImpl property = new ObjectPropertyImpl( propertyTO.getName(), propertyTO.getClassName(), propertyTO.isMultiple(), propertyTO.getBag(), propertyTO.getModifiers() );
        for ( AnnotationTO annotationTO : propertyTO.getAnnotations() ) {
            Annotation annotation = to2Domain( annotationTO );
            if ( annotation != null ) {
                property.addAnnotation( annotation );
            }
        }
        return property;
    }

    public Annotation to2Domain( AnnotationTO annotationTO ) {
        AnnotationDefinition annotationDefinition = to2Domain( annotationTO.getAnnotationDefinition() );
        Annotation annotation = new AnnotationImpl( annotationDefinition );
        Object memberValue;
        for ( AnnotationMemberDefinition memberDefinition : annotationDefinition.getAnnotationMembers() ) {
            memberValue = annotationTO.getValue( memberDefinition.getName() );
            if ( memberValue != null ) {
                annotation.setValue( memberDefinition.getName(), memberValue );
            }
        }
        return annotation;
    }

    public AnnotationDefinition to2Domain( AnnotationDefinitionTO annotationDefinitionTO ) {
        AnnotationDefinitionImpl annotationDefinition = new AnnotationDefinitionImpl( annotationDefinitionTO.getName(), annotationDefinitionTO.getClassName(), annotationDefinitionTO.getShortDescription(), annotationDefinitionTO.getDescription(), annotationDefinitionTO.isObjectAnnotation(), annotationDefinitionTO.isPropertyAnnotation() );
        AnnotationMemberDefinition memberDefinition;
        for ( AnnotationMemberDefinitionTO memberDefinitionTO : annotationDefinitionTO.getAnnotationMembers() ) {
            memberDefinition = new AnnotationMemberDefinitionImpl( memberDefinitionTO.getName(), memberDefinitionTO.getClassName(), memberDefinitionTO.isEnum(), memberDefinitionTO.getDefaultValue(), memberDefinitionTO.getShortDescription(), memberDefinitionTO.getDescription() );
            annotationDefinition.addMember( memberDefinition );
        }
        return annotationDefinition;
    }

    public AnnotationTO domain2To( Annotation annotation ) {
        AnnotationDefinitionTO annotationDefinitionTO = domain2To( annotation.getAnnotationDefinition() );
        AnnotationTO annotationTO = new AnnotationTO( annotationDefinitionTO );
        Object memberValue;
        for ( AnnotationMemberDefinitionTO memberDefinitionTO : annotationDefinitionTO.getAnnotationMembers() ) {
            memberValue = annotation.getValue( memberDefinitionTO.getName() );
            if ( memberValue != null ) {
                annotationTO.setValue( memberDefinitionTO.getName(), memberValue );
            }
        }
        return annotationTO;
    }

    public AnnotationDefinitionTO domain2To( AnnotationDefinition annotationDefinition ) {

        AnnotationDefinitionTO annotationDefinitionTO = new AnnotationDefinitionTO( annotationDefinition.getName(), annotationDefinition.getClassName(), annotationDefinition.getShortDescription(), annotationDefinition.getDescription(), annotationDefinition.isObjectAnnotation(), annotationDefinition.isPropertyAnnotation() );
        AnnotationMemberDefinitionTO memberDefinitionTO;
        for ( AnnotationMemberDefinition memberDefinition : annotationDefinition.getAnnotationMembers() ) {
            memberDefinitionTO = new AnnotationMemberDefinitionTO( memberDefinition.getName(), memberDefinition.getClassName(), memberDefinition.isPrimitiveType(), memberDefinition.isEnum(), memberDefinition.defaultValue(), memberDefinition.getShortDescription(), memberDefinition.getDescription() );
            annotationDefinitionTO.addMember( memberDefinitionTO );
        }

        return annotationDefinitionTO;
    }

    public JavaTypeInfoTO domain2TO( JavaTypeInfo javaTypeInfo ) {
        if ( javaTypeInfo == null ) {
            return null;
        }

        JavaTypeInfoTO javaTypeInfoTO = new JavaTypeInfoTO( javaTypeInfo.getName(), javaTypeInfo.getPackageName() );
        javaTypeInfoTO.setAnnotation( javaTypeInfo.isAnnotation() );
        javaTypeInfoTO.setClass( javaTypeInfo.isClass() );
        javaTypeInfoTO.setInterface( javaTypeInfo.isInterface() );
        javaTypeInfoTO.setEnum( javaTypeInfo.isEnum() );
        javaTypeInfoTO.setPackagePrivate( javaTypeInfo.isPackagePrivate() );
        javaTypeInfoTO.setPrivate( javaTypeInfo.isPrivate() );
        javaTypeInfoTO.setProtected( javaTypeInfo.isProtected() );
        javaTypeInfoTO.setPublic( javaTypeInfo.isPublic() );

        return javaTypeInfoTO;
    }

    public String calculateFingerPrint( String str ) {
        return Base64.encodeBase64String( DigestUtils.sha( str ) );
    }

    public String calculateFingerPrint( Object obj ) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream( byteArrayOutputStream );
        objectOutputStream.writeObject( obj );
        objectOutputStream.close();
        byte[] fingerPrint = DigestUtils.sha( byteArrayOutputStream.toByteArray() );
        return Base64.encodeBase64String( fingerPrint );
    }

    public Map<String, String> claculateFingerPrints( DataModelTO dataModelTO ) {
        Map<String, String> fingerPrints = new HashMap<String, String>();
        for ( DataObjectTO dataObjectTO : dataModelTO.getDataObjects() ) {
            fingerPrints.put( dataObjectTO.getClassName(), calculateFingerPrint( dataObjectTO.getStringId() ) );
        }
        return fingerPrints;
    }

    /**
     * Returns the list of persistent renamed data objects.
     */
    public Map<String, String> calculatePersistentDataObjectRenames( DataModelTO dataModelTO ) {
        Map<String, String> renames = new HashMap<String, String>();
        for ( DataObjectTO dataObjectTO : dataModelTO.getDataObjects() ) {
            if ( !dataObjectTO.isVolatile() && dataObjectTO.classNameChanged() ) {
                renames.put( dataObjectTO.getOriginalClassName(), dataObjectTO.getClassName() );
            }
        }
        return renames;
    }

    /**
     * Returns the list of persistent deleted data objects.
     */
    public List<String> calculatePersistentDataObjectDeletions( DataModelTO dataModelTO ) {
        List<String> deletions = new ArrayList<String>();
        for ( DataObjectTO dataObjectTO : dataModelTO.getDeletedDataObjects() ) {
            if ( !dataObjectTO.isVolatile() ) {
                deletions.add( dataObjectTO.getOriginalClassName() );
            }
        }
        return deletions;
    }

    public List<DataModelerError> toDataModelerError( List<ModelDriverError> errors ) {
        List<DataModelerError> result = new ArrayList<DataModelerError>();
        if ( errors == null ) {
            return result;
        }

        for ( ModelDriverError error : errors ) {
            DataModelerError dataModelerError = new DataModelerError(
                    error.getId(),
                    error.getMessage(),
                    SystemMessage.Level.ERROR,
                    Paths.convert( error.getFile() ),
                    error.getLine(),
                    error.getColumn() );
            result.add( dataModelerError );
        }
        return result;
    }

    public List<DataModelerError> toDataModelerError( List<SyntaxError> syntaxErrors, Path file ) {
        List<DataModelerError> errors = new ArrayList<DataModelerError>();
        DataModelerError error;
        for ( SyntaxError syntaxError : syntaxErrors ) {
            error = new DataModelerError( syntaxError.getDescription(),
                    syntaxError.isError() ? SystemMessage.Level.ERROR : SystemMessage.Level.WARNING,
                    Paths.convert( file ) );
            error.setColumn( syntaxError.getColumn() );
            error.setLine( syntaxError.getLine() );
            errors.add( error );
        }
        return errors;
    }

    public List<ValidationMessage> toValidationMessage( List<DataModelerError> errors ) {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        ValidationMessage validationMessage;

        if ( errors == null ) {
            return validationMessages;
        }
        for ( DataModelerError error : errors ) {
            validationMessage = new ValidationMessage();
            validationMessage.setPath( error.getFile() );
            validationMessage.setText( error.getMessage() );
            validationMessage.setColumn( error.getColumn() );
            validationMessage.setLine( error.getLine() );
            validationMessage.setId( error.getId() );
            if ( error.getLevel() != null ) {
                switch ( error.getLevel() ) {
                    case ERROR:
                        validationMessage.setLevel( ValidationMessage.Level.ERROR );
                        break;
                    case WARNING:
                        validationMessage.setLevel( ValidationMessage.Level.WARNING );
                        break;
                    case INFO:
                        validationMessage.setLevel( ValidationMessage.Level.INFO );
                        break;
                    default:
                        validationMessage.setLevel( ValidationMessage.Level.ERROR );
                }
            }
            validationMessages.add( validationMessage );
        }
        return validationMessages;
    }

    public CommentedOption makeCommentedOption( String commitMessage ) {
        final String name = identity.getIdentifier();
        final Date when = new Date();

        final CommentedOption option = new CommentedOption( sessionInfo.getId(),
                name,
                null,
                commitMessage,
                when );
        return option;
    }

    public Package ensurePackageStructure(final Project project, final String packageName) {

        if ( packageName == null || "".equals( packageName ) || project == null ) {
            return null;
        }

        Package defaultPackage = projectService.resolveDefaultPackage( project );
        Package subPackage = defaultPackage;
        Path subDirPath = Paths.convert( defaultPackage.getPackageMainSrcPath() );
        String subDirName;

        StringTokenizer tokenizer = new StringTokenizer( packageName, "." );
        while ( tokenizer.hasMoreTokens() ) {
            subDirName = tokenizer.nextToken();
            subDirPath = subDirPath.resolve( subDirName );
            if ( !ioService.exists( subDirPath ) ) {
                //create the package using the projectService.
                subPackage = projectService.newPackage( subPackage, subDirName );
            } else {
                subPackage = projectService.resolvePackage( Paths.convert( subDirPath ) );
            }
        }

        return subPackage;
    }

    public String getCanonicalFileName(org.uberfire.backend.vfs.Path path) {
        if ( path == null ) return null;
        String fileName = path.getFileName();
        return fileName.substring( 0, fileName.indexOf( "." ) );
    }

}
