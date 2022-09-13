/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class TextBoundsWrapTest extends BaseTextTest {

    @Test
    public void testTextBoundsWrap() {
        testTextBoundsWrap("very long text that should wrap",
                           new Object[]{
                                   new DrawnText("very long ",
                                                 0,
                                                 0.8),
                                   new DrawnText("text that ",
                                                 0,
                                                 1.8),
                                   new DrawnText("should    ",
                                                 0,
                                                 2.8),
                                   new DrawnText("wrap      ",
                                                 0,
                                                 3.8)
                           });
    }

    @Test
    public void testTextBoundsWrapOneWord() {
        testTextBoundsWrap("very",
                           new Object[]{
                                   new DrawnText("very      ",
                                                 0,
                                                 0.8)
                           });
    }

    @Test
    public void testTextBoundsWrapWhiteSpace() {
        testTextBoundsWrap("   ",
                           new Object[]{});
    }

    private void testTextBoundsWrap(final String text,
                                    final Object[] results) {
        BoundingBox bbox = new BoundingBox().addX(0).addY(0).addX(10).addY(10);
        Text tested = spy(new Text(text));

        tested.setWrapper(new TextBoundsWrap(tested,
                                             bbox));
        tested.setTextAlign(TextAlign.LEFT);

        when(tested.getLineHeight(context)).thenReturn(1.0);
        tested.getBoundingBox();

        tested.drawWithTransforms(context,
                                  1,
                                  bbox);

        assertArrayEquals(results,
                          drawnTexts.toArray());
    }
}
