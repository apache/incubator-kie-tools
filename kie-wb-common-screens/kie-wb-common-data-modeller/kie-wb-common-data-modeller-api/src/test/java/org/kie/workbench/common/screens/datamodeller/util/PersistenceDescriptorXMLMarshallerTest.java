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

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.model.persistence.CachingType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistableDataObject;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.ValidationMode;

import static org.junit.Assert.*;

public class PersistenceDescriptorXMLMarshallerTest {

    private InputStream getInputStream( String file ) {
        return PersistenceDescriptorXMLMarshallerTest.class.getResourceAsStream( file );
    }

    @Test
    public void readFromXMLTest() {
        try {
            PersistenceDescriptorModel descriptor = PersistenceDescriptorXMLMarshaller.fromXML( getInputStream( "persistence.xml" ), false );
            assertEqualsDescriptor( expectedDescriptor(), descriptor );
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }

    @Ignore
    public void writeToXMLTest() {

        try {
            PersistenceDescriptorModel descriptor = expectedDescriptor();
            String xml = PersistenceDescriptorXMLMarshaller.toXML( descriptor );

            StringBuilder expectedXml = new StringBuilder( );

            InputStream in = getExpectedXML();
            byte buffer[] = new byte[1024];

            int size;

            while ( ( size = in.read( buffer ) ) != -1 ) {
                expectedXml.append( new String( buffer, 0, size, "UTF-8" ) );
            }

            assertEquals( expectedXml.toString(), xml );

        } catch ( Exception e ) {
            Assert.fail( e.getMessage() );
        }
    }

    private void assertEqualsDescriptor( PersistenceDescriptorModel expectedDescriptor, PersistenceDescriptorModel descriptor ) {
        assertEquals( expectedDescriptor.getVersion(), descriptor.getVersion() );
        assertEqualsPersistenceUnit( expectedDescriptor.getPersistenceUnit(), descriptor.getPersistenceUnit() );
    }

    private void assertEqualsPersistenceUnit( PersistenceUnitModel expectedPersistenceUnit, PersistenceUnitModel persistenceUnit ) {
        assertEquals( expectedPersistenceUnit.getName(), persistenceUnit.getName() );
        assertEquals( expectedPersistenceUnit.getDescription(), persistenceUnit.getDescription() );
        assertEquals( expectedPersistenceUnit.getProvider(), persistenceUnit.getProvider() );
        assertEquals( expectedPersistenceUnit.getJtaDataSource(), persistenceUnit.getJtaDataSource() );
        assertEquals( expectedPersistenceUnit.getNonJtaDataSource(), persistenceUnit.getNonJtaDataSource() );
        assertEquals( expectedPersistenceUnit.getExcludeUnlistedClasses(), persistenceUnit.getExcludeUnlistedClasses() );
        assertEquals( expectedPersistenceUnit.getClasses(), persistenceUnit.getClasses() );
        assertEquals( expectedPersistenceUnit.getJarFile(), persistenceUnit.getJarFile() );
        assertEquals( expectedPersistenceUnit.getMappingFile(), persistenceUnit.getMappingFile() );
        assertEquals( expectedPersistenceUnit.getSharedCacheMode(), persistenceUnit.getSharedCacheMode() );
        assertEquals( expectedPersistenceUnit.getTransactionType(), persistenceUnit.getTransactionType() );
        assertEquals( expectedPersistenceUnit.getProperties(), persistenceUnit.getProperties() );
    }

    private PersistenceDescriptorModel expectedDescriptor() {
        PersistenceDescriptorModel descriptor = new PersistenceDescriptorModel();
        descriptor.setVersion( "2.1" );

        PersistenceUnitModel persistenceUnit = new PersistenceUnitModel();

        descriptor.setPersistenceUnit( persistenceUnit );

        persistenceUnit.setName( "org.test.persistence-unit" );
        persistenceUnit.setDescription( "org.test.description" );
        persistenceUnit.setProvider( "org.test.Provider" );
        persistenceUnit.setJtaDataSource( "java:jboss/datasources/ExampleDS" );
        persistenceUnit.getMappingFile().add( "META-INF/Mapping1.xml" );
        persistenceUnit.getMappingFile().add( "META-INF/Mapping2.xml" );
        persistenceUnit.getJarFile().add( "file1.jar" );
        persistenceUnit.getJarFile().add( "file2.jar" );
        persistenceUnit.getClasses().add(new PersistableDataObject("org.test.Entity1") );
        persistenceUnit.getClasses().add( new PersistableDataObject("org.test.Entity2") );
        persistenceUnit.setExcludeUnlistedClasses( true );
        persistenceUnit.setSharedCacheMode( CachingType.ALL );
        persistenceUnit.setValidationMode( ValidationMode.AUTO );
        persistenceUnit.setTransactionType( TransactionType.JTA );

        persistenceUnit.addProperty( new Property( "property1", "property1_value" ) );
        persistenceUnit.addProperty( new Property( "property2", "property2_value" ) );


        return descriptor;
    }

    private InputStream getExpectedXML() {
        return getInputStream( "generated-persistence.xml" );
    }

}
