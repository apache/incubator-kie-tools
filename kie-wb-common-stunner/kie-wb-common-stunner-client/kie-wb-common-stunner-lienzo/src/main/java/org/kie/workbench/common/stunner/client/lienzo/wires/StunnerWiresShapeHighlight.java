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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHighlightImpl;
import org.kie.workbench.common.stunner.client.lienzo.shape.animation.ShapeViewDecoratorAnimation;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.animation.Animation;
import org.kie.workbench.common.stunner.core.client.animation.AnimationHandle;
import org.uberfire.mvp.Command;

public class StunnerWiresShapeHighlight implements WiresShapeHighlight<PickerPart.ShapePart> {

    static final String HIGHLIGHT_COLOR = "#0000FF";
    static final double HIGHLIGHT_STROKE_PCT = 10d;
    static final double HIGHLIGHT_ALPHA = 1d;

    private final WiresShapeHighlightImpl delegate;
    private AnimationHandle restoreAnimationHandle;
    private Animation restoreAnimation;

    public StunnerWiresShapeHighlight(final WiresManager wiresManager) {
        this(new WiresShapeHighlightImpl(wiresManager.getDockingAcceptor().getHotspotSize()));
    }

    StunnerWiresShapeHighlight(final WiresShapeHighlightImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public void highlight(final WiresShape shape,
                          final PickerPart.ShapePart part) {
        switch (part) {
            case BODY:
                highlightBody(shape);
                break;
            default:
                highlightBorder(shape);
        }
    }

    @Override
    public void restore() {
        restoreAnimation(() -> {
        });
        delegate.restore();
    }

    Animation getRestoreAnimation() {
        return restoreAnimation;
    }

    private void highlightBody(final WiresShape shape) {
        if (shape instanceof LienzoShapeView) {
            highlightBody((LienzoShapeView) shape);
        } else {
            delegate.highlight(shape, PickerPart.ShapePart.BODY);
        }
    }

    void highlightBody(final LienzoShapeView view) {
        checkPreviousAnimation(view,
                               () -> runAnimation(view));
    }

    private void checkPreviousAnimation(final LienzoShapeView<?> view,
                                        final Command runnable) {
        if (null != restoreAnimation &&
                (null == restoreAnimationHandle || !restoreAnimationHandle.isRunning())) {
            if (view != restoreAnimation.getSource()) {
                restoreAnimation(runnable::execute);
            }
        } else if (null == restoreAnimation) {
            runnable.execute();
        }
    }

    private void runAnimation(final LienzoShapeView view) {
        final String strokeColor = view.getStrokeColor();
        final double strokeWidth = view.getStrokeWidth();
        final double strokeAlpha = view.getStrokeAlpha();
        restoreAnimation = new ShapeViewDecoratorAnimation(() -> view,
                                                           strokeColor,
                                                           strokeWidth,
                                                           strokeAlpha);
        final Animation highlightAnimation =
                new ShapeViewDecoratorAnimation(() -> view,
                                                HIGHLIGHT_COLOR,
                                                calculateStrokeWidth(strokeWidth),
                                                HIGHLIGHT_ALPHA);
        highlightAnimation.run();
    }

    static double calculateStrokeWidth(final double value) {
        return value + (value / 100 * HIGHLIGHT_STROKE_PCT);
    }

    private void highlightBorder(final WiresShape shape) {
        delegate.highlight(shape, PickerPart.ShapePart.BORDER);
    }

    private void restoreAnimation(Command onComplete) {
        if (null != restoreAnimation && null == restoreAnimationHandle) {
            restoreAnimation.setCallback(new Animation.AnimationCallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFrame() {
                }

                @Override
                public void onComplete() {
                    restoreAnimation = null;
                    restoreAnimationHandle = null;
                    onComplete.execute();
                }
            });
            restoreAnimationHandle = restoreAnimation.run();
        }
    }
}
