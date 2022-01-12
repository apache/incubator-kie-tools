/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.AbstractFormGroupTest;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultFormGroupTest extends AbstractFormGroupTest<DefaultFormGroup, DefaultFormGroupView> {

    private static final String INPUT_ID = "id";

    @Override
    protected Class<DefaultFormGroupView> getViewClass() {
        return DefaultFormGroupView.class;
    }

    @Override
    protected DefaultFormGroup getFormGroupInstance(DefaultFormGroupView view) {
        return new DefaultFormGroup(view);
    }

    @Test
    public void testRenderWithInputId() {
        formGroup.render(INPUT_ID, widget, fieldDefinition);

        verify(view).render(INPUT_ID, widget, fieldDefinition);

        formGroup.getElement();

        verify(view).getElement();
    }
}
