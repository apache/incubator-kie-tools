/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.library;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Option;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LibraryToolbarViewTest {

    @Mock
    private Document document;

    @InjectMocks
    private LibraryToolbarView view;

    /*
     * https://issues.jboss.org/browse/RHDM-255
     * [IE11] An error occurs when creating new space in the business central
     * The value attribute must be set in order that option.getValue() returns the correct value.
     * Only calling option.setText() is enough for modern browsers only.
     */
    @Test
    public void makeSureTextAndValueAreBeingSetWhenCreatingNewOption() {
        final Option option = mock(Option.class);
        doReturn(option).when(document).createElement("option");

        final Option createdOption = view.createOption("alias");

        assertEquals(option,
                     createdOption);
        verify(option).setValue("alias");
        verify(option).setText("alias");
    }
}
