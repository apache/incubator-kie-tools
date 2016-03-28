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
package org.drools.workbench.jcr2vfsmigration.jcrExport.asset;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.asseteditor.drools.factmodel.AnnotationMetaModel;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FactMetaModel;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FactModels;
import org.drools.guvnor.client.asseteditor.drools.factmodel.FieldMetaModel;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactModelExporter
        extends BaseAssetExporter
        implements AssetExporter<DataModelAsset, ExportContext> {

    private static final Logger logger = LoggerFactory.getLogger(FactModelExporter.class);

    @Inject
    private RepositoryAssetService jcrRepositoryAssetService;

    @Override
    public DataModelAsset export( ExportContext exportContext ) {

        DataModelAsset dma = new DataModelAsset( exportContext.getJcrAssetItem().getName(),
                                                 exportContext.getJcrAssetItem().getFormat(),
                                                 exportContext.getJcrAssetItem().getLastContributor(),
                                                 exportContext.getJcrAssetItem().getCheckinComment(),
                                                 exportContext.getJcrAssetItem().getLastModified().getTime() );

        // At this point the module's name is normalized already
        String normalizedPackageName = exportContext.getJcrModule().getName();

        Asset jcrAsset = null;
        try {
            jcrAsset = jcrRepositoryAssetService.loadRuleAsset( exportContext.getJcrAssetItem().getUUID() );
        } catch ( SerializationException e ) {
            logger.error( "Can't load rule asset {}!", exportContext.getJcrAssetItem().getName(), e );
            return null;
        }

        FactModels factModels = ( ( FactModels ) jcrAsset.getContent() );
        for ( FactMetaModel factMetaModel : factModels.models ) {
            DataModelAsset.DataModelObject dataModelObject = dma.addDataModelObject( factMetaModel.getName(), factMetaModel.getSuperType() );

            // Object Fields
            List<FieldMetaModel> objectFields = factMetaModel.getFields();
            for ( FieldMetaModel fieldMetaModel : objectFields ) {
                String fieldName = fieldMetaModel.name;
                String fieldType = fieldMetaModel.type;
                dataModelObject.addObjectProperty( fieldName, fieldType );
            }

            // Object Annotations
            List<AnnotationMetaModel> objectAnnotations = factMetaModel.getAnnotations();
            for ( AnnotationMetaModel annotationMetaModel : objectAnnotations ) {
                String annotationName = annotationMetaModel.name;
                Map<String, String> values = annotationMetaModel.values;

                String key = "value";
                String value = "";

                if ( values.size() > 0 ) {
                    key = values.keySet().iterator().next();
                    value = values.values().iterator().next();
                }
                dataModelObject.addObjectAnnotation( annotationName, key, value );
            }
        }
        return dma;
    }
}
