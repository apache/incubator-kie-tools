/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.jcr2vfsmigration.vfsImport.asset;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;

import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationMemberDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationMemberDefinition;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.PositionAnnotationDefinition;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.vfs.Path;

public class FactModelImporter implements AssetImporter<DataModelAsset> {

    @Inject
    private MigrationPathManager migrationPathManager;

    @Inject
    private ProjectService<KieProject> projectService;

    @Inject
    private DataModelerService modelerService;

    private Map<String, String> orderedBaseTypes = new TreeMap<String, String>();
    private Map<String, AnnotationDefinitionTO> annotationDefinitions;

    @Override
    public void importAsset( Module xmlModule, DataModelAsset xmlAsset ) {

        String normalizedPackageName = xmlModule.getNormalizedPackageName();
        Path path = migrationPathManager.generatePathForAsset( xmlModule, xmlAsset );
        KieProject project = projectService.resolveProject( path );

        initBasePropertyTypes();
        initAnnotationDefinitions();
        AnnotationDefinitionTO positionAnnotationDef = getPositionAnnotationDefinition();

        if ( project == null ) {
            // formerly jcrModule.getName(), but when arriving at this point the jcrModule name has been replaced with
            // the normalized package name.
            Path projectRootPath = migrationPathManager.generatePathForModule( normalizedPackageName );
            //Quick hack to pass mock values for pomPath etc, to make Project constructor happy. We only use projectRootPath anyway
            project = new KieProject( projectRootPath,
                                      projectRootPath,
                                      projectRootPath,
                                      projectRootPath,
                                      projectRootPath,
                                      "" );
        }

        DataModelTO dataModelTO = new DataModelTO();

        for ( Iterator<DataModelAsset.DataModelObject> objIt = xmlAsset.modelObjects(); objIt.hasNext(); ) {
            DataModelAsset.DataModelObject obj = objIt.next();

            // Same remark for the package as above
            DataObjectTO dataObject = new DataObjectTO( obj.getName(),
                                                        normalizedPackageName,
                                                        obj.getSuperType() );

            // Add fields to data object
            int position = 0;
            for ( Iterator<DataModelAsset.DataObjectProperty> propIt = obj.properties(); propIt.hasNext(); ) {
                DataModelAsset.DataObjectProperty objProp = propIt.next();
                String fieldName = objProp.getName();
                String fieldType = objProp.getType();
                boolean isMultiple = false;
                boolean isBaseType = isBaseType( fieldType );

                ObjectPropertyTO property = new ObjectPropertyTO( fieldName,
                                                                  fieldType,
                                                                  isMultiple,
                                                                  isBaseType );
                property.addAnnotation( positionAnnotationDef, AnnotationDefinitionTO.VALUE_PARAM, position + "" );
                position++;

                dataObject.getProperties().add( property );
            }

            // Add annotations to data object
            for ( Iterator<DataModelAsset.DataObjectAnnotation> annIt = obj.annotations(); annIt.hasNext(); ) {
                DataModelAsset.DataObjectAnnotation objAnn = annIt.next();
                String name = objAnn.getName();
                String key = objAnn.getKey();
                String value = objAnn.getValue();

                if ( "Role".equals( name ) ) {
                    dataObject.addAnnotation( annotationDefinitions.get( AnnotationDefinitionTO.ROLE_ANNOTATION ), key, value );
                } else if ( "Position".equals( name ) ) {
                    dataObject.addAnnotation( annotationDefinitions.get( AnnotationDefinitionTO.POSITION_ANNOTATION ), key, value );
                } else if ( "Equals".equals( name ) ) {
                    dataObject.addAnnotation( annotationDefinitions.get( AnnotationDefinitionTO.KEY_ANNOTATION ), key, value );
                }
            }

            dataModelTO.getDataObjects().add( dataObject );
        }

        modelerService.saveModel( dataModelTO, project );
    }

    private void initBasePropertyTypes() {
        List<PropertyTypeTO> baseTypes = modelerService.getBasePropertyTypes();
        if ( baseTypes != null ) {
            for ( PropertyTypeTO type : baseTypes ) {
                orderedBaseTypes.put( type.getName(), type.getClassName() );
            }
        }
    }

    private void initAnnotationDefinitions() {
        annotationDefinitions = modelerService.getAnnotationDefinitions();
    }

    private AnnotationDefinitionTO getPositionAnnotationDefinition() {
        AnnotationDefinition positionAnnotationDef = PositionAnnotationDefinition.getInstance();
        AnnotationDefinitionTO positionAnnotationDefTO = new AnnotationDefinitionTO( positionAnnotationDef.getName(), positionAnnotationDef.getClassName(), positionAnnotationDef.getShortDescription(), positionAnnotationDef.getDescription(), positionAnnotationDef.isObjectAnnotation(), positionAnnotationDef.isPropertyAnnotation() );
        AnnotationMemberDefinitionTO memberDefinitionTO;
        for ( AnnotationMemberDefinition memberDefinition : positionAnnotationDef.getAnnotationMembers() ) {
            memberDefinitionTO = new AnnotationMemberDefinitionTO( memberDefinition.getName(), memberDefinition.getClassName(), memberDefinition.isPrimitiveType(), memberDefinition.isEnum(), memberDefinition.defaultValue(), memberDefinition.getShortDescription(), memberDefinition.getDescription() );
            positionAnnotationDefTO.addMember( memberDefinitionTO );
        }
        return positionAnnotationDefTO;
    }

    private Boolean isBaseType( String type ) {
        return orderedBaseTypes.containsValue( type );
    }
}
