/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import javax.inject.Named;

import org.drools.workbench.jcr2vfsmigration.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.screens.datamodeller.model.droolsdomain.DroolsDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.StandardCopyOption;

public class FactModelImporter implements AssetImporter<DataModelAsset> {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MigrationPathManager migrationPathManager;

    @Inject
    private ProjectService<KieProject> projectService;

    @Inject
    private DataModelerService modelerService;

    private Map<String, String> orderedBaseTypes = new TreeMap<String, String>();
    private Map<String, AnnotationDefinition> annotationDefinitions;

    @Override
    public Path importAsset( Module xmlModule,
                             DataModelAsset xmlAsset,
                             Path previousVersionPath ) {

        String normalizedPackageName = xmlModule.getNormalizedPackageName();
        Path path = migrationPathManager.generatePathForAsset( xmlModule, xmlAsset, xmlAsset.getAssetType().toString() );

        //The asset was renamed in this version. We move this asset first.
        if ( previousVersionPath != null && !previousVersionPath.equals( path ) ) {
            ioService.move( Paths.convert( previousVersionPath ), Paths.convert( path ), StandardCopyOption.REPLACE_EXISTING );
        }

        KieProject project = projectService.resolveProject( path );

        initBasePropertyTypes();
        initAnnotationDefinitions();

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
                                      projectRootPath,
                                      "" );
        }

        DataModel dataModel = new DataModelImpl();

        for ( Iterator<DataModelAsset.DataModelObject> objIt = xmlAsset.modelObjects(); objIt.hasNext(); ) {
            DataModelAsset.DataModelObject obj = objIt.next();

            // Same remark for the package as above
            DataObject dataObject = new DataObjectImpl( normalizedPackageName,
                                                        obj.getName() );
            dataObject.setSuperClassName( obj.getSuperType() );

            // TODO add fileOrder to object properties, and adapt kie-wb-common accordingly so that the param order in the
            // param constructor respects the order of the properties in the generated java source file.
            for ( Iterator<DataModelAsset.DataObjectProperty> propIt = obj.properties(); propIt.hasNext(); ) {
                DataModelAsset.DataObjectProperty objProp = propIt.next();
                String fieldName = objProp.getName();
                String fieldType = objProp.getType();
                boolean isMultiple = false;
                boolean isBaseType = isBaseType( fieldType );

                ObjectProperty property = new ObjectPropertyImpl( fieldName,
                                                                  fieldType,
                                                                  isMultiple );
                property.setBaseType( isBaseType );
                dataObject.addProperty( property );
            }

            // Add annotations to data object
            for ( Iterator<DataModelAsset.DataObjectAnnotation> annIt = obj.annotations(); annIt.hasNext(); ) {
                DataModelAsset.DataObjectAnnotation objAnn = annIt.next();
                String name = objAnn.getName();
                String key = objAnn.getKey();
                String value = objAnn.getValue();
                Annotation annotation;

                if ( "Role".equals( name ) ) {
                    annotation = new AnnotationImpl( annotationDefinitions.get( DroolsDomainAnnotations.ROLE_ANNOTATION ) );
                    annotation.setValue( key, value );
                    dataObject.addAnnotation( annotation );
                }
            }

            dataModel.addDataObject( dataObject );
        }
        modelerService.saveModel( dataModel, project );

        return path;
    }

    private void initBasePropertyTypes() {
        List<PropertyType> baseTypes = modelerService.getBasePropertyTypes();
        if ( baseTypes != null ) {
            for ( PropertyType type : baseTypes ) {
                orderedBaseTypes.put( type.getName(), type.getClassName() );
            }
        }
    }

    private void initAnnotationDefinitions() {
        annotationDefinitions = modelerService.getAnnotationDefinitions();
    }

    private Boolean isBaseType( String type ) {
        return orderedBaseTypes.containsValue( type );
    }
}
