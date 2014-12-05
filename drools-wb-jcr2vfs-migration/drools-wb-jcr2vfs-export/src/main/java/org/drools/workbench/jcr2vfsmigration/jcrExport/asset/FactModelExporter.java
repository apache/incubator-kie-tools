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
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;

public class FactModelExporter implements AssetExporter<DataModelAsset> {

    @Inject
    private RepositoryAssetService jcrRepositoryAssetService;

    @Override
    public DataModelAsset export( Module jcrModule, AssetItem jcrAssetItem ) {

        DataModelAsset dma = new DataModelAsset( jcrAssetItem.getName(), jcrAssetItem.getFormat() );

        // At this point the module's name is normalized already
        String normalizedPackageName = jcrModule.getName();

        Asset jcrAsset = null;
        try {
            jcrAsset = jcrRepositoryAssetService.loadRuleAsset( jcrAssetItem.getUUID() );
        } catch ( SerializationException e ) {
            System.out.println( "Error: " + e.getMessage() );
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
