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

import java.util.ArrayList;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import elemental2.dom.TextMetrics;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class BaseTextTest {

    @Mock
    protected Context2D context;

    @Mock
    protected ScratchPad scratchPad;

    protected ArrayList<DrawnText> drawnTexts = new ArrayList<>();

    protected Answer<Object> drawTextAnswer = invocation -> {
        Object[] args = invocation.getArguments();
        String text = (String) args[0];
        double x = (double) args[1];
        double y = (double) args[2];
        drawnTexts.add(new DrawnText(text,
                                     x,
                                     y));
        return null;
    };

    @Before
    public void setup() {
        when(scratchPad.getContext()).thenReturn(context);
        when(context.measureText(anyString())).thenAnswer((Answer<TextMetrics>) invocation -> {
            Object[] args = invocation.getArguments();
            String arg = (String) args[0];
            TextMetrics out = new TextMetrics();
            when(out.width).thenReturn(arg.length() * 1.0);
            return out;
        });

        doAnswer(drawTextAnswer)
                .when(context).fillText(anyString(),
                                        anyDouble(),
                                        anyDouble());

        doAnswer(drawTextAnswer)
                .when(context).strokeText(anyString(),
                                          anyDouble(),
                                          anyDouble());
    }

    protected static class DrawnText {

        String text;
        double x;
        double y;

        DrawnText(String text,
                  double x,
                  double y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DrawnText) {
                DrawnText other = (DrawnText) o;
                return text.equals(other.text) &&
                        Math.abs(x - other.x) < 0.001 &&
                        Math.abs(y - other.y) < 0.001;
            }
            return false;
        }

        @Override
        public String toString() {
            return "\"" + text + "\"" + " @ " + "(" + x + "," + y + ")";
        }
    }
}
