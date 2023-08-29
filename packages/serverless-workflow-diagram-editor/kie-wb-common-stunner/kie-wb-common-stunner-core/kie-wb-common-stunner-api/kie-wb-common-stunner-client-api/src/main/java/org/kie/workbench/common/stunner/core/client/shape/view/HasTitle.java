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


package org.kie.workbench.common.stunner.core.client.shape.view;

import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public interface HasTitle<T> {

    default T setTitle(final String title) {
        return (T) this;
    }

    default void setTitleBackgroundColor(String color) {
    }

    T setTitleAlpha(final double alpha);

    T setTitleFontFamily(final String fontFamily);

    T setTitleFontSize(final double fontSize);

    T setTitleFontColor(final String fillColor);

    T setTitleStrokeWidth(final double strokeWidth);

    String getTitleFontFamily();

    double getTitleFontSize();

    default Point2D getTitlePosition() {
        return null;
    }

    default void batch() {
    }

    default double getTextboxWidth() {
        return 0;
    }

    default double getTextboxHeight() {
        return 0;
    }

    default T setTitleStrokeAlpha(final double alpha) {
        return (T) this;
    }

    default T setTitleStrokeColor(final String color) {
        return (T) this;
    }

    default T moveTitleToTop() {
        return (T) this;
    }
}
