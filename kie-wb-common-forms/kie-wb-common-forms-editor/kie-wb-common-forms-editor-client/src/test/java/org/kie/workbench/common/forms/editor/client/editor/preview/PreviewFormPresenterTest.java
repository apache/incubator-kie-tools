/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.preview;

import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.FormRenderingContext;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PreviewFormPresenterTest extends TestCase {

    @Mock
    private PreviewFormPresenter.PreviewFormPresenterView view;

    @Mock
    private FormRenderingContext context;

    private PreviewFormPresenter presenter;


    @Before
    public void init() {
        presenter = new PreviewFormPresenter( view );
    }

    @Test
    public void testRenderContext() {
        presenter.preview( context );

        presenter.asWidget();

        verify( view ).preview( context );

        verify( view ).asWidget();
    }
}
