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

package com.ait.lienzo.client.core.shape.toolbox.items.impl;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.ActionItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.ItemsToolbox;

public class ItemsToolboxHighlight {

    private final ItemsToolbox toolbox;
    private final ItemsToolboxHighlightDecorator decorator;

    static class ItemsToolboxHighlightDecorator {

        private IPrimitive<?> decorator;

        ItemsToolboxHighlightDecorator() {
            this.decorator = null;
        }

        public void highlight(final DecoratedItem item) {
            destroyDecorator();
            final AbstractGroupItem<?> group = getGroup(item);
            decorator = null != group ? cloneDecorator(group) : null;
            if (null != decorator) {
                group.add(decorator);
                group.asPrimitive().getLayer().batch();
            }
        }

        private static AbstractGroupItem getGroup(final DecoratedItem item) {
            AbstractGroupItem gi = null;
            if (item instanceof WrappedItem) {
                final AbstractDecoratedItem wrapped = ((WrappedItem) item).getWrapped();
                if (wrapped instanceof AbstractGroupItem) {
                    gi = (AbstractGroupItem) wrapped;
                }
            } else if (item instanceof AbstractGroupItem) {
                gi = (AbstractGroupItem) item;
            }
            return gi;
        }

        private static IPrimitive<?> cloneDecorator(final AbstractGroupItem item) {
            DecoratorItem decoratorItem = item.getDecorator();
            if (decoratorItem instanceof AbstractDecoratorItem) {
                AbstractDecoratorItem di = (AbstractDecoratorItem) decoratorItem;
                AbstractDecoratorItem copied = di.copy();
                copied.show();
                return (IPrimitive<?>) copied.asPrimitive();
            }
            return null;
        }

        public void restore() {
            destroyDecorator();
        }

        private void destroyDecorator() {
            if (null != decorator) {
                decorator.removeFromParent();
                decorator = null;
            }
        }
    }

    public ItemsToolboxHighlight(final ItemsToolbox toolbox) {
        this.toolbox = toolbox;
        this.decorator = new ItemsToolboxHighlightDecorator();
    }

    public static void restore(final ItemsToolbox toolbox) {
        consumeActions(toolbox, ActionItem::enable);
    }

    public void highlight(final DecoratedItem item) {
        consumeActions(toolbox, action -> {
            if (action == item) {
                action.enable();
                decorator.restore();
                decorator.highlight(action);
            } else {
                action.disable();
            }
        });
    }

    public void restore() {
        decorator.restore();
        restore(toolbox);
    }

    @SuppressWarnings("all")
    private static void consumeActions(final ItemsToolbox toolbox,
                                       final Consumer<ActionItem> itemConsumer) {
        if (null != toolbox.iterator()) {
            for (DecoratedItem item : toolbox) {
                if (item instanceof ActionItem) {
                    itemConsumer.accept((ActionItem) item);
                }
            }
        }
    }
}
