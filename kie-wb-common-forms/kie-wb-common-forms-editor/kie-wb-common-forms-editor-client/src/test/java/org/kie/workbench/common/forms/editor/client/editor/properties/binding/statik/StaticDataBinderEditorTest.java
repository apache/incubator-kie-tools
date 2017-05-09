/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.editor.properties.binding.statik;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DataBinderEditorTest;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StaticDataBinderEditorTest extends DataBinderEditorTest<StaticDataBinderEditor> {

    @Mock
    protected StaticDataBinderEditorView view;

    @Override
    public void initTest() {
        super.initTest();

        editor = new StaticDataBinderEditor(view);
    }

    @Test
    public void testFunctionallity() {
        editor.setUp();
        verify(view).init(editor);

        editor.init(helper);

        verify(view).clear();

        verify(view,
               times(fields.size() + 1)).addModelField(anyString(),
                                                   anyBoolean());
        verify(helper,
               times(fields.size())).getCurrentField();
        verify(fieldDefinition,
               times(fields.size())).getBinding();

        fields.forEach(field -> {
            verify(view).addModelField(field,
                                       FIELD_BINDING.endsWith(field));
        });

        editor.onBindingChange(NAME);
        verify(helper).onFieldBindingChange(NAME);

        editor.getElement();
        verify(view).getElement();
    }
}
