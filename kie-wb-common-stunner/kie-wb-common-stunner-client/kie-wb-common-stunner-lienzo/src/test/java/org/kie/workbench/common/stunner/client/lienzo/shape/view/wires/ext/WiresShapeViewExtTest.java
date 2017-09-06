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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import java.lang.reflect.Field;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeViewExtTest {

    private static ViewEventType[] viewEventTypes = {};
    private final static MultiPath PATH = new MultiPath();

    @Mock
    private WiresTextDecorator textDecorator;

    private WiresShapeViewExt<WiresShapeViewExt> tested;

    @Before
    public void setup() throws Exception {
        this.tested = new WiresShapeViewExt<>(viewEventTypes,
                                              PATH);
        setPrivateField(WiresShapeViewExt.class,
                        tested,
                        "textViewDecorator",
                        textDecorator);
    }

    @Test
    public void testTitle() {
        //setTitle should not thrown an exception when called with a null argument
        tested.setTitle(null);
    }

    @Test
    public void testTextWrapBoundariesUpdates() {
        tested.refresh();
        verify(textDecorator).setTextBoundaries(PATH.getBoundingBox());
    }

    public static void setPrivateField(Class clazz,
                                       Object instance,
                                       String field,
                                       Object value) {
        try {
            Field myField = clazz.getDeclaredField(field);
            myField.setAccessible(true);
            myField.set(instance,
                        value);
        } catch (Exception e) {
            System.err.printf("Attempted to set non-existing field %s on %s." +
                                      " This test might need updating",
                              field,
                              instance.toString());
        }
    }
}
