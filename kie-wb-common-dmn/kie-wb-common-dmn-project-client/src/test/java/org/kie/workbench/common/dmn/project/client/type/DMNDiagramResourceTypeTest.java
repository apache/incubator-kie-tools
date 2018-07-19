/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.project.client.type;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.categories.Decision;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.resource.DMNDefinitionSetResourceType;
import org.kie.workbench.common.dmn.project.client.resources.i18n.DMNProjectClientConstants;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramResourceTypeTest {

    private DMNDefinitionSetResourceType definitionSetResourceType;

    @Mock

    private TranslationService translationService;

    private DMNDiagramResourceType resourceType;

    @Before
    public void setup() {
        final Decision category = new Decision();
        this.definitionSetResourceType = new DMNDefinitionSetResourceType(category);
        this.resourceType = new DMNDiagramResourceType(definitionSetResourceType,
                                                       category,
                                                       translationService);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(anyString());
    }

    @Test
    public void testCategory() {
        assertTrue(resourceType.getCategory() instanceof Decision);
    }

    @Test
    public void testTranslatedShortName() {
        assertEquals(DMNProjectClientConstants.DMNDiagramResourceType,
                     resourceType.getTranslatedShortName());
    }

    @Test
    public void testTranslatedDescription() {
        assertEquals(DMNProjectClientConstants.DMNDiagramResourceTypeDescription,
                     resourceType.getTranslatedDescription());
    }
}
