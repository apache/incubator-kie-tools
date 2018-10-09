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

import java.util.List;

import org.kie.workbench.common.screens.datamodeller.model.persistence.CachingType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistableDataObject;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.model.persistence.ValidationMode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PersistenceDescriptor2DOMVisitor {

    Document document;

    PersistenceDescriptorModel descriptorModel;

    public PersistenceDescriptor2DOMVisitor( PersistenceDescriptorModel descriptorModel, Document document ) {
        this.descriptorModel = descriptorModel;
        this.document = document;
    }

    public Document visit() {

        Element element = document.createElement( PersistenceDescriptorXMLMarshaller.PERSISTENCE );

        element.setAttribute( PersistenceDescriptorXMLMarshaller.VERSION, descriptorModel.getVersion() );
        element.setAttribute( "xmlns", "http://java.sun.com/xml/ns/persistence" );
        element.setAttribute( "xmlns:orm", "http://java.sun.com/xml/ns/persistence/orm" );
        element.setAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
        element.setAttribute( "xsi:schemaLocation", "http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm_2_0.xsd" );


                document.appendChild( element );

        if ( descriptorModel.getPersistenceUnit() != null ) {
            visitPersistenceUnit( element, descriptorModel.getPersistenceUnit() );
        }

        return document;
    }

    private void visitPersistenceUnit( Element parent, PersistenceUnitModel persistenceUnit ) {

        Element persistenceUnitElement = document.createElement( PersistenceDescriptorXMLMarshaller.PERSISTENCE_UNIT );
        parent.appendChild( persistenceUnitElement );

        persistenceUnitElement.setAttribute( PersistenceDescriptorXMLMarshaller.NAME, persistenceUnit.getName() );
        if ( persistenceUnit.getTransactionType() != null ) {
            persistenceUnitElement.setAttribute( PersistenceDescriptorXMLMarshaller.TRANSACTION_TYPE, persistenceUnit.getTransactionType().name() );
        }

        visitDescription( persistenceUnitElement, persistenceUnit.getDescription() );
        visitProvider( persistenceUnitElement, persistenceUnit.getProvider() );
        visitJTADataSource( persistenceUnitElement, persistenceUnit.getJtaDataSource() );
        visitNonJTADataSource( persistenceUnitElement, persistenceUnit.getNonJtaDataSource() );

        if ( persistenceUnit.getMappingFile() != null ) {
            for ( String mappingFile : persistenceUnit.getMappingFile() ) {
                visitMappingFile( persistenceUnitElement, mappingFile );
            }
        }

        if ( persistenceUnit.getJarFile() != null ) {
            for ( String jarFile : persistenceUnit.getJarFile() ) {
                visitJarFile( persistenceUnitElement, jarFile );
            }
        }

        if ( persistenceUnit.getClasses() != null ) {
            for ( PersistableDataObject clazz : persistenceUnit.getClasses() ) {
                visitClass( persistenceUnitElement, clazz.getValue() );
            }
        }

        visitExcludeUnlistedClasses( persistenceUnitElement, persistenceUnit.getExcludeUnlistedClasses() );
        visitSharedChacheMode( persistenceUnitElement, persistenceUnit.getSharedCacheMode() );
        visitValidationMode( persistenceUnitElement, persistenceUnit.getValidationMode() );
        visitProperties( persistenceUnitElement, persistenceUnit.getProperties() );
    }

    private void visitDescription( Element element, String description ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.DESCRIPTION, description, false );
    }

    private void visitProvider( Element element, String provider ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.PROVIDER, provider, true );
    }

    private void visitJTADataSource( Element element, String jtaDataSource ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.JTA_DATA_SOURCE, jtaDataSource, false );
    }

    private void visitNonJTADataSource( Element element, String nonJtaDataSource ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.NON_JTA_DATA_SOURCE, nonJtaDataSource, false );
    }

    private void visitJarFile( Element element, String jarFile ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.JAR_FILE, jarFile, false );
    }

    private void visitClass( Element element, String clazz ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.CLASS, clazz, false );
    }

    private void visitExcludeUnlistedClasses( Element element, Boolean excludeUnlistedClasses ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.EXCLUDE_UNLISTED_CLASSES, excludeUnlistedClasses != null ? excludeUnlistedClasses.toString(): null , false );
    }

    private void visitSharedChacheMode( Element element, CachingType sharedCacheMode ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.SHARED_CACHE_MODE, sharedCacheMode != null ? sharedCacheMode.name() : null, false );
    }

    private void visitValidationMode( Element element, ValidationMode validationMode ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.VALIDATION_MODE, validationMode != null ? validationMode.name() : null, false );
    }

    private void visitProperties( Element element, List<Property> properties ) {
        if ( properties != null && properties.size() > 0 ) {
            Element propertiesElement = document.createElement( PersistenceDescriptorXMLMarshaller.PROPERTIES );
            element.appendChild( propertiesElement );
            for ( Property property : properties ) {
                visitProperty( propertiesElement, property );
            }
        }
    }

    private void visitProperty( Element element, Property property ) {
        Element propertyElement = document.createElement( PersistenceDescriptorXMLMarshaller.PROPERTY );
        element.appendChild( propertyElement );
        propertyElement.setAttribute( PersistenceDescriptorXMLMarshaller.NAME, property.getName() );
        propertyElement.setAttribute( PersistenceDescriptorXMLMarshaller.VALUE, property.getValue() );
    }

    void createSimpleTextElement( Element element, String elementName, String elementValue, boolean includeIfNull ) {
        if ( elementValue != null || includeIfNull ) {
            Element childElement = document.createElement( elementName );
            if ( elementValue != null ) {
                childElement.setTextContent( elementValue );
            }
            element.appendChild( childElement );
        }
    }

    private void visitMappingFile( Element element, String mappingFile ) {
        createSimpleTextElement( element, PersistenceDescriptorXMLMarshaller.MAPPING_FILE, mappingFile, false );
    }

}
