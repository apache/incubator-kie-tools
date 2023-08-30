/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public class TextTruncateWrapperTest extends BaseTextTest {

    @Test
    public void atestSingleCharText() {
        testTextBoundsWrap("a",
                           new Object[]{
                                   new DrawnText("a     ",
                                                 0,
                                                 0.8)
                           });
    }

    @Test
    public void testSingleShortWordText() {
        testTextBoundsWrap("short",
                           new Object[]{
                                   new DrawnText("short ",
                                                 0,
                                                 0.8)
                           });
    }

    @Test
    public void testMultipleShortWordsMultilinesText() {
        testTextBoundsWrap("short words",
                           new Object[]{
                                   new DrawnText("short ",
                                                 0,
                                                 0.8),
                                   new DrawnText("words ",
                                                 0,
                                                 1.8)
                           });
    }

    @Test
    public void testTwoWordsFitsSingleLineText() {
        testTextBoundsWrap("ab cd",
                           new Object[]{
                                   new DrawnText("ab cd ",
                                                 0,
                                                 0.8)
                           });
    }

    @Test
    public void testMultipleWordsFitsSingleLineText() {
        testTextBoundsWrap("ab cd ef",
                           new Object[]{
                                   new DrawnText("ab cd ef",
                                                 0,
                                                 0.8)
                           },
                           28);
    }

    @Test
    public void testMultipleWordsFitsFirstWordsOnSingleLineText() {
        testTextBoundsWrap("ab cd ef longlong",
                           new Object[]{
                                   new DrawnText("ab cd ef",
                                                 0,
                                                 0.8),
                                   new DrawnText("longlong",
                                                 0,
                                                 1.8)
                           },
                           28);
    }

    @Test
    public void testMultipleWordsText() {
        testTextBoundsWrap("ab cd ef longlong gh ij",
                           new Object[]{
                                   new DrawnText("ab cd ef",
                                                 0,
                                                 0.8),
                                   new DrawnText("longlong",
                                                 0,
                                                 1.8),
                                   new DrawnText("gh ij   ",
                                                 0,
                                                 2.8)
                           },
                           28);
    }

    @Test
    public void testTruncateSingleWord() {
        testTextBoundsWrap("LongWordThatDoesntFits",
                           new Object[]{
                                   new DrawnText("Lo...",
                                                 0,
                                                 0.8)
                           },
                           22,
                           2);
    }

    @Test
    public void testTruncateAndWrapSingleWord() {
        testTextBoundsWrap("LongWordThatDoesntFits",
                           new Object[]{
                                   new DrawnText("LongWord",
                                                 0,
                                                 0.8),
                                   new DrawnText("ThatD...",
                                                 0,
                                                 1.8),
                           },
                           28,
                           6);
    }

    @Test
    public void testTruncateAndWrapWord() {
        testTextBoundsWrap("Short VeryLooong Short",
                           new Object[]{
                                   new DrawnText("Short   ",
                                                 0,
                                                 0.8),
                                   new DrawnText("VeryLooo",
                                                 0,
                                                 1.8),
                                   new DrawnText("ng Short",
                                                 0,
                                                 2.8)
                           },
                           28,
                           8);
    }

    @Test
    public void testTruncateAndWrapShortLongWord() {
        testTextBoundsWrap("Short VeryLooong Short",
                           new Object[]{
                                   new DrawnText("Short   ",
                                                 0,
                                                 0.8),
                                   new DrawnText("VeryL...",
                                                 0,
                                                 1.8)
                           },
                           28,
                           6);
    }

    @Test
    public void testTruncateSequenceOfShortWords() {
        testTextBoundsWrap("Word1 Word2 Word3 Word4 Word5 Word6 Word7",
                           new Object[]{
                                   new DrawnText("Word1",
                                                 0,
                                                 0.8),
                                   new DrawnText("Wo...",
                                                 0,
                                                 1.8)
                           },
                           25,
                           6);
    }

    private void testTextBoundsWrap(final String text,
                                    final Object[] results) {
        testTextBoundsWrap(text, results, 26);
    }

    private void testTextBoundsWrap(final String text,
                                    final Object[] results,
                                    final double width) {
        testTextBoundsWrap(text, results, width, 10);
    }

    private void testTextBoundsWrap(final String text,
                                    final Object[] results,
                                    final double width,
                                    final double height) {

        final BoundingBox bbox = new BoundingBox().addX(0).addY(0).addX(width).addY(height);
        final Text tested = spy(new Text(text));

        tested.setWrapper(new TextTruncateWrapper(tested, bbox));
        tested.setTextAlign(TextAlign.LEFT);

        Mockito.doReturn(1.0).when(tested).getLineHeight(any(Context2D.class));

        assertTrue(bbox.getWidth() >= tested.getBoundingBox().getWidth());

        tested.drawWithTransforms(context,
                                  1,
                                  bbox);
        assertArrayEquals(results, drawnTexts.toArray());
    }
}