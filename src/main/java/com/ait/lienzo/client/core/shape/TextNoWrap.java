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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;

/**
 * ITextWrapper implementation that performs no wrapping.
 */
public class TextNoWrap implements ITextWrapper {

    protected final ITextWrapper.Supplier<String> textSupplier;
    protected final ITextWrapper.Supplier<Double> fontSizeSupplier;
    protected final ITextWrapper.Supplier<String> fontStyleSupplier;
    protected final ITextWrapper.Supplier<String> fontFamilySupplier;
    protected final ITextWrapper.Supplier<TextUnit> textUnitSupplier;
    protected final ITextWrapper.Supplier<TextBaseLine> textBaseLineSupplier;
    protected final ITextWrapper.Supplier<TextAlign> textAlignSupplier;

    public TextNoWrap(final Text text) {
        this(new ITextWrapper.Supplier<String>() {
                 @Override
                 public String get() {
                     return text.getText();
                 }
             },
             new ITextWrapper.Supplier<Double>() {
                 @Override
                 public Double get() {
                     return text.getFontSize();
                 }
             },
             new ITextWrapper.Supplier<String>() {
                 @Override
                 public String get() {
                     return text.getFontStyle();
                 }
             },
             new ITextWrapper.Supplier<String>() {
                 @Override
                 public String get() {
                     return text.getFontFamily();
                 }
             },
             new ITextWrapper.Supplier<TextUnit>() {
                 @Override
                 public TextUnit get() {
                     return text.getTextUnit();
                 }
             },
             new ITextWrapper.Supplier<TextBaseLine>() {
                 @Override
                 public TextBaseLine get() {
                     return text.getTextBaseLine();
                 }
             },
             new ITextWrapper.Supplier<TextAlign>() {
                 @Override
                 public TextAlign get() {
                     return text.getTextAlign();
                 }
             });
    }

    public TextNoWrap(final ITextWrapper.Supplier<String> textSupplier,
                      final ITextWrapper.Supplier<Double> fontSizeSupplier,
                      final ITextWrapper.Supplier<String> fontStyleSupplier,
                      final ITextWrapper.Supplier<String> fontFamilySupplier,
                      final ITextWrapper.Supplier<TextUnit> textUnitSupplier,
                      final ITextWrapper.Supplier<TextBaseLine> textBaseLineSupplier,
                      final ITextWrapper.Supplier<TextAlign> textAlignSupplier) {
        this.textSupplier = textSupplier;
        this.fontSizeSupplier = fontSizeSupplier;
        this.fontStyleSupplier = fontStyleSupplier;
        this.fontFamilySupplier = fontFamilySupplier;
        this.textUnitSupplier = textUnitSupplier;
        this.textBaseLineSupplier = textBaseLineSupplier;
        this.textAlignSupplier = textAlignSupplier;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return getBoundingBoxForString(textSupplier.get());
    }

    protected BoundingBox getBoundingBoxForString(final String string) {
        return TextUtils.getBoundingBox(string,
                                        fontSizeSupplier.get(),
                                        fontStyleSupplier.get(),
                                        fontFamilySupplier.get(),
                                        textUnitSupplier.get(),
                                        textBaseLineSupplier.get(),
                                        textAlignSupplier.get());
    }

    @Override
    public void drawString(final Context2D context,
                           final Attributes attr,
                           final IDrawString drawCommand) {
        drawCommand.draw(context,
                         attr.getText(),
                         0,
                         0);
    }
}
