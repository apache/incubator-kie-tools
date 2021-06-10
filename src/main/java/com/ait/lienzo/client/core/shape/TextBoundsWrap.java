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
import java.util.List;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;

/**
 * ITextWrapper implementation that wraps text when a line exceeds the width of the provided boundary.
 */
public class TextBoundsWrap extends TextNoWrap implements ITextWrapperWithBoundaries  {

    protected static final double      Y_OFFSET = 0.8;
    private                BoundingBox wrapBoundaries;

    public TextBoundsWrap(final Text text) {
        this(text,
             new BoundingBox());
    }

    public TextBoundsWrap(final Text text,
                          final BoundingBox wrapBoundaries) {
        super(text);
        setWrapBoundaries(wrapBoundaries);
    }

    public TextBoundsWrap(final Supplier<String> textSupplier,
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
              textAlignSupplier);
        this.wrapBoundaries = wrapBoundaries;
    }

    public BoundingBox getWrapBoundaries() {
        return wrapBoundaries;
    }

    @Override
    public void setWrapBoundaries(final BoundingBox boundaries) {
        wrapBoundaries = boundaries;
    }

    public BoundingBox getTextBoundaries() {
        final double[] boundaries = calculateWrapBoundaries();
        return new BoundingBox().addX(0).addX(boundaries[0]).addY(0).addY(boundaries[1]);
    }

    @Override
    public BoundingBox getBoundingBox() {
        final double[] boundaries = calculateWrapBoundaries();
        return BoundingBox.fromDoubles(0, 0, boundaries[0], boundaries[1]);
    }

    private double[] calculateWrapBoundaries() {
        final String[] words = textSupplier.get().split("\\s");
        if (words.length < 1) {
            return new double[] { getWrapBoundaries().getX(), getWrapBoundaries().getY() };
        }

        final double        wrapWidth  = getWrapBoundaries().getWidth();
        final String        firstWord  = words[0];
        double              width;
        final StringBuilder nextLine   = new StringBuilder(firstWord);
        int                 numOfLines = 1;
        double              maxWidth = 0;
        for (int i = 1; i < words.length; i++) {
            width = getBoundingBoxForString(nextLine + " " + words[i]).getWidth();
            if (width <= wrapWidth) {
                nextLine.append(" ").append(words[i]);
                if(maxWidth < width){
                    maxWidth = width;
                }
            } else {
                nextLine.setLength(words[i].length());
                nextLine.replace(0,
                                 words[i].length(),
                                 words[i]);
                numOfLines++;
            }
        }
        final double height = getBoundingBoxForString(textSupplier.get()).getHeight() * numOfLines;
        return new double[] {maxWidth, height};
    }

    @Override
    public void drawString(final Context2D context,
                           final IDrawString drawCommand) {
        final String[] words = text.getText().split("\\s");

        if (words.length < 1) {
            return;
        }

        final StringBuilder nextLine = new StringBuilder(words[0]);
        final ArrayList<String> lines = new ArrayList<>();
        for (int i = 1; i < words.length; i++) {
            if (getBoundingBoxForString(nextLine + " " + words[i]).getWidth() <= getWrapBoundaries().getWidth())
            {
                nextLine.append(" ").append(words[i]);
            } else {
                lines.add(nextLine.toString());
                nextLine.setLength(words[i].length());
                nextLine.replace(0, words[i].length(), words[i]);
            }
        }
        lines.add(nextLine.toString());

        drawLines(context, drawCommand, lines, wrapBoundaries.getWidth());
    }

    protected void drawLines(Context2D context, IDrawString drawCommand, List<String> lines, double boundariesWidth)
    {

        double xOffset = 0;

        switch (textAlignSupplier.get())
        {
        case START:
        case LEFT:
            xOffset = 0;
            break;

        case CENTER:
            xOffset = boundariesWidth / 2;
            break;

        case END:
        case RIGHT:
            xOffset = boundariesWidth;
            break;
        }

        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);

            if (line.length() == 0)
            {
                continue;
            }
            final int toPad = (int) Math
                    .round((boundariesWidth - getBoundingBoxForString(line).getWidth()) / getBoundingBoxForString(" ")
                            .getWidth());
            line = textUtils.padString(line, line.length() + toPad, ' ', textAlignSupplier.get());
            drawCommand.draw(context, line, xOffset, i + Y_OFFSET);
        }
    }
}
