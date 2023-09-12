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


package org.kie.workbench.common.stunner.client.lienzo.shape.impl;

import java.util.LinkedList;
import java.util.List;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Shape;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.MultipleAnimationHandle;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttributes;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHandler;
import org.uberfire.mvp.Command;

import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.FILL_ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.FILL_COLOR;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_COLOR;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_WIDTH;

public class ShapeStateAttributeAnimationHandler<V extends LienzoShapeView>
        implements ShapeStateHandler {

    private static final long ANIMATION_DURATION = 50L;

    private final ShapeStateAttributeHandler<V> handler;
    private IAnimationHandle animationHandle;
    private Command completeCallback;

    public ShapeStateAttributeAnimationHandler() {
        this.handler = new ShapeStateAttributeHandler<>(this::applyState);
        this.completeCallback = () -> {
        };
    }

    public ShapeStateAttributeHandler<V> getAttributesHandler() {
        return handler;
    }

    public ShapeStateAttributeAnimationHandler<V> onComplete(final Command completeCallback) {
        this.completeCallback = completeCallback;
        return this;
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        final ShapeState currentState = getShapeState();
        if (!shapeState.equals(currentState)) {
            if (null != animationHandle) {
                animationHandle.stop();
                setAnimationHandle(null);
            }
            handler.applyState(shapeState);
        }
    }

    @Override
    public ShapeStateAttributeAnimationHandler<V> shapeAttributesChanged() {
        handler.shapeAttributesChanged();
        return this;
    }

    @Override
    public ShapeState reset() {
        if (null != animationHandle) {
            animationHandle.stop();
            setAnimationHandle(null);
        }
        return handler.reset();
    }

    @Override
    public ShapeState getShapeState() {
        return handler.getShapeState();
    }

    @SuppressWarnings("unchecked")
    private void applyState(final V view,
                            final ShapeStateAttributes attributes) {
        final List<IAnimationHandle> handles = new LinkedList<>();
        view.getDecorators().forEach(dec -> handles.add(animate((Shape<?>) dec,
                                                                attributes,
                                                                ANIMATION_DURATION)));
        setAnimationHandle(new MultipleAnimationHandle(handles));
    }

    private IAnimationHandle animate(final com.ait.lienzo.client.core.shape.Shape<?> shape,
                                     final ShapeStateAttributes attributes,
                                     final long duration) {
        final AnimationProperties properties = new AnimationProperties();
        attributes.consume((attr, value) -> {
            switch (attr) {
                case FILL_COLOR:
                    properties.push(FILL_COLOR((String) value));
                    break;
                case FILL_ALPHA:
                    properties.push(FILL_ALPHA((double) value));
                    break;
                case STROKE_COLOR:
                    properties.push(STROKE_COLOR((String) value));
                    break;
                case STROKE_ALPHA:
                    properties.push(STROKE_ALPHA((double) value));
                    break;
                case STROKE_WIDTH:
                    properties.push(STROKE_WIDTH((double) value));
                    break;
            }
        });
        return shape.animate(
                AnimationTweener.LINEAR,
                properties,
                duration,
                new AnimationCallback() {
                    @Override
                    public void onClose(final IAnimation animation,
                                        final IAnimationHandle handle) {
                        super.onClose(animation, handle);
                        if (null != animationHandle && !animationHandle.isRunning()) {
                            setAnimationHandle(null);
                            completeCallback.execute();
                        }
                    }
                });
    }

    void setAnimationHandle(final IAnimationHandle animationHandle) {
        this.animationHandle = animationHandle;
    }
}
