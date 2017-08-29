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

package org.kie.workbench.common.forms.editor.client.editor.changes.displayers.newProperties;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.editor.changes.displayers.ModelChangeDisplayerTestFieldProvider;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewPropertiesDisplayerTest {

    List<FieldDefinition> fields;

    @Mock
    NewPropertiesDisplayerView view;

    NewPropertiesDisplayer presenter;

    @Before
    public void init() {
        fields = ModelChangeDisplayerTestFieldProvider.getFields();
        presenter = new NewPropertiesDisplayer(view);
    }

    @Test
    public void testFunctionallity() {
        verify(view).init(presenter);

        presenter.getElement();

        verify(view).getElement();

        presenter.showAvailableFields(fields);

        verify(view,
               times(fields.size())).showProperty(any());

        presenter.clear();

        verify(view).clear();
    }
}
