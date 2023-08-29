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

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class TextLineBreakTruncateWrapperTest extends BaseTextTest {

    @Test
    public void testSingleShortWordText() {
        testTextBoundsWrap("short",
                           new Object[]{
                                   new DrawnText("short",
                                                 0,
                                                 0.8)
                           });
    }

    @Test
    public void testMultipleShortWordsMultilinesText() {
        testTextBoundsWrap("short words",
                           new Object[]{
                                   new DrawnText("short",
                                                 0,
                                                 0.8),
                                   new DrawnText("words",
                                                 0,
                                                 1.8)
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
                           getWidth(8));
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
                           getWidth(8));
    }

    @Test
    public void testTruncateSingleWord() {
        testTextBoundsWrap("LongWordThatDoesntFits",
                           new Object[]{
                                   new DrawnText("L...",
                                                 0,
                                                 0.8)
                           },
                           getWidth(4),
                           2);
    }

    @Test
    public void testTruncateSingleWordTruncatedWithNoDots() {
        testTextBoundsWrap("LongWordThatDoesntFits",
                           new Object[]{
                                   new DrawnText("Lon",
                                                 0,
                                                 0.8),
                                   new DrawnText("gWo",
                                                 0,
                                                 1.8),
                                   new DrawnText("rdT",
                                                 0,
                                                 2.8)
                           },
                           getWidth(2),
                           8);
    }

    @Test
    public void testTruncateAndWrapSingleWord() {
        testTextBoundsWrap("LongWordThatDoesntFits",
                           new Object[]{
                                   new DrawnText("LongWordT",
                                                 0,
                                                 0.8),
                                   new DrawnText("hatDo...",
                                                 0,
                                                 1.8),
                           },
                           getWidth(8),
                           getLineHeight(2));
    }

    @Test
    public void testTruncateAndWrapWord() {
        testTextBoundsWrap("Short a VeryLong123456789 Text",
                           new Object[]{
                                   new DrawnText("Short a V",
                                                 0,
                                                 0.8),
                                   new DrawnText("eryLong12",
                                                 0,
                                                 1.8),
                                   new DrawnText("3456789",
                                                 0,
                                                 2.8),
                                   new DrawnText("Text",
                                                 0,
                                                 3.8)
                           },
                           getWidth(8),
                           getLineHeight(4));
    }

    @Test
    public void testTruncateAndWrapShortLongWord() {
        testTextBoundsWrap("Short VeryLooong Short",
                           new Object[]{
                                   new DrawnText("Short",
                                                 0,
                                                 0.8),
                                   new DrawnText("VeryL...",
                                                 0,
                                                 1.8)
                           },
                           getWidth(8),
                           getLineHeight(2));
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
                           getWidth(5),
                           getLineHeight(2));
    }

    @Test
    public void testLineBreak() {
        testTextBoundsWrap("Word1\nWord2\nWord3 Word4 Word5 Word6 Word7",
                           new Object[]{
                                   new DrawnText("Word1",
                                                 0,
                                                 0.8),
                                   new DrawnText("Word2",
                                                 0,
                                                 1.8),
                                   new DrawnText("Word3 Word4 Word5 Word6 Word7",
                                                 0,
                                                 2.8)
                           },
                           getWidth(30),
                           getLineHeight(3));
    }

    private int getLineHeight(int num) {
        return num * 2 + 1;
    }

    @Test
    public void testLineBreakWithSpaces() {
        testTextBoundsWrap("Word 1   \n Word 2   \n   Word 3 Word 4",
                           new Object[]{
                                   new DrawnText("Word 1",
                                                 0,
                                                 0.8),
                                   new DrawnText(" Word 2",
                                                 0,
                                                 1.8),
                                   new DrawnText("   Word 3 Word 4",
                                                 0,
                                                 2.8)
                           },
                           getWidth(30),
                           getLineHeight(3));
    }

    @Test
    public void testLineBreakTruncated() {
        testTextBoundsWrap("Word1\nWord2\nWord3 Word4 Word5 Word6 Word7",
                           new Object[]{
                                   new DrawnText("Word1",
                                                 0,
                                                 0.8),
                                   new DrawnText("Word2",
                                                 0,
                                                 1.8),
                                   new DrawnText("Word3",
                                                 0,
                                                 2.8),
                                   new DrawnText("Wo...",
                                                 0,
                                                 3.8)
                           },
                           getWidth(5),
                           getLineHeight(4));
    }

    private void testTextBoundsWrap(final String text,
                                    final Object[] results) {
        testTextBoundsWrap(text, results, getWidth(6));
    }

    private double getWidth(double width) {
        //adding margin
        return 10 + width;
    }

    private void testTextBoundsWrap(final String text,
                                    final Object[] results,
                                    final double width) {
        testTextBoundsWrap(text, results, width, 100);
    }

    private void testTextBoundsWrap(final String text,
                                    final Object[] results,
                                    final double width,
                                    final double height) {
        final BoundingBox bbox = new BoundingBox().addX(0).addY(0).addX(width).addY(height);
        final Text tested = spy(new Text(text));

        TextLineBreakTruncateWrapper wrapper = new TextLineBreakTruncateWrapper(tested,
                                                                                bbox);

        tested.setWrapper(wrapper);
        tested.setTextAlign(TextAlign.LEFT);

        when(tested.getLineHeight(context)).thenReturn(1.0);

        assertTrue(bbox.getWidth() >= tested.getBoundingBox().getWidth());

        tested.drawWithTransforms(context,
                                  1,
                                  bbox);

        assertArrayEquals(results, getDrawnTextRemovePaddingChar());
    }

    private Object[] getDrawnTextRemovePaddingChar() {
        for (Object o : drawnTexts.toArray()) {
            DrawnText drawnText = (DrawnText) o;
            drawnText.text = StringUtils.stripEnd(drawnText.text, " ");
        }
        return drawnTexts.toArray();
    }
}