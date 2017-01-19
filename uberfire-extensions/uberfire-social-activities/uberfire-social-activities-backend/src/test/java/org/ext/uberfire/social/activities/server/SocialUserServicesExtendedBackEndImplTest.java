/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.ext.uberfire.social.activities.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.ConfigIOServiceProducer;
import org.uberfire.java.nio.file.FileSystem;

import javax.enterprise.context.*;
import java.lang.annotation.Annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class SocialUserServicesExtendedBackEndImplTest {

    SocialUserServicesExtendedBackEndImpl socialUserServicesExtendedBackEnd;
    FileSystem filesystemConstructor;
    FileSystem fileSystemProducer;
    final ConfigIOServiceProducer configIOServiceProducer = mock( ConfigIOServiceProducer.class );

    @Before
    public void setup() {
        filesystemConstructor = mock( FileSystem.class );
        fileSystemProducer = mock( FileSystem.class );
        socialUserServicesExtendedBackEnd = new SocialUserServicesExtendedBackEndImpl( filesystemConstructor ) {
            @Override
            ConfigIOServiceProducer getConfigIOServiceProducer() {

                return configIOServiceProducer;
            }
        };
    }

    @Test
    public void cannotBeAManagedBeanBecauseOfWas() {
        assertNull( getAnnotation( ApplicationScoped.class ) );
        assertNull( getAnnotation( Dependent.class ) );
        assertNull( getAnnotation( SessionScoped.class ) );
        assertNull( getAnnotation( RequestScoped.class ) );
        assertNull( getAnnotation( ConversationScoped.class ) );
    }

    @Test
    public void getFileSystemTest() {

        when( configIOServiceProducer.configFileSystem() ).thenReturn( fileSystemProducer );
        assertEquals( fileSystemProducer, socialUserServicesExtendedBackEnd.getFileSystem() );

        when( configIOServiceProducer.configFileSystem() ).thenReturn( null );
        assertEquals( filesystemConstructor, socialUserServicesExtendedBackEnd.getFileSystem() );

    }


    private Annotation getAnnotation( Class<? extends Annotation> annotation ) {
        return SocialUserServicesExtendedBackEndImpl.class.getAnnotation( annotation );
    }
}