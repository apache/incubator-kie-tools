/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.guided.tour;

import java.util.Objects;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Console;
import elemental2.dom.DomGlobal;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Rect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedTourCustomSelectorPositionProviderTest {

    @Mock
    private Console console;

    private Rect none;

    private GuidedTourCustomSelectorPositionProvider positionProvider;

    @Before
    public void setup() {
        positionProvider = spy(GuidedTourCustomSelectorPositionProvider.getInstance());
        none = makeRect(0);

        DomGlobal.console = console;

        doReturn(none).when(positionProvider).none();
    }

    @Test
    public void testGetPosition() {
        final Rect rect1 = makeRect(1);
        final Rect rect2 = makeRect(2);

        positionProvider.registerPositionProvider("TEST_PROVIDER_1", name -> Objects.equals(name, "OBJECT-1") ? rect1 : rect2);

        assertEquals(rect1, positionProvider.getPosition("TEST_PROVIDER_1:::OBJECT-1"));
        assertEquals(rect2, positionProvider.getPosition("TEST_PROVIDER_1:::OBJECT-2"));
    }

    @Test
    public void testGetPositionWhenSelectorIsInvalid() {
        final Rect rect1 = makeRect(1);
        final Rect rect2 = makeRect(2);

        positionProvider.registerPositionProvider("TEST_PROVIDER_2", name -> Objects.equals(name, "OBJECT-1") ? rect1 : rect2);

        final Rect position = positionProvider.getPosition("TEST_PROVIDER_2___OBJECT-1");

        verify(console).warn("[Guided Tour - Position Provider] Invalid custom query selector: TEST_PROVIDER_2___OBJECT-1");
        assertEquals(none, position);
    }

    @Test
    public void testGetPositionWhenNoSelectorIsRegistered() {
        final Rect position = positionProvider.getPosition("TEST_PROVIDER_3:::OBJECT-1");

        verify(console).warn("[Guided Tour - Position Provider] The position provider could not be found: TEST_PROVIDER_3");
        assertEquals(none, position);
    }

    @Test
    public void testGetPositionWhenSelectorIsNull() {
        final Rect position = positionProvider.getPosition(null);

        verify(console).warn("[Guided Tour - Position Provider] Invalid custom query selector: null");
        assertEquals(none, position);
    }

    private Rect makeRect(final int seed) {
        final Rect rect = new Rect();
        rect.setBottom(seed);
        rect.setHeight(seed);
        rect.setLeft(seed);
        rect.setRight(seed);
        rect.setTop(seed);
        rect.setWidth(seed);
        rect.setX(seed);
        rect.setY(seed);
        return rect;
    }
}
