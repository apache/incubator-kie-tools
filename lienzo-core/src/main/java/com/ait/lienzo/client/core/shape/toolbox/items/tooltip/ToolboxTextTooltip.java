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

package com.ait.lienzo.client.core.shape.toolbox.items.tooltip;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.toolbox.items.LayerToolbox;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Direction;

public class ToolboxTextTooltip implements TextTooltipItem<ToolboxTextTooltip> {

    private final PrimitiveTextTooltip tooltip;
    private final TextTooltipItemImpl delegate;

    public ToolboxTextTooltip(final LayerToolbox toolbox) {
        this.tooltip = PrimitiveTextTooltip.Builder.build("");
        this.delegate = new ToolboxTextItem();
        attachTo(toolbox);
    }

    ToolboxTextTooltip(final LayerToolbox toolbox,
                       final PrimitiveTextTooltip tooltip,
                       final TextTooltipItemImpl delegate) {
        this.tooltip = tooltip;
        this.delegate = delegate;
        attachTo(toolbox);
    }

    public TextTooltipItemImpl createItem(final String text) {
        return new TextTooltipItemImpl(new Supplier<TextTooltipItem>() {
            @Override
            public TextTooltipItem get() {
                return tooltip;
            }
        },
                                       text,
                                       delegate.getAt(),
                                       delegate.getTowards());
    }

    @Override
    public ToolboxTextTooltip at(final Direction at) {
        delegate.at(at);
        return this;
    }

    @Override
    public ToolboxTextTooltip towards(final Direction towards) {
        delegate.towards(towards);
        return this;
    }

    @Override
    public ToolboxTextTooltip setText(final String text) {
        delegate.setText(text);
        return this;
    }

    public ToolboxTextTooltip withText(final Consumer<Text> textConsumer) {
        tooltip.withText(textConsumer);
        return this;
    }

    @Override
    public ToolboxTextTooltip forComputedBoundingBox(final Supplier<BoundingBox> boundingBoxSupplier) {
        delegate.forComputedBoundingBox(boundingBoxSupplier);
        return this;
    }

    @Override
    public ToolboxTextTooltip show() {
        delegate.show();
        return this;
    }

    @Override
    public ToolboxTextTooltip hide() {
        delegate.hide();
        return this;
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

    private ToolboxTextTooltip attachTo(final LayerToolbox toolbox) {
        toolbox
                .getLayer()
                .add(tooltip.asPrimitive());
        return this;
    }

    private class ToolboxTextItem extends TextTooltipItemImpl {

        private ToolboxTextItem() {
            super(new Supplier<TextTooltipItem>() {
                      @Override
                      public TextTooltipItem get() {
                          return tooltip;
                      }
                  },
                  "");
        }
    }
}
