/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class EditableCollectionBoxTest {

    @Mock
    private Callback<String> changed;

    @Mock
    private TextBox view;

    @Test
    public void testOnValueChange() {

        final EditableCollectionBox collectionBox = new EditableCollectionBox(changed, view, "", "");

        collectionBox.onValueChange("[1, 2, 3]");

        verify(changed).callback("=[1, 2, 3]");
    }

    @Test
    public void withCollectionPrefix() {

        final String expected = "=[12, 34]";
        final String actual = EditableCollectionBox.withCollectionPrefix("[12, 34]");

        assertEquals(expected, actual);
    }

    @Test
    public void withCollectionPrefixWhenValueAlreadyHasThePrefix() {

        final String expected = "=[12, 34]";
        final String actual = EditableCollectionBox.withCollectionPrefix("=[12, 34]");

        assertEquals(expected, actual);
    }

    @Test
    public void withoutCollectionPrefix() {

        final String expected = "[12, 34]";
        final String actual = EditableCollectionBox.withoutCollectionPrefix("=[12, 34]");

        assertEquals(expected, actual);
    }

    @Test
    public void withoutCollectionPrefixWhenValueDoesNotHaveThePrefix() {

        final String expected = "[12, 34]";
        final String actual = EditableCollectionBox.withoutCollectionPrefix("[12, 34]");

        assertEquals(expected, actual);
    }
}
