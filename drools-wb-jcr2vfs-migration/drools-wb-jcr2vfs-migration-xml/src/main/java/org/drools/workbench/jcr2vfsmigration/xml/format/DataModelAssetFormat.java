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
package org.drools.workbench.jcr2vfsmigration.xml.format;

import java.util.Iterator;

import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.format.XmlAssetFormat.*;

public class DataModelAssetFormat implements XmlFormat<DataModelAsset> {

    private static final String MODEL_OBJ = "modelObject";
    private static final String MODEL_OBJ_NAME = "objName";
    private static final String MODEL_OBJ_SUPERTYPE = "objSuperType";

    private static final String MODEL_OBJ_PROP = "objProperty";
    private static final String MODEL_OBJ_PROP_NAME = "objPropName";
    private static final String MODEL_OBJ_PROP_TYPE = "objPropType";

    private static final String MODEL_OBJ_ANN = "objAnnotation";
    private static final String MODEL_OBJ_ANN_NAME = "objAnnName";
    private static final String MODEL_OBJ_ANN_KEY = "objAnnKey";
    private static final String MODEL_OBJ_ANN_VALUE = "objAnnValue";

    @Override
    public void format( StringBuilder sb, DataModelAsset dataModelAsset ) {
        if ( sb == null || dataModelAsset == null ) throw new IllegalArgumentException( "No output or data model asset asset specified" );

        sb.append( LT ).append( ASSET )
            .append( " " ).append( ASSET_NAME ).append( "=\"" ).append( dataModelAsset.getName() ).append( "\"" )
            .append( " " ).append( ASSET_TYPE ).append( "=\"" ).append( dataModelAsset.getAssetType().toString() ).append( "\"" )
            .append( GT );

        for ( Iterator<DataModelAsset.DataModelObject> objectIt = dataModelAsset.modelObjects(); objectIt.hasNext(); ) {
            DataModelAsset.DataModelObject obj = objectIt.next();
            sb.append( LT ).append( MODEL_OBJ )
                .append( " " ).append( MODEL_OBJ_NAME ).append( "=\"" ).append( obj.getName() ).append( "\"" )
                .append( " " ).append( MODEL_OBJ_SUPERTYPE ).append( "=\"" ).append( obj.getSuperType() ).append( "\"" )
                .append( GT );

            for ( Iterator<DataModelAsset.DataObjectProperty> propIt = obj.properties(); propIt.hasNext(); ) {
                DataModelAsset.DataObjectProperty prop = propIt.next();
                sb.append( LT ).append( MODEL_OBJ_PROP )
                    .append( " " ).append( MODEL_OBJ_PROP_NAME ).append( "=\"" ).append( prop.getName() ).append( "\"" )
                    .append( " " ).append( MODEL_OBJ_PROP_TYPE ).append( "=\"" ).append( prop.getType() ).append( "\"" )
                    .append( SLASH_GT );
            }

            for ( Iterator<DataModelAsset.DataObjectAnnotation> annIt = obj.annotations(); annIt.hasNext(); ) {
                DataModelAsset.DataObjectAnnotation ann = annIt.next();
                sb.append( LT ).append( MODEL_OBJ_ANN )
                        .append( " " ).append( MODEL_OBJ_ANN_NAME ).append( "=\"" ).append( ann.getName() ).append( "\"" )
                        .append( " " ).append( MODEL_OBJ_ANN_KEY ).append( "=\"" ).append( ann.getKey() ).append( "\"" )
                        .append( " " ).append( MODEL_OBJ_ANN_VALUE ).append( "=\"" ).append( ann.getValue() ).append( "\"" )
                        .append( SLASH_GT );
            }

            sb.append( LT_SLASH ).append( MODEL_OBJ ).append( GT );
        }

        sb.append( LT_SLASH ).append( ASSET ).append( GT );
    }

    @Override
    public DataModelAsset parse( Node assetNode ) {
        // Null-ness already checked before
        NamedNodeMap assetAttribs = assetNode.getAttributes();
        String name = assetAttribs.getNamedItem( ASSET_NAME ).getNodeValue();
        String assetType = assetAttribs.getNamedItem( ASSET_TYPE ).getNodeValue();

        DataModelAsset dataModel = new DataModelAsset( name, assetType );

        NodeList modelNodeList = assetNode.getChildNodes();
        for ( int i = 0; i < modelNodeList.getLength(); i++ ) {
            Node objNode = modelNodeList.item( i );
            if ( MODEL_OBJ.equalsIgnoreCase( objNode.getNodeName() ) ) {
                NamedNodeMap objAttribs = objNode.getAttributes();
                String objName = objAttribs.getNamedItem( MODEL_OBJ_NAME ).getNodeValue();
                String objSuperType = objAttribs.getNamedItem( MODEL_OBJ_SUPERTYPE ).getNodeValue();

                DataModelAsset.DataModelObject obj = dataModel.addDataModelObject( objName, objSuperType );

                NodeList objNodeList = objNode.getChildNodes();
                for ( int j = 0; j < objNodeList.getLength(); j++ ) {
                    Node objChildNode = objNodeList.item( j );
                    NamedNodeMap childNodeAttribs = objChildNode.getAttributes();

                    if ( MODEL_OBJ_PROP.equalsIgnoreCase( objChildNode.getNodeName() ) ) {
                        obj.addObjectProperty( childNodeAttribs.getNamedItem( MODEL_OBJ_PROP_NAME).getNodeValue(),
                                               childNodeAttribs.getNamedItem( MODEL_OBJ_PROP_TYPE ).getNodeValue() );

                    } else if ( MODEL_OBJ_ANN.equalsIgnoreCase( objChildNode.getNodeName() ) ) {
                        obj.addObjectAnnotation( childNodeAttribs.getNamedItem( MODEL_OBJ_ANN_NAME).getNodeValue(),
                                                 childNodeAttribs.getNamedItem( MODEL_OBJ_ANN_KEY ).getNodeValue(),
                                                 childNodeAttribs.getNamedItem( MODEL_OBJ_ANN_VALUE ).getNodeValue() );
                    }
                }
            }
        }
        return dataModel;
    }
}
