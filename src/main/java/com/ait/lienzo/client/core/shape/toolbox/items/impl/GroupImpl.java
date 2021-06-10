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

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;
import com.ait.lienzo.client.core.types.BoundingBox;

class GroupImpl extends AbstractGroupItem<GroupImpl> {

    private final Group primitive;

    GroupImpl(final Group group) {
        this(new GroupItem(group),
             group);
    }

    GroupImpl(final GroupItem groupItem,
              final Group group) {
        super(groupItem);
        this.primitive = group;
    }

    @Override
    public GroupImpl show(final Runnable before,
                          final Runnable after) {
        getGroupItem().show(new Runnable() {
                                @Override
                                public void run() {
                                    GroupImpl.this.showAddOns();
                                    before.run();
                                }
                            },
                            after);
        return this;
    }

    @Override
    public GroupImpl hide(final Runnable before,
                          final Runnable after) {
        getGroupItem().hide(before,
                            new Runnable() {
                                @Override
                                public void run() {
                                    GroupImpl.this.hideAddOns();
                                    after.run();
                                }
                            });
        return this;
    }

    @Override
    public IPrimitive<?> getPrimitive() {
        return primitive;
    }

    @Override
    public Supplier<BoundingBox> getBoundingBox() {
        return () -> {
            if (primitive.getChildNodes().size() == 0) {
                return BoundingBox.fromDoubles(0,
                                       0,
                                       1,
                                       1);
            }
            return GroupImpl.super.getBoundingBox().get();
        };
    }
}
