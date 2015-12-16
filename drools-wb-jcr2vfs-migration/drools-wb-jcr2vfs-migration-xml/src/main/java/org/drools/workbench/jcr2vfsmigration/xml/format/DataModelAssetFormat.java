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
package org.drools.workbench.jcr2vfsmigration.xml.format;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.drools.workbench.jcr2vfsmigration.xml.ExportXmlUtils.*;

public class DataModelAssetFormat extends XmlAssetFormat {

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

    protected String doFormat( DataModelAsset dataModelAsset ) {
        StringBuilder sb = new StringBuilder();

        for ( Iterator<DataModelAsset.DataModelObject> objectIt = dataModelAsset.modelObjects(); objectIt.hasNext(); ) {
            DataModelAsset.DataModelObject obj = objectIt.next();
            String objSuperType = StringUtils.isNotBlank( obj.getSuperType() ) ? obj.getSuperType() : "";
            sb.append( LT ).append( MODEL_OBJ )
                .append( " " ).append( MODEL_OBJ_NAME ).append( "=\"" ).append( obj.getName() ).append( "\"" )
                .append( " " ).append( MODEL_OBJ_SUPERTYPE ).append( "=\"" ).append( objSuperType ).append( "\"" )
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
                        .append( " " ).append( MODEL_OBJ_ANN_KEY ).append( "=\"" ).append( escapeXml( ann.getKey() ) ).append( "\"" )
                        .append( " " ).append( MODEL_OBJ_ANN_VALUE ).append( "=\"" ).append( escapeXml( ann.getValue() ) ).append( "\"" )
                        .append( SLASH_GT );
            }

            sb.append( LT_SLASH ).append( MODEL_OBJ ).append( GT );
        }
        return sb.toString();
    }

    protected DataModelAsset doParse( String name,
                                      String format,
                                      String lastContributor,
                                      String checkinComment,
                                      Date lastModified,
                                      Node assetNode ) {

        DataModelAsset dataModel = new DataModelAsset( name, format, lastContributor, checkinComment, lastModified );

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
                                                 unEscapeXml( childNodeAttribs.getNamedItem( MODEL_OBJ_ANN_KEY ).getNodeValue() ),
                                                 unEscapeXml( childNodeAttribs.getNamedItem( MODEL_OBJ_ANN_VALUE ).getNodeValue() ) );
                    }
                }
            }
        }
        return dataModel;
    }
}
