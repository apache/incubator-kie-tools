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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PersistenceDescriptorXMLMarshaller {

    public static final String PERSISTENCE = "persistence";
    public static final String VERSION = "version";
    public static final String PERSISTENCE_UNIT = "persistence-unit";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String TRANSACTION_TYPE = "transaction-type";
    public static final String DESCRIPTION = "description";
    public static final String PROVIDER = "provider";
    public static final String JTA_DATA_SOURCE = "jta-data-source";
    public static final String NON_JTA_DATA_SOURCE = "non-jta-data-source";
    public static final String MAPPING_FILE = "mapping-file";
    public static final String JAR_FILE = "jar-file";
    public static final String CLASS = "class";
    public static final String EXCLUDE_UNLISTED_CLASSES = "exclude-unlisted-classes";
    public static final String SHARED_CACHE_MODE = "shared-cache-mode";
    public static final String VALIDATION_MODE = "validation-mode";
    public static final String PROPERTIES = "properties";
    public static final String PROPERTY = "property";
    private static final Logger logger = LoggerFactory.getLogger( PersistenceDescriptorXMLMarshaller.class );
    private static Schema persistenceSchema = null;

    public static PersistenceDescriptorModel fromXML( InputStream xmlStream, boolean validate ) throws Exception {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware( true );
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse( xmlStream );
        if ( validate ) {
            Validator validator = getPersistenceSchema().newValidator();
            validator.setErrorHandler( new ErrorHandler() {
                @Override
                public void warning( SAXParseException e ) throws SAXException {
                    //TODO add fine grained error processing if needed
                    logger.warn( "PersistenceDescriptorModel parsing error: ", e );
                }

                @Override
                public void error( SAXParseException e ) throws SAXException {
                    //TODO add fine grained error processing if needed
                    logger.error( "PersistenceDescriptorModel parsing error: ", e );
                    throw e;
                }

                @Override
                public void fatalError( SAXParseException e ) throws SAXException {
                    //TODO add fine grained error processing if needed
                    logger.error( "PersistenceDescriptorModel parsing error: ", e );
                    throw e;
                }
            } );
            validator.validate( new DOMSource( document ) );
        }

        DOM2PersistenceDescriptorVisitor visitor = new DOM2PersistenceDescriptorVisitor( document );
        return visitor.visit();
    }

    public static String toXML( PersistenceDescriptorModel persistenceDescriptor ) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( 1024 );
        toXML( persistenceDescriptor, outputStream );
        return new String( outputStream.toByteArray(), "UTF-8" );
    }

    public static void toXML( PersistenceDescriptorModel persistenceDescriptor, OutputStream outputStream ) throws Exception {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty( OutputKeys.STANDALONE, "yes" );
        transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
        transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );

        PersistenceDescriptor2DOMVisitor visitor = new PersistenceDescriptor2DOMVisitor( persistenceDescriptor, document );
        visitor.visit();

        DOMSource source = new DOMSource( document );

        StreamResult result = new StreamResult( new BufferedOutputStream( outputStream ) );
        transformer.transform( source, result );
    }

    private static Schema getPersistenceSchema() throws Exception {
        if ( persistenceSchema == null ) {
            synchronized ( PersistenceDescriptorXMLMarshaller.class ) {
                SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
                URL schemaURI = PersistenceDescriptorXMLMarshaller.class.getResource( "persistence_2_0.xsd" );
                persistenceSchema = schemaFactory.newSchema( schemaURI );
            }
        }
        return persistenceSchema;
    }

}
