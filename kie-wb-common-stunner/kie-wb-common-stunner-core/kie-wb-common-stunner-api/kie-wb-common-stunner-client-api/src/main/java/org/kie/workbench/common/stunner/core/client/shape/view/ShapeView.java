/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.view;

public interface ShapeView<T> {

    String UUID_PREFFIX = "stunner:";

    T setUUID( String uuid );

    String getUUID();

    double getShapeX();

    double getShapeY();

    T setShapeX( double x );

    T setShapeY( double y );

    String getFillColor();

    T setFillColor( String color );

    double getFillAlpha();

    T setFillAlpha( double alpha );

    String getStrokeColor();

    T setStrokeColor( String color );

    double getStrokeAlpha();

    T setStrokeAlpha( double alpha );

    double getStrokeWidth();

    T setStrokeWidth( double width );

    T setDragEnabled( boolean isDraggable );

    T moveToTop();

    T moveToBottom();

    T moveUp();

    T moveDown();

    T setZIndex( int zindez );

    int getZIndex();

    void removeFromParent();

    void destroy();

}
