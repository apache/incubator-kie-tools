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

package org.guvnor.ala.ui.client.provider.status.runtime.actions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeActionItemSeparatorPresenterTest {

    @Mock
    private RuntimeActionItemSeparatorView view;

    private RuntimeActionItemSeparatorPresenter presenter;

    @Before
    public void setUp() {
        presenter = new RuntimeActionItemSeparatorPresenter(view);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }
}
