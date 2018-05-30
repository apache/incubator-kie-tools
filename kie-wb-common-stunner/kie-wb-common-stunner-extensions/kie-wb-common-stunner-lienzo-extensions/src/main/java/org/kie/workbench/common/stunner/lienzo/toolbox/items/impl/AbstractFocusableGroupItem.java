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

import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.lienzo.toolbox.GroupItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.ToolboxVisibilityExecutors;
import org.uberfire.mvp.Command;

public abstract class AbstractFocusableGroupItem<T extends AbstractFocusableGroupItem>
        extends AbstractGroupItem<T> {

    private final static int FOCUS_DELAY_MILLIS = 50;
    static final double ALPHA_FOCUSED = 1d;
    static final double ALPHA_UNFOCUSED = 0.75d;

    private FocusGroupExecutor focusGroupExecutor;
    private int focusDelay;
    private int unFocusDelay;

    private final Timer focusDelayTimer = new Timer() {
        @Override
        public void run() {
            cancelUnFocusTimer();
            doFocus();
        }
    };
    private final Timer unFocusDelayTimer = new Timer() {
        @Override
        public void run() {
            cancelFocusTimer();
            doUnFocus();
        }
    };

    protected AbstractFocusableGroupItem(final GroupItem groupItem) {
        super(groupItem);
        this.focusDelay = FOCUS_DELAY_MILLIS;
        this.unFocusDelay = 0;
        useFocusGroupExecutor(new FocusGroupExecutor());
    }

    T focus() {
        cancelFocusTimer();
        if (focusDelay > 0) {
            focusDelayTimer.schedule(focusDelay);
        } else {
            focusDelayTimer.run();
        }
        return cast();
    }

    T unFocus() {
        cancelUnFocusTimer();
        if (unFocusDelay > 0) {
            unFocusDelayTimer.schedule(unFocusDelay);
        } else {
            unFocusDelayTimer.run();
        }
        return cast();
    }

    public T setFocusDelay(final int delay) {
        this.focusDelay = delay;
        return cast();
    }

    public T setUnFocusDelay(final int delay) {
        this.unFocusDelay = delay;
        return cast();
    }

    @Override
    public T show(final Command before,
                  final Command after) {
        getGroupItem().show(before,
                            after);
        return cast();
    }

    @Override
    public T hide(final Command before,
                  final Command after) {
        cancelTimers();
        getGroupItem().hide(() -> {
                                unFocus();
                                before.execute();
                            },
                            after);
        return cast();
    }

    @Override
    public void destroy() {
        cancelTimers();
        super.destroy();
    }

    void cancelTimers() {
        cancelFocusTimer();
        cancelUnFocusTimer();
    }

    protected T setupFocusingHandlers() {
        registerMouseEnterHandler(event -> focus());
        registerMouseExitHandler(event -> unFocus());
        return cast();
    }

    private void doFocus() {
        focusGroupExecutor.focus();
    }

    private void doUnFocus() {
        focusGroupExecutor.unFocus();
    }

    private void cancelFocusTimer() {
        focusDelayTimer.cancel();
    }

    private void cancelUnFocusTimer() {
        unFocusDelayTimer.cancel();
    }

    public class FocusGroupExecutor
            extends ToolboxVisibilityExecutors.AnimatedAlphaGroupExecutor {

        public FocusGroupExecutor() {
            super(ALPHA_UNFOCUSED);
            setAnimationTweener(AnimationTweener.LINEAR);
        }

        public void focus() {
            showAddOns();
            setAlpha(ALPHA_FOCUSED);
            accept(asPrimitive(),
                   () -> {
                   });
        }

        public void unFocus() {
            hideAddOns();
            setAlpha(ALPHA_UNFOCUSED);
            accept(asPrimitive(),
                   () -> {
                   });
        }
    }

    T useFocusGroupExecutor(final FocusGroupExecutor focusGroupExecutor) {
        this.focusGroupExecutor = focusGroupExecutor;
        return useShowExecutor(focusGroupExecutor);
    }

    FocusGroupExecutor getFocusGroupExecutor() {
        return focusGroupExecutor;
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
