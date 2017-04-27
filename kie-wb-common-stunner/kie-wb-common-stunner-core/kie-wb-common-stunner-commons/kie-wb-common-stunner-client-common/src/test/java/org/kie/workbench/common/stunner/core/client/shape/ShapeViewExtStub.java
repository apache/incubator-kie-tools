/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.core.client.shape.view.HasDecorators;
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class ShapeViewExtStub
        extends ShapeViewStub
        implements ShapeView<Object>,
                   HasDecorators<Object>,
                   HasFillGradient<Object>,
                   HasTitle<Object>,
                   HasSize<Object>,
                   HasRadius<Object> {

    private final List<Object> decorators = new ArrayList<>();

    @Override
    public Object setFillGradient(final Type type,
                                  final String startColor,
                                  final String endColor) {
        return this;
    }

    @Override
    public Object setTitle(final String title) {
        return this;
    }

    @Override
    public Object setTitlePosition(final Position position) {
        return this;
    }

    @Override
    public Object setTitleRotation(final double degrees) {
        return this;
    }

    @Override
    public Object setTitleStrokeColor(final String color) {
        return this;
    }

    @Override
    public Object setTitleFontFamily(final String fontFamily) {
        return this;
    }

    @Override
    public Object setTitleFontSize(final double fontSize) {
        return this;
    }

    @Override
    public Object setTitleStrokeWidth(final double strokeWidth) {
        return this;
    }

    @Override
    public Object setTitleAlpha(final double alpha) {
        return this;
    }

    @Override
    public Object moveTitleToTop() {
        return this;
    }

    @Override
    public Object setRadius(final double radius) {
        return this;
    }

    @Override
    public Object setSize(final double width,
                          final double height) {
        return this;
    }

    @Override
    public List<Object> getDecorators() {
        return decorators;
    }
}
