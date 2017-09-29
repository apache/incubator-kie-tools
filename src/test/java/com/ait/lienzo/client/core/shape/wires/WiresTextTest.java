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

package com.ait.lienzo.client.core.shape.wires;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.ITextWrapper;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsWrap;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.test.LienzoMockitoTestRunner;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresTextTest
{
    private static final ScratchPad     scratchPad     = mock(ScratchPad.class);

    private static final Context2D      context        = mock(Context2D.class);

    private static ArrayList<DrawnText> drawnTexts     = new ArrayList<>();

    private static Answer<Object>       drawTextAnswer = new Answer<Object>()
    {
        @Override
        public Object answer(final InvocationOnMock invocation) throws Throwable
        {
            final Object[] args = invocation.getArguments();
            final String text = (String) args[0];
            final double x = (double) args[1];
            final double y = (double) args[2];

            drawnTexts.add(new DrawnText(text, x, y));

            return null;
        }
    };

    @Before
    public void setup() throws Exception
    {
        try
        {
            setFinalStatic(Text.class.getDeclaredField("FORBOUNDS"), scratchPad);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        when(scratchPad.getContext()).thenReturn(context);
        when(context.measureText(anyString())).thenAnswer(new Answer<TextMetrics>()
        {
            @Override
            public TextMetrics answer(final InvocationOnMock invocation) throws Throwable
            {
                final Object[] args = invocation.getArguments();
                final String arg = (String) args[0];
                final TextMetrics out = mock(TextMetrics.class);
                when(out.getWidth()).thenReturn(arg.length() * 1.0);
                when(out.getHeight()).thenReturn(1.0);
                return out;
            }
        });
        doAnswer(drawTextAnswer).when(context).fillText(anyString(), anyDouble(), anyDouble());

        doAnswer(drawTextAnswer).when(context).strokeText(anyString(), anyDouble(), anyDouble());
    }

    @Test
    public void testTextWrap()
    {
        final BoundingBox bbox = new BoundingBox().addX(0).addY(0).addX(10).addY(10);
        final Text tested = new Text("very long text that should wrap");

        final ITextWrapper wrapper = new TextBoundsWrap(tested, bbox);
        tested.setWrapper(wrapper);
        tested.setTextAlign(TextAlign.LEFT);

        spy(tested);

        when(tested.getLineHeight(context)).thenReturn(1.0);
        assertEquals(bbox, tested.getWrapper().getBoundingBox());
        tested.getBoundingBox();
        assertEquals(bbox.getWidth(), tested.getBoundingBox().getWidth(), 0.0001);

        //Cannot be tested until a workaround is found for BoundingBox
        tested.drawWithTransforms(context, 1, bbox);

        assertArrayEquals(new Object[] { new DrawnText("very long ", 0, 0.8), new DrawnText("text that ", 0, 1.8), new DrawnText("should    ", 0, 2.8), new DrawnText("wrap      ", 0, 3.8) }, drawnTexts.toArray());
    }

    private static void setFinalStatic(final Field field, final Object newValue) throws Exception
    {
        field.setAccessible(true);
        // remove final modifier from field
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    private static class DrawnText
    {
        String text;

        double x;

        double y;

        DrawnText(final String text, final double x, final double y)
        {
            this.text = text;
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(final Object o)
        {
            if (o instanceof DrawnText)
            {
                final DrawnText other = (DrawnText) o;
                return text.equals(other.text) && (Math.abs(x - other.x) < 0.001) && (Math.abs(y - other.y) < 0.001);
            }
            return false;
        }

        @Override
        public String toString()
        {
            return "\"" + text + "\"" + " @ " + "(" + x + "," + y + ")";
        }
    }
}
