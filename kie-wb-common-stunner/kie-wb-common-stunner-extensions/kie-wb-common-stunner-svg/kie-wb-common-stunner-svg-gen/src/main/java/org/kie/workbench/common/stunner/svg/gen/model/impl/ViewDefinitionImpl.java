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

package org.kie.workbench.common.stunner.svg.gen.model.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.model.LayoutDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.TransformDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;

public class ViewDefinitionImpl implements ViewDefinition<SVGShapeView> {

    private final PrimitiveDefinition main;
    private final List<PrimitiveDefinition> children = new LinkedList<>();
    private final List<ViewRefDefinition> viewRefDefinitions = new LinkedList<>();
    private final String id;
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final ViewBoxDefinition viewBox;
    private TransformDefinition transformDefinition;
    private String name;
    private String path;

    public ViewDefinitionImpl(final String id,
                              final double x,
                              final double y,
                              final double width,
                              final double height,
                              final ViewBoxDefinition viewBox,
                              final PrimitiveDefinition main,
                              final PrimitiveDefinition... children) {
        this.viewBox = viewBox;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.main = main;
        if (null != children) {
            Collections.addAll(this.children,
                               children);
        }
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public ViewBoxDefinition getViewBox() {
        return viewBox;
    }

    @Override
    public PrimitiveDefinition getMain() {
        return main;
    }

    @Override
    public List<PrimitiveDefinition> getChildren() {
        return children;
    }

    @Override
    public List<ViewRefDefinition> getSVGViewRefs() {
        return viewRefDefinitions;
    }

    @Override
    public Class<SVGShapeView> getViewType() {
        return SVGShapeView.class;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getAlpha() {
        return 1;
    }

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean isMain() {
        return false;
    }

    @Override
    public boolean isListening() {
        return false;
    }

    @Override
    public LayoutDefinition getLayoutDefinition() {
        return LayoutDefinition.NONE;
    }

    @Override
    public TransformDefinition getTransformDefinition() {
        return transformDefinition;
    }

    public void setTransformDefinition(final TransformDefinition transformDefinition) {
        this.transformDefinition = transformDefinition;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return this.getClass().getName()
                + " [id=" + id + "]"
                + " [x=" + x + "]"
                + " [y =" + y + "]"
                + " [width=" + width + "]"
                + " [height=" + height + "]"
                + " [main=" + main + "]"
                + " [children={" + children + "}]";
    }
}
