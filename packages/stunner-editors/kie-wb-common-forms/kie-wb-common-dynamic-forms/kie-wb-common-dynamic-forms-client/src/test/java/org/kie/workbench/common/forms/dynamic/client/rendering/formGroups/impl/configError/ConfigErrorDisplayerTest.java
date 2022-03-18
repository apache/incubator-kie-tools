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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ConfigErrorDisplayerTest {

    @Mock
    protected ConfigErrorDisplayerView view;

    protected ConfigErrorDisplayer presenter;

    @Before
    public void setUp() {
        presenter = new ConfigErrorDisplayer(view);
    }

    @Test
    public void testDefaultFuncionallity() {
        List<String> errors = new ArrayList<>();

        presenter.render(errors);

        verify(view).render(errors);

        presenter.asWidget();

        verify(view).asWidget();
    }

    @Test
    public void testDefaultFuncionallityWithNullErrors() {

        presenter.render(null);

        verify(view,
               never()).render(null);

        presenter.asWidget();

        verify(view).asWidget();
    }
}
