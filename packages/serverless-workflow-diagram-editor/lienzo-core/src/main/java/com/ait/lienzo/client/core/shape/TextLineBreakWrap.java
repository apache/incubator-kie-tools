/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;

/**
 * ITextWrapper implementation that wraps text on line breaks.
 */
public class TextLineBreakWrap extends TextNoWrap {

    public TextLineBreakWrap(final Text text) {
        super(text);
    }

    public TextLineBreakWrap(final Supplier<String> textSupplier,
                             final Supplier<Double> fontSizeSupplier,
                             final Supplier<String> fontStyleSupplier,
                             final Supplier<String> fontFamilySupplier,
                             final Supplier<TextUnit> textUnitSupplier,
                             final Supplier<TextBaseLine> textBaseLineSupplier,
                             final Supplier<TextAlign> textAlignSupplier) {
        super(textSupplier,
              fontSizeSupplier,
              fontStyleSupplier,
              fontFamilySupplier,
              textUnitSupplier,
              textBaseLineSupplier,
              textAlignSupplier);
    }

    @Override
    public BoundingBox getBoundingBox() {
        double width = 0;
        final String text = textSupplier.get();
        if (text == null || text.isEmpty()) {
            return new BoundingBox();
        }
        final String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            double w = getBoundingBoxForString(line).getWidth();
            width = Math.max(width,
                             w);
        }
        double height = getBoundingBoxForString(text).getHeight();
        height = height * lines.length;
        return new BoundingBox().addX(0).addX(width).addY(0).addY(height);
    }

    @Override
    public void drawString(final Context2D context,
                           final IDrawString drawCommand) {
        if (text == null || text.getText().isEmpty()) {
            return;
        }

        final String[] lines = text.getText().split("\\r?\\n");
        final BoundingBox bb = getBoundingBox();

        double xOffset = 0;

        switch (textAlignSupplier.get()) {
            case START:
            case LEFT:
                xOffset = 0;
                break;

            case CENTER:
                xOffset = bb.getWidth() / 2;
                break;

            case END:
            case RIGHT:
                xOffset = bb.getWidth();
                break;
        }

        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i];
            drawCommand.draw(context,
                             line,
                             xOffset,
                             i + 0.8);
        }
    }
}
