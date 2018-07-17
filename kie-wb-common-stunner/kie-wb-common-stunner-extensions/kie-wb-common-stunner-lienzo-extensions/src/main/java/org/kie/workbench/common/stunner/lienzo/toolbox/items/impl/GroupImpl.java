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

package org.kie.workbench.common.stunner.lienzo.toolbox.items.impl;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.lienzo.toolbox.GroupItem;

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
        getGroupItem().show(() -> {
                                showAddOns();
                                before.run();
                            },
                            after);
        return this;
    }

    @Override
    public GroupImpl hide(final Runnable before,
                          final Runnable after) {
        getGroupItem().hide(before,
                            () -> {
                                hideAddOns();
                                after.run();
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
                return new BoundingBox(0,
                                       0,
                                       1,
                                       1);
            }
            return GroupImpl.super.getBoundingBox().get();
        };
    }
}
