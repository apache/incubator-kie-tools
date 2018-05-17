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

package org.kie.workbench.common.forms.editor.backend.service.impl.helpers;

import org.assertj.core.api.Assertions;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.io.IOService;

import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class FormDefinitionCopyHelperTest extends AbstractFormDefinitionHelperTest<FormDefinitionCopyHelper> {

    private String originalId;

    @Override
    protected FormDefinitionCopyHelper getHelper(IOService ioService, FormDefinitionSerializer serializer, CommentedOptionFactory commentedOptionFactory) {
        return new FormDefinitionCopyHelper(ioService, serializer, commentedOptionFactory);
    }

    @Override
    protected void beforeProcess(FormDefinition formDefinition) {
        super.beforeProcess(formDefinition);
        this.originalId = formDefinition.getId();
    }

    @Override
    protected void verifyForm(FormDefinition formDefinition) {
        super.verifyForm(formDefinition);

        String formId = formDefinition.getId();

        assertFalse(formId.equals(originalId));

        LayoutComponent firstField = formDefinition.getLayoutTemplate().getRows().get(0).getLayoutColumns().get(0).getLayoutComponents().get(0);
        verifyLayoutComponent(firstField, formId);

        LayoutComponent secondField = formDefinition.getLayoutTemplate().getRows().get(0).getLayoutColumns().get(1).getLayoutComponents().get(0);
        verifyLayoutComponent(secondField, formId);
    }

    private void verifyLayoutComponent(LayoutComponent layoutComponent, String formId) {
        Assertions.assertThat(layoutComponent)
                .isNotNull();

        Assertions.assertThat(layoutComponent.getProperties().get(FormLayoutComponent.FORM_ID))
                .isEqualTo(formId);
    }
}
