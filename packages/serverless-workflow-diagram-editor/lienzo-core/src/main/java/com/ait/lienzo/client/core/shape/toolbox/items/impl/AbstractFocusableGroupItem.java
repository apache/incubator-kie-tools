/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package com.ait.lienzo.client.core.shape.toolbox.items.impl;

import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;
import com.ait.lienzo.client.core.shape.toolbox.ToolboxVisibilityExecutors;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.gwtproject.timer.client.Timer;

public abstract class AbstractFocusableGroupItem<T extends AbstractFocusableGroupItem>
        extends AbstractGroupItem<T> {

    private static final int FOCUS_DELAY_MILLIS = 50;
    static final double ALPHA_FOCUSED = 1d;
    static final double ALPHA_UNFOCUSED = 0.75d;

    private FocusGroupExecutor focusGroupExecutor;
    private int focusDelay;
    private int unFocusDelay;

    private HandlerRegistration mouseEnterHandler;
    private HandlerRegistration mouseExitHandler;

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
    public T show(final Runnable before,
                  final Runnable after) {
        getGroupItem().show(before,
                            after);
        return cast();
    }

    @Override
    public T hide(final Runnable before,
                  final Runnable after) {
        cancelTimers();
        getGroupItem().hide(new Runnable() {
                                @Override
                                public void run() {
                                    AbstractFocusableGroupItem.this.unFocus();
                                    before.run();
                                }
                            },
                            after);
        return cast();
    }

    @Override
    public void destroy() {
        cancelTimers();

        if (null != mouseEnterHandler) {
            mouseEnterHandler.removeHandler();
            mouseEnterHandler = null;
        }

        if (null != mouseExitHandler) {
            mouseExitHandler.removeHandler();
            mouseExitHandler = null;
        }

        super.destroy();
    }

    void cancelTimers() {
        cancelFocusTimer();
        cancelUnFocusTimer();
    }

    protected T setupFocusingHandlers() {
        mouseEnterHandler = registerMouseEnterHandler(new NodeMouseEnterHandler() {
            @Override
            public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                AbstractFocusableGroupItem.this.focus();
            }
        });

        mouseExitHandler = registerMouseExitHandler(new NodeMouseExitHandler() {
            @Override
            public void onNodeMouseExit(NodeMouseExitEvent event) {
                AbstractFocusableGroupItem.this.unFocus();
            }
        });
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
                   new Runnable() {
                       @Override
                       public void run() {
                       }
                   });
        }

        public void unFocus() {
            hideAddOns();
            setAlpha(ALPHA_UNFOCUSED);
            accept(asPrimitive(),
                   new Runnable() {
                       @Override
                       public void run() {
                       }
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
