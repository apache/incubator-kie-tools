/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.common;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationDRDsFactory;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.mockito.Mock;
import org.uberfire.rpc.SessionInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNDocumentationFactoryTest {

    @Mock
    private CanvasFileExport canvasFileExport;

    @Mock
    private TranslationService translationService;

    @Mock
    private DMNDocumentationDRDsFactory drdsFactory;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private DMNGraphUtils graphUtils;

    private DMNDocumentationFactory factory;

    @Before
    public void setup() {
        this.factory = new DMNDocumentationFactory(canvasFileExport,
                                                   translationService,
                                                   drdsFactory,
                                                   sessionInfo,
                                                   graphUtils);
    }

    @Test
    public void testGetCurrentUserName() {
        assertThat(factory.getCurrentUserName()).isNull();
    }
}
