/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.screens.datamodeller.model.persistence.CachingType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistableDataObject;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.ValidationMode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DOM2PersistenceDescriptorVisitor {

    PersistenceDescriptorModel descriptorModel;

    Document document;

    public DOM2PersistenceDescriptorVisitor( Document document ) {
        this.document = document;
    }

    public PersistenceDescriptorModel visit() {
        visitPersistence( document.getDocumentElement() );
        return descriptorModel;
    }

    private void visitPersistence( Element element ) {
        descriptorModel = new PersistenceDescriptorModel();
        descriptorModel.setVersion( element.getAttribute( PersistenceDescriptorXMLMarshaller.VERSION ) );

        NodeList children = element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.PERSISTENCE_UNIT );
        if ( children != null && children.getLength() > 0 ) {
            for ( int i = 0; i < children.getLength(); i++ ) {
                visitPersistenceUnit( (Element) children.item( i ) );
            }
        }
    }

    private void visitPersistenceUnit( Element element ) {
        PersistenceUnitModel persistenceUnit = new PersistenceUnitModel();
        descriptorModel.setPersistenceUnit( persistenceUnit );

        persistenceUnit.setName( element.getAttribute( PersistenceDescriptorXMLMarshaller.NAME ) );
        String transactionType = element.getAttribute( PersistenceDescriptorXMLMarshaller.TRANSACTION_TYPE );
        persistenceUnit.setTransactionType( parseTransactionType( transactionType ) );

        visitDescription( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.DESCRIPTION ) );
        visitProvider( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.PROVIDER ) );
        visitJTADataSource( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.JTA_DATA_SOURCE ) );
        visitNonJTADataSource( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.NON_JTA_DATA_SOURCE ) );
        visitMappingFile( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.MAPPING_FILE ) );
        visitJarFile( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.JAR_FILE ) );
        visitClass( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.CLASS ) );
        visitExcludeUnlistedClasses( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.EXCLUDE_UNLISTED_CLASSES ) );
        visitSharedCacheMode( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.SHARED_CACHE_MODE ) );
        visitValidationMode( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.VALIDATION_MODE ) );
        visitProperties( persistenceUnit, element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.PROPERTIES ) );
    }

    private void visitDescription( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        persistenceUnit.setDescription( parseSimpleTextElement( nodes ) );
    }

    private void visitProvider( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        persistenceUnit.setProvider( parseSimpleTextElement( nodes ) );
    }

    private void visitJTADataSource( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        persistenceUnit.setJtaDataSource( parseSimpleTextElement( nodes ) );
    }

    private void visitNonJTADataSource( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        persistenceUnit.setNonJtaDataSource( parseSimpleTextElement( nodes ) );
    }

    private void visitMappingFile( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        persistenceUnit.setMappingFile( parseSimpleTextElementList( nodes ) );
    }

    private void visitJarFile( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        persistenceUnit.setJarFile( parseSimpleTextElementList( nodes ) );
    }

    private void visitClass(PersistenceUnitModel persistenceUnit, NodeList nodes) {
        persistenceUnit.setClasses(parseSimpleTextElementList(nodes).stream().map(p -> new PersistableDataObject(p)).collect(Collectors.toList()));
    }

    private void visitExcludeUnlistedClasses( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        persistenceUnit.setExcludeUnlistedClasses( Boolean.parseBoolean( parseSimpleTextElement( nodes ) ) );
    }

    private void visitSharedCacheMode( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        String value = parseSimpleTextElement( nodes );
        persistenceUnit.setSharedCacheMode( value != null ? CachingType.valueOf( value ) : null );
    }

    private void visitValidationMode( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        String value = parseSimpleTextElement( nodes );
        persistenceUnit.setValidationMode( value != null ? ValidationMode.valueOf( value ) : null );
    }

    private void visitProperties( PersistenceUnitModel persistenceUnit, NodeList nodes ) {
        List<Property> properties = new ArrayList<Property>( );
        persistenceUnit.setProperties( properties );
        if ( nodes != null && nodes.getLength() > 0 ) {
            Element element = (Element) nodes.item( 0 );
            NodeList propertyNodes = element.getElementsByTagName( PersistenceDescriptorXMLMarshaller.PROPERTY );
            if ( propertyNodes != null && propertyNodes.getLength() > 0 ) {
                for ( int i = 0; i < propertyNodes.getLength(); i++ ) {
                    visitProperty( properties, ( Element ) propertyNodes.item( i) );
                }
            }
        }
    }

    private void visitProperty( List<Property> properties, Element item ) {
        Property property = new Property( item.getAttribute( PersistenceDescriptorXMLMarshaller.NAME ), item.getAttribute( PersistenceDescriptorXMLMarshaller.VALUE ) );
        properties.add( property );
    }

    private List<String> parseSimpleTextElementList( NodeList nodes ) {
        List<String> result = new ArrayList<String>(  );
        if ( nodes != null && nodes.getLength() > 0 ) {
            Element element;
            for ( int i = 0; i < nodes.getLength(); i++ ) {
                element = (Element) nodes.item( i );
                result.add( element.getTextContent() );

            }
        }
        return result;
    }

    private String parseSimpleTextElement( NodeList nodes ) {
        String result = null;
        if ( nodes != null && nodes.getLength() > 0 ) {
            Element element = (Element) nodes.item( 0 );
            result = element.getTextContent();
        }
        return result;
    }

    private TransactionType parseTransactionType( String value ) {
        if ( value != null ) {
            try {
                return TransactionType.valueOf( value.trim() );
            } catch ( Exception e ) {
                //invalid values will be interpreted as if the transaction type is not set
                //this will le the user the chance to set a valid value in the UI
            }
        }
        return null;
    }

}
