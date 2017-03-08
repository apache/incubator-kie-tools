package org.ext.uberfire.social.activities.server;

import java.lang.reflect.Field;
import javax.inject.Named;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.io.ConfigIOServiceProducer;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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

public class SocialUserPersistenceProducerTest {

    SocialUserPersistenceProducer producer;
    ConfigIOServiceProducer configIO;
    IOService ioService;
    FileSystem fileSystem;

    @Before
    public void setup() {

        producer = mock(SocialUserPersistenceProducer.class);
        ioService = mock(IOService.class);
        fileSystem = mock(FileSystem.class);
        configIO = mock(ConfigIOServiceProducer.class);

        when(producer.getConfigIOServiceProducer()).thenReturn(configIO);
        when(configIO.configIOService()).thenReturn(ioService);
        when(configIO.configFileSystem()).thenReturn(fileSystem);

        doCallRealMethod().when(producer).setup();
    }

    @Test
    public void assertRequiredWASInjections() {
        assertTrue(hasNamedField("systemFS"));
        assertTrue(hasNamedField("configIO"));
    }

    @Test
    public void setupShouldUseConfigIOServiceProducer() {

        producer.setup();
        verify(producer).setupSocialUserPersistenceAPI(eq(ioService),
                                                       any(SocialUserServicesExtendedBackEndImpl.class));
    }

    private boolean hasNamedField(String targetName) {
        final Field[] declaredFields = SocialUserPersistenceProducer.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            final Named annotation = declaredField.getAnnotation(Named.class);
            if (annotation != null) {
                if (annotation.value().equals(targetName)) {
                    return true;
                }
            }
        }
        return false;
    }
}