/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.properties.binding.dynamic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DataBinderEditorTest;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DynamicDataBinderEditorTest extends DataBinderEditorTest<DynamicDataBinderEditor> {

    @Mock
    protected DynamicDataBinderEditorView view;

    @Override
    public void initTest() {
        super.initTest();

        editor = new DynamicDataBinderEditor(view);
    }

    @Test
    public void testFunctionallity() {
        editor.setUp();
        verify(view).init(editor);

        editor.init(fieldDefinition,
                    bindingsSupplier,
                    bindingChangeConsumer);

        verify(view).clear();

        verify(bindingsSupplier,
               never()).get();

        verify(view).setFieldBinding(FIELD_BINDING);

        editor.onBindingChange();
        verify(bindingChangeConsumer).accept(any());

        editor.getElement();
        verify(view).getElement();
    }
}
