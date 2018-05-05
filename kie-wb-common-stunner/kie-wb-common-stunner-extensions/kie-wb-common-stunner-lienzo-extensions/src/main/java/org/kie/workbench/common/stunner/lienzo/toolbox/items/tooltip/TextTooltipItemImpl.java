/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.lienzo.toolbox.items.tooltip;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Direction;

public class TextTooltipItemImpl
        implements TextTooltipItem<TextTooltipItemImpl> {

    private final Supplier<TextTooltipItem> textTooltipSupplier;
    private String text;
    private Direction at;
    private Direction towards;
    private Supplier<BoundingBox> boundingBoxSupplier;

    TextTooltipItemImpl(final Supplier<TextTooltipItem> textTooltipSupplier,
                        final String text) {
        this(textTooltipSupplier,
             text,
             Direction.EAST,
             Direction.EAST);
    }

    TextTooltipItemImpl(final Supplier<TextTooltipItem> textTooltipSupplier,
                        final String text,
                        final Direction at,
                        final Direction towards) {
        this.textTooltipSupplier = textTooltipSupplier;
        this.text = text;
        this.at = at;
        this.towards = towards;
    }

    @Override
    public TextTooltipItemImpl at(final Direction at) {
        this.at = at;
        return this;
    }

    @Override
    public TextTooltipItemImpl towards(final Direction towards) {
        this.towards = towards;
        return this;
    }

    @Override
    public TextTooltipItemImpl setText(final String text) {
        this.text = text;
        return this;
    }

    @Override
    public TextTooltipItemImpl forComputedBoundingBox(final Supplier<BoundingBox> boundingBoxSupplier) {
        this.boundingBoxSupplier = boundingBoxSupplier;
        return this;
    }

    @Override
    public TextTooltipItemImpl show() {
        hide();
        if (null != text && text.trim().length() > 0) {
            getTextTooltipItem()
                    .forComputedBoundingBox(boundingBoxSupplier)
                    .at(at)
                    .towards(towards)
                    .setText(text)
                    .show();
        }
        return this;
    }

    @Override
    public TextTooltipItemImpl hide() {
        getTextTooltipItem().hide();
        return this;
    }

    @Override
    public void destroy() {
        textTooltipSupplier.get().destroy();
        this.text = null;
        this.at = null;
        this.towards = null;
        this.boundingBoxSupplier = null;
    }

    Direction getAt() {
        return at;
    }

    Direction getTowards() {
        return towards;
    }

    @SuppressWarnings("unchecked")
    private TextTooltipItem<TextTooltipItem> getTextTooltipItem() {
        return textTooltipSupplier.get();
    }
}
