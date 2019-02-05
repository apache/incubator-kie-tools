/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.ait.lienzo.client.core.shape.ITextWrapper;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsAndLineBreaksWrap;
import com.ait.lienzo.client.core.shape.TextBoundsWrap;
import com.ait.lienzo.client.core.shape.TextLineBreakWrap;
import com.ait.lienzo.client.core.shape.TextNoWrap;
import com.ait.lienzo.client.core.shape.TextTruncateWrapper;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class TextWrapperProviderTest {

    @Mock
    private Text text;

    @Test
    public void testBoundsAndLineBreaks() {
        final ITextWrapper wrapper = TextWrapperProvider.get(TextWrapperStrategy.BOUNDS_AND_LINE_BREAKS, text);
        assertTrue(wrapper instanceof TextBoundsAndLineBreaksWrap);
    }

    @Test
    public void testLineBreak() {
        final ITextWrapper wrapper = TextWrapperProvider.get(TextWrapperStrategy.LINE_BREAK, text);
        assertTrue(wrapper instanceof TextLineBreakWrap);
    }

    @Test
    public void testNoWrap() {
        final ITextWrapper wrapper = TextWrapperProvider.get(TextWrapperStrategy.NO_WRAP, text);
        assertTrue(wrapper instanceof TextNoWrap);
    }

    @Test
    public void testTruncate() {
        final ITextWrapper wrapper = TextWrapperProvider.get(TextWrapperStrategy.TRUNCATE, text);
        assertTrue(wrapper instanceof TextTruncateWrapper);
    }

    @Test
    public void testBounds() {
        final ITextWrapper wrapper = TextWrapperProvider.get(TextWrapperStrategy.BOUNDS, text);
        assertTrue(wrapper instanceof TextBoundsWrap);
    }
}