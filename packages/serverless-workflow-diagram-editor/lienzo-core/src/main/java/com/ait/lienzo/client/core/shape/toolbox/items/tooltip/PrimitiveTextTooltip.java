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

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.toolbox.Positions;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractPrimitiveItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Direction;

public class PrimitiveTextTooltip
        extends AbstractPrimitiveItem<PrimitiveTextTooltip>
        implements TextTooltipItem<PrimitiveTextTooltip> {

    static final Direction DEFAULT_AT = Direction.EAST;
    static final Direction DEFAULT_TOWARDS = Direction.EAST;

    private final Tooltip tooltip;
    private BoundingLocationExecutor locationExecutor;

    public static class Builder {

        public static PrimitiveTextTooltip atEast(final String text) {
            return build(text)
                    .at(Direction.EAST)
                    .towards(Direction.EAST);
        }

        public static PrimitiveTextTooltip build(final String text) {
            return new PrimitiveTextTooltip(text);
        }
    }

    PrimitiveTextTooltip(final String text) {
        this(text,
             new Tooltip(),
             new BoundingLocationExecutor());
    }

    PrimitiveTextTooltip(final String text,
                         final Tooltip tooltip,
                         final BoundingLocationExecutor locationExecutor) {
        this.tooltip = tooltip
                .withText((Consumer<Text>) textPrim -> textPrim.setText(text));
        this.locationExecutor = locationExecutor
                .at(DEFAULT_AT);
        this.towards(DEFAULT_TOWARDS);
    }

    @Override
    public PrimitiveTextTooltip setText(final String text) {
        return withText(new Consumer<Text>() {
            @Override
            public void accept(Text textPrim) {
                textPrim.setText(text);
            }
        });
    }

    public PrimitiveTextTooltip withText(final Consumer<Text> textConsumer) {
        this.tooltip.withText(textConsumer);
        return this;
    }

    public PrimitiveTextTooltip at(final Direction at) {
        this.locationExecutor
                .at(at)
                .accept(tooltip);
        return this;
    }

    public PrimitiveTextTooltip towards(final Direction towards) {
        this.tooltip.setDirection(towards);
        return this;
    }

    public PrimitiveTextTooltip setPadding(final double padding) {
        this.tooltip.setPadding(padding);
        return this;
    }

    @Override
    public PrimitiveTextTooltip forComputedBoundingBox(final Supplier<BoundingBox> boundingBoxSupplier) {
        this.locationExecutor
                .forBoundingBox(boundingBoxSupplier)
                .accept(tooltip);
        return this;
    }

    @Override
    public PrimitiveTextTooltip show() {
        tooltip.show();
        return this;
    }

    @Override
    public PrimitiveTextTooltip hide() {
        tooltip.hide();
        return this;
    }

    @Override
    public void destroy() {
        tooltip.destroy();
    }

    @Override
    public IPrimitive<?> asPrimitive() {
        return tooltip.asPrimitive();
    }

    BoundingLocationExecutor getLocationExecutor() {
        return locationExecutor;
    }

    public static class BoundingLocationExecutor implements Consumer<Tooltip> {

        private Supplier<BoundingBox> boundingBoxSupplier;
        private Direction at;

        public BoundingLocationExecutor at(final Direction at) {
            this.at = at;
            return this;
        }

        public BoundingLocationExecutor forBoundingBox(final Supplier<BoundingBox> supplier) {
            this.boundingBoxSupplier = supplier;
            return this;
        }

        @Override
        public void accept(final Tooltip tooltip) {
            final BoundingBox bb = boundingBoxSupplier.get();
            tooltip.setLocation(Positions.anchorFor(bb,
                                                    this.at));
        }
    }
}
