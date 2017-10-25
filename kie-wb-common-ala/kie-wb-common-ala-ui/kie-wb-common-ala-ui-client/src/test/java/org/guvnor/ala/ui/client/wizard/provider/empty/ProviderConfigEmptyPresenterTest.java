/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.wizard.provider.empty;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderConfigEmptyPresenterTest {

    @Mock
    private ProviderConfigEmptyPresenter.View view;

    private ProviderConfigEmptyPresenter presenter;

    @Before
    public void setUp() {
        presenter = new ProviderConfigEmptyPresenter(view);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testIsValid() {
        //must always be false, since this presenter does nothing.
        presenter.isValid(Assert::assertFalse);
    }
}
