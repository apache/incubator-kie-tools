/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class ShapeStateAttributeHandler<V extends ShapeView>
        implements ShapeStateHandler {

    private final BiConsumer<V, ShapeStateAttributes> stateAttributesApplier;
    private final ShapeStateAttributes stateHolder;
    private Function<ShapeState, ShapeStateAttributes> stateAttributesProvider;
    private ShapeState state;
    private Supplier<V> view;

    public static <V extends ShapeView> BiConsumer<V, ShapeStateAttributes> newDefaultStateApplier() {
        return (view1, attributes) -> attributes.consume((attr, value) -> attr.valueApplier.accept(view1, value));
    }

    public ShapeStateAttributeHandler() {
        this(newDefaultStateApplier());
    }

    public ShapeStateAttributeHandler(final BiConsumer<V, ShapeStateAttributes> stateAttributesApplier) {
        this.stateAttributesApplier = stateAttributesApplier;
        this.stateHolder = new ShapeStateAttributes();
        this.state = ShapeState.NONE;
    }

    public ShapeStateAttributeHandler<V> useAttributes(final Function<ShapeState, ShapeStateAttributes> stateAttributesProvider) {
        this.stateAttributesProvider = stateAttributesProvider;
        return this;
    }

    public ShapeStateAttributeHandler<V> setView(final Supplier<V> viewSupplier) {
        setViewSupplier(viewSupplier);
        this.state = ShapeState.NONE;
        saveState();
        return this;
    }

    @Override
    public ShapeStateAttributeHandler<V> shapeAttributesChanged() {
        if (state.equals(ShapeState.NONE)) {
            saveState();
        }
        return this;
    }

    private void saveState() {
        this.stateHolder.store(view.get());
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        if (!this.state.equals(shapeState)) {
            this.state = shapeState;
            // Clone the state holder.
            final ShapeStateAttributes stateHolder = this.stateHolder.copy();
            // Apply the attributes for the given state to the new instance.
            stateAttributesProvider
                    .apply(shapeState)
                    .consume(stateHolder.values::put);
            // Apply the holder attributes to the view.
            stateAttributesApplier.accept(getShapeView(),
                                          stateHolder);
        }
    }

    @Override
    public ShapeState reset() {
        final ShapeState result = this.state;
        newDefaultStateApplier()
                .accept(getShapeView(),
                        stateHolder.copy());
        this.state = ShapeState.NONE;
        return result;
    }

    @Override
    public ShapeState getShapeState() {
        return state;
    }

    public V getShapeView() {
        return view.get();
    }

    public enum ShapeStateAttribute {

        FILL_COLOR(ShapeView::getFillColor, (view, color) -> view.setFillColor((String) color)),
        FILL_ALPHA(ShapeView::getFillAlpha, (view, value) -> view.setFillAlpha((double) value)),
        STROKE_COLOR(ShapeView::getStrokeColor, (view, color) -> view.setStrokeColor((String) color)),
        STROKE_ALPHA(ShapeView::getStrokeAlpha, (view, value) -> view.setStrokeAlpha((double) value)),
        STROKE_WIDTH(ShapeView::getStrokeWidth, (view, value) -> view.setStrokeWidth((double) value));

        private final Function<ShapeView<?>, Object> valueProvider;
        private final BiConsumer<ShapeView<?>, Object> valueApplier;

        ShapeStateAttribute(final Function<ShapeView<?>, Object> valueProvider,
                            final BiConsumer<ShapeView<?>, Object> valueApplier) {
            this.valueProvider = valueProvider;
            this.valueApplier = valueApplier;
        }
    }

    public static class ShapeStateAttributes {

        private final Map<ShapeStateAttribute, Object> values;

        ShapeStateAttributes() {
            this(new HashMap<>(ShapeStateAttribute.values().length));
            for (ShapeStateAttribute attribute : ShapeStateAttribute.values()) {
                add(attribute);
            }
        }

        private ShapeStateAttributes(final Map<ShapeStateAttribute, Object> values) {
            this.values = values;
        }

        private ShapeStateAttributes add(final ShapeStateAttribute attribute) {
            this.values.put(attribute, null);
            return this;
        }

        public ShapeStateAttributes set(final ShapeStateAttribute attribute,
                                        final Object value) {
            this.values.put(attribute, value);
            return this;
        }

        public ShapeStateAttributes unset(final ShapeStateAttribute attribute) {
            this.values.put(attribute, null);
            return this;
        }

        public ShapeStateAttributes copy() {
            return new ShapeStateAttributes(new HashMap<>(values));
        }

        private ShapeStateAttributes store(final ShapeView<?> view) {
            new HashSet<>(values.keySet())
                    .forEach(attr -> values.put(attr, attr.valueProvider.apply(view)));
            return this;
        }

        public ShapeStateAttributes consume(final BiConsumer<ShapeStateAttribute, Object> attribute) {
            values.forEach((key, value) -> {
                        if (null != value) {
                            attribute.accept(key, value);
                        }
                    });
            return this;
        }

        public Map<ShapeStateAttribute, Object> getValues() {
            return values;
        }
    }

    void setViewSupplier(final Supplier<V> view) {
        this.view = view;
    }

    ShapeStateAttributes getStateHolder() {
        return stateHolder;
    }
}
