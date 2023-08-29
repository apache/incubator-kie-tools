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


package org.kie.workbench.common.stunner.svg.gen.model.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.model.LayoutDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.TransformDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;

public class ViewDefinitionImpl implements ViewDefinition<SVGShapeView> {

    private final ShapeDefinition main;
    private final List<PrimitiveDefinition> children = new LinkedList<>();
    private final List<ViewRefDefinition> viewRefDefinitions = new LinkedList<>();
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final ViewBoxDefinition viewBox;
    private final StyleSheetDefinition globalStyleSheetDefinition;
    private TransformDefinition transformDefinition;
    private String id;
    private String factoryMethodName;
    private String path;
    private final Map<String, String> staticFields;

    public ViewDefinitionImpl(final String id,
                              final double x,
                              final double y,
                              final double width,
                              final double height,
                              final StyleSheetDefinition globalStyleSheetDefinition,
                              final ViewBoxDefinition viewBox,
                              final ShapeDefinition main,
                              final PrimitiveDefinition... children) {
        this.viewBox = viewBox;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.main = main;
        this.globalStyleSheetDefinition = globalStyleSheetDefinition;
        this.staticFields = new LinkedHashMap<>();
        if (null != children) {
            Collections.addAll(this.children,
                               children);
        }
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setFactoryMethodName(final String name) {
        this.factoryMethodName = name;
    }

    @Override
    public String getFactoryMethodName() {
        return factoryMethodName;
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
    public ShapeDefinition getMain() {
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

    public StyleSheetDefinition getGlobalStyleSheetDefinition() {
        return globalStyleSheetDefinition;
    }

    public Map<String, String> getStaticFields() {
        return staticFields;
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
