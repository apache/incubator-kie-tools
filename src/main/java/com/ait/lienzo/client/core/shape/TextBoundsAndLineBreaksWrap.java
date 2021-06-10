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

import java.util.ArrayList;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;

/**
 * ITextWrapper implementation that wraps text when a line exceeds the width of the provided boundary.
 */
public class TextBoundsAndLineBreaksWrap extends TextBoundsWrap {
    public TextBoundsAndLineBreaksWrap(final Text text) {
        super(text);
    }

    public TextBoundsAndLineBreaksWrap(final Text text,
                                       final BoundingBox wrapBoundaries) {
        super(text, wrapBoundaries);
    }

    public TextBoundsAndLineBreaksWrap(final Supplier<String> textSupplier,
                                       final Supplier<Double> fontSizeSupplier,
                                       final Supplier<String> fontStyleSupplier,
                                       final Supplier<String> fontFamilySupplier,
                                       final Supplier<TextUnit> textUnitSupplier,
                                       final Supplier<TextBaseLine> textBaseLineSupplier,
                                       final Supplier<TextAlign> textAlignSupplier,
                                       final BoundingBox wrapBoundaries) {
        super(textSupplier,
              fontSizeSupplier,
              fontStyleSupplier,
              fontFamilySupplier,
              textUnitSupplier,
              textBaseLineSupplier,
              textAlignSupplier,
              wrapBoundaries);
    }

    @Override
    public void drawString(final Context2D context,
                           final IDrawString drawCommand) {
        final BoundingBox wrapBoundaries = getWrapBoundaries();
        final String[] textLines = text.getText().split("\\r?\\n");
        if (textLines.length < 1) {
            return;
        }

        final ArrayList<String> lines = new ArrayList<>();
        for (String line : textLines) {
            String[] words = line.split("\\s");
            if (words.length < 1) {
                lines.add("");
                continue;
            }
            final StringBuilder nextLine = new StringBuilder(words[0]);

            for (int i = 1; i < words.length; i++) {
                if (getBoundingBoxForString(nextLine + " " + words[i]).getWidth() <= wrapBoundaries.getWidth()) {
                    nextLine.append(" ").append(words[i]);
                } else {
                    lines.add(nextLine.toString());
                    nextLine.setLength(words[i].length());
                    nextLine.replace(0,
                                     words[i].length(),
                                     words[i]);
                }
            }
            lines.add(nextLine.toString());
        }

        double xOffset = 0;

        switch (textAlignSupplier.get()) {
            case START:
            case LEFT:
                xOffset = 0;
                break;

            case CENTER:
                xOffset = wrapBoundaries.getWidth() / 2;
                break;

            case END:
            case RIGHT:
                xOffset = wrapBoundaries.getWidth();
                break;
        }
        
        double yOffset = 0.8;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int toPad = (int) Math.round((wrapBoundaries.getWidth() - getBoundingBoxForString(line).getWidth()) / getBoundingBoxForString(" ").getWidth());
            line = textUtils.padString(line,
                                       line.length() + toPad,
                                       ' ',
                                       textAlignSupplier.get());
            drawCommand.draw(context,
                             line,
                             xOffset,
                             i + yOffset);
        }
    }
}
