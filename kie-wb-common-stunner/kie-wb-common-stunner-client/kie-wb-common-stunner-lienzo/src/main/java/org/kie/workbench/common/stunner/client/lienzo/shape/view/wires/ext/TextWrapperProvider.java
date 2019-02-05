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
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;

public final class TextWrapperProvider {

    public static ITextWrapper get(final TextWrapperStrategy strategy,
                                   final Text text) {
        switch (strategy) {
            case BOUNDS_AND_LINE_BREAKS:
                return new TextBoundsAndLineBreaksWrap(text);

            case LINE_BREAK:
                return new TextLineBreakWrap(text);

            case NO_WRAP:
                return new TextNoWrap(text);

            case TRUNCATE:
                return new TextTruncateWrapper(text, new BoundingBox(0,
                                                                     0,
                                                                     1,
                                                                     1));
            default:
                return new TextBoundsWrap(text);
        }
    }
}