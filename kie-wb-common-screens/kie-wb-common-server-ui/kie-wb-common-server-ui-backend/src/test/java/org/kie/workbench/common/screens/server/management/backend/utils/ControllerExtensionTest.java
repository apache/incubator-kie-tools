/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.utils;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.server.management.backend.KieServerEmbeddedControllerProducer;
import org.kie.workbench.common.screens.server.management.backend.KieServerStandaloneControllerProducer;
import org.kie.workbench.common.screens.server.management.utils.ControllerUtils;

import static org.kie.workbench.common.screens.server.management.utils.ControllerUtils.KIE_SERVER_CONTROLLER;
import static org.mockito.Mockito.*;

public class ControllerExtensionTest {

    private ControllerExtension extension = new ControllerExtension();

    @Before
    @After
    public void clear() {
        System.clearProperty(KIE_SERVER_CONTROLLER);
    }

    @Test
    public void testEmbeddedAnnotationWithEmbeddedController() {
        final ProcessAnnotatedType annotatedType = createAnnotatedType(KieServerEmbeddedControllerProducer.class);

        extension.processEmbeddedController(annotatedType);

        verify(annotatedType,
               never()).veto();
    }

    @Test
    public void testEmbeddedAnnotationWithStandaloneController() {
        System.setProperty(ControllerUtils.KIE_SERVER_CONTROLLER,
                           "http://localhost:8080/controller");

        final ProcessAnnotatedType annotatedType = createAnnotatedType(KieServerEmbeddedControllerProducer.class);

        extension.processEmbeddedController(annotatedType);

        verify(annotatedType).veto();
    }

    @Test
    public void testStandaloneAnnotationWithEmbeddedController() {
        final ProcessAnnotatedType annotatedType = createAnnotatedType(KieServerStandaloneControllerProducer.class);

        extension.processStandaloneController(annotatedType);

        verify(annotatedType).veto();
    }

    @Test
    public void testStandaloneAnnotationWithStandaloneController() {
        System.setProperty(ControllerUtils.KIE_SERVER_CONTROLLER,
                           "http://localhost:8080/controller");

        final ProcessAnnotatedType annotatedType = createAnnotatedType(KieServerStandaloneControllerProducer.class);

        extension.processStandaloneController(annotatedType);

        verify(annotatedType,
               never()).veto();
    }

    protected ProcessAnnotatedType createAnnotatedType(final Class aClass) {
        final ProcessAnnotatedType processAnnotatedType = mock(ProcessAnnotatedType.class);
        final AnnotatedType annotatedType = mock(AnnotatedType.class);

        when(processAnnotatedType.getAnnotatedType()).thenReturn(annotatedType);
        when(annotatedType.getJavaClass()).thenReturn(aClass);

        return processAnnotatedType;
    }
}
