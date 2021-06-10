/*
   Copyright (c) 2019 Ahome' Innovation Technologies. All rights reserved.
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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;

/**
 * ITextWrapper implementation that truncates text and appends "..." if there is no space left.
 */
public class TextTruncateWrapper extends TextBoundsWrap
        implements ITextWrapperWithBoundaries
{
    private BoundingBox             m_wrapBoundaries;

    private  double m_margin = 20;

    public TextTruncateWrapper(final Text text,
                               final BoundingBox wrapBoundaries)
    {
        super(text);
        setWrapBoundaries(wrapBoundaries);
    }

    public BoundingBox getWrapBoundaries()
    {
        return m_wrapBoundaries;
    }

    @Override
    public void setWrapBoundaries(final BoundingBox boundaries)
    {
        m_wrapBoundaries = boundaries;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final double[] boundaries = calculateWrapBoundaries();
        return BoundingBox.fromDoubles(0, 0, boundaries[0], boundaries[1]);
    }

    protected double getWrapBoundariesWidth()
    {
        return m_wrapBoundaries.getWidth() - m_margin;
    }

    protected double[] calculateWrapBoundaries()
    {
        final String[] words = textSupplier.get().split("\\s");
        if (words.length < 1)
        {
            return new double[] { getWrapBoundaries().getX(), getWrapBoundaries().getY() };
        }

        final double wrapWidth = getWrapBoundariesWidth();
        final String firstWord = words[0];
        double width = getBoundingBoxForString(firstWord).getWidth();
        final StringBuilder nextLine = new StringBuilder(firstWord);
        int numOfLines = 1;
        for (int i = 1; i < words.length; i++)
        {
            width = getBoundingBoxForString(nextLine + " " + words[i]).getWidth();
            if (width <= wrapWidth)
            {
                nextLine.append(" ").append(words[i]);
            }
            else
            {
                nextLine.setLength(words[i].length());
                nextLine.replace(0, words[i].length(), words[i]);
                numOfLines++;
            }
        }

        final double height = getHeightByLines(numOfLines);
        return new double[] { width, height };
    }

    protected double getHeightByLines(int numOfLines)
    {
        final double lineHeight = getLineHeight();

        while (!hasVerticalSpace(numOfLines, lineHeight, getWrapBoundaries().getHeight() - (Y_OFFSET * numOfLines))
                && numOfLines >= 0)
        {
            numOfLines--;
        }

        return lineHeight * numOfLines;
    }

    protected boolean hasVerticalSpace(final int lineIndex,
                                       final double lineHeight,
                                       final double availableHeight)
    {
        return lineHeight * (lineIndex + Y_OFFSET) <= availableHeight;
    }

    protected double getLineHeight()
    {
        return getBoundingBoxForString("Mg").getHeight();
    }

    @Override
    public void drawString(final Context2D context,
                           final IDrawString drawCommand)
    {
        final String[] words = text.getText().split("\\s");

        if (words.length < 1)
        {
            return;
        }

        final ArrayList<String> lines = new ArrayList<>();
        final double boundariesWidth = getWrapBoundariesWidth();
        StringBuilder currentLine = new StringBuilder();
        String currentWord;
        final double lineHeight = getLineHeight();
        for (int i = 0; i < words.length; i++)
        {
            currentWord = words[i];

            if (hasHorizontalSpaceToDraw(currentLine.toString(), currentWord, boundariesWidth))
            {
                if (i + 1 < words.length
                        && getBoundingBoxForString(currentLine + currentWord + " " + words[i + 1]).getWidth() <= boundariesWidth)
                {
                    currentLine.append(currentWord).append(" ").append(words[i + 1]);
                    i++;

                    int j = i + 1;
                    while (j < words.length
                            && getBoundingBoxForString(currentLine + " " + words[j]).getWidth() <= boundariesWidth)
                    {

                        currentLine.append(" ").append(words[j]);
                        i++;
                        j++;
                    }
                }
                else
                {
                    currentLine.append(currentWord);
                }

                if (i != words.length - 1 && !hasVerticalSpace(lines.size() + 2, lineHeight,
                                                               getWrapBoundaries().getHeight() - (Y_OFFSET * lines.size() + 2)))
                {
                    if (currentLine.length() > 3)
                    {
                        currentLine.replace(currentLine.length() - 3, currentLine.length(), "...");
                    }
                    else
                    {
                        currentLine.append("...");
                    }

                    lines.add(currentLine.toString());
                    break;
                }

                lines.add(currentLine.toString());

                currentLine = new StringBuilder();
            }
            else
            {
                String newWord = currentWord;
                int indexOfRemovedChars = 0;
                while (!hasHorizontalSpaceToDraw(currentLine.toString(), newWord, boundariesWidth)
                        && indexOfRemovedChars >= 0)
                {
                    indexOfRemovedChars = newWord.length() - 1;
                    newWord = newWord.substring(0, indexOfRemovedChars);
                }

                currentLine.append(newWord);

                if (!hasVerticalSpace(lines.size() + 2, lineHeight,
                                      getWrapBoundaries().getHeight() - (Y_OFFSET * lines.size() + 2)))
                {
                    if (currentLine.length() > 3) {
                        currentLine = new StringBuilder(currentLine.substring(0, currentLine.length() - 3) + "...");
                    }
                    else
                    {
                        currentLine.append("...");
                    }

                    lines.add(currentLine.toString());
                    break;
                }

                lines.add(currentLine.toString());
                currentLine = new StringBuilder(currentWord.substring(indexOfRemovedChars) + " ");
            }

            if (i == words.length - 1 && currentLine.length() != 0)
            {
                final double currentLineWidth = getBoundingBoxForString(currentLine.toString()).getWidth();
                if (currentLineWidth > boundariesWidth)
                {
                    while (!hasHorizontalSpaceToDraw(currentLine.toString(), "", boundariesWidth)
                            && currentLine.length() > 0)
                    {
                        currentLine = new StringBuilder(currentLine.substring(0, currentLine.length() - 1));
                    }

                    currentLine = new StringBuilder(currentLine.substring(0, currentLine.length() - 3) + "...");
                }
                lines.add(currentLine.toString());
            }
        }
        drawLines(context, drawCommand, lines, boundariesWidth);
    }

    protected boolean hasHorizontalSpaceToDraw(final String currentLine,
                                               final String currentWord,
                                               final double boundariesWidth)
    {
        final BoundingBox currentWordSize = getBoundingBoxForString(currentWord);
        final BoundingBox currentLineSize = getBoundingBoxForString(currentLine);
        final double width = currentLineSize.getWidth() + currentWordSize.getWidth();
        return width <= boundariesWidth;
    }

    public double getMargin() {
        return m_margin;
    }

    public void setMargin(double m_margin) {
        this.m_margin = m_margin;
    }
}