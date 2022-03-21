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

package com.ait.lienzo.client.core.shape.toolbox;

import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;

public class GroupItem extends AbstractItem<GroupItem, Group> implements Item<GroupItem> {

    private static final Runnable NO_OP = new Runnable() {
        @Override
        public void run() {
        }
    };

    private final Group group;
    private BiConsumer<Group, Runnable> showExecutor;
    private BiConsumer<Group, Runnable> hideExecutor;

    public GroupItem() {
        this(new Group());
    }

    public GroupItem(final Group group) {
        this.group = group;
        this.showExecutor = ToolboxVisibilityExecutors.alpha(1);
        this.hideExecutor = ToolboxVisibilityExecutors.alpha(0);
        group.setAlpha(0);
    }

    public GroupItem useShowExecutor(final BiConsumer<Group, Runnable> executor) {
        this.showExecutor = executor;
        return this;
    }

    public GroupItem useHideExecutor(final BiConsumer<Group, Runnable> executor) {
        this.hideExecutor = executor;
        return this;
    }

    @Override
    public GroupItem show() {
        return show(NO_OP, NO_OP);
    }

    @Override
    public GroupItem hide() {
        return hide(NO_OP, NO_OP);
    }

    public GroupItem add(final IPrimitive<?> iPrimitive) {
        group.add(iPrimitive);
        return this;
    }

    public GroupItem remove(final IPrimitive<?> iPrimitive) {
        group.remove(iPrimitive);
        return this;
    }

    public GroupItem show(final Runnable before,
                          final Runnable after) {
        if (!isVisible()) {
            before.run();
            doShow(after);
        }
        return this;
    }

    public GroupItem hide(final Runnable before,
                          final Runnable after) {
        if (isVisible()) {
            before.run();
            doHide(after);
        }
        return this;
    }

    public boolean isVisible() {
        return group.getAlpha() > 0;
    }

    @Override
    public void destroy() {
        group.destroy();
    }

    @Override
    public Group asPrimitive() {
        return group;
    }

    private void doShow(final Runnable callback) {
        showExecutor.accept(group,
                            callback);
    }

    private void doHide(final Runnable callback) {
        hideExecutor.accept(group,
                            callback);
    }
}
