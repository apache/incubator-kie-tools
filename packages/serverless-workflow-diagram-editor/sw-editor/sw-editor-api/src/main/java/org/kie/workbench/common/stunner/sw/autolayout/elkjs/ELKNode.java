/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.autolayout.elkjs;

import elemental2.core.JsArray;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class ELKNode {

    @JsProperty
    String id;
    @JsProperty
    Object layoutOptions;
    @JsProperty
    double width;
    @JsProperty
    double height;
    @JsProperty
    double x;
    @JsProperty
    double y;
    @JsProperty
    JsArray<ELKNode> children;
    @JsProperty
    JsArray<ELKEdge> edges;

    @JsIgnore
    public ELKNode(String id,
                   double width,
                   double height) {
        this(id,
             new JsArray<>(),
             0d,
             0d,
             width,
             height,
             new JsArray<>(),
             new JsArray<>());
    }

    @JsIgnore
    public ELKNode(String id,
                   Object layoutOptions) {
        this(id,
             layoutOptions,
             0d,
             0d,
             0d,
             0d,
             new JsArray<>(),
             new JsArray<>());
    }

    @JsIgnore
    public ELKNode(String id,
                   Object layoutOptions,
                   JsArray<ELKNode> children,
                   JsArray<ELKEdge> edges) {
        this(id,
             layoutOptions,
             0d,
             0d,
             0d,
             0d,
             children,
             edges);
    }

    @JsIgnore
    public ELKNode(String id,
                   Object layoutOptions,
                   double x,
                   double y,
                   double width,
                   double height,
                   JsArray<ELKNode> children,
                   JsArray<ELKEdge> edges) {
        this.id = id;
        this.layoutOptions = layoutOptions;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.children = children;
        this.edges = edges;
    }

    @JsIgnore
    public String getId() {
        return id;
    }

    @JsIgnore
    public void setId(String id) {
        this.id = id;
    }

    @JsIgnore
    public double getWidth() {
        return width;
    }

    @JsIgnore
    public void setWidth(double width) {
        this.width = width;
    }

    @JsIgnore
    public double getHeight() {
        return height;
    }

    @JsIgnore
    public void setHeight(double height) {
        this.height = height;
    }

    @JsIgnore
    public double getX() {
        return x;
    }

    @JsIgnore
    public void setX(double x) {
        this.x = x;
    }

    @JsIgnore
    public double getY() {
        return y;
    }

    @JsIgnore
    public void setY(double y) {
        this.y = y;
    }

    @JsIgnore
    public JsArray<ELKNode> getChildren() {
        return children;
    }

    @JsIgnore
    public void setChildren(JsArray<ELKNode> children) {
        this.children = children;
    }

    @JsIgnore
    public JsArray<ELKEdge> getEdges() {
        return edges;
    }

    @JsIgnore
    public void setEdges(JsArray<ELKEdge> edges) {
        this.edges = edges;
    }

    @JsIgnore
    public Object getLayoutOptions() {
        return layoutOptions;
    }

    @JsIgnore
    public ELKNode setLayoutOptions(Object layoutOptions) {
        this.layoutOptions = layoutOptions;
        return this;
    }

    @JsIgnore
    public void addNode(ELKNode node) {
        children.push(node);
    }

    @JsIgnore
    public void addEdge(ELKEdge edge) {
        getEdges().push(edge);
    }

    @JsIgnore
    public void addEdgeWithFilter(ELKEdge edge) {
        if (null != getChild(edge.getSources().getAt(0)) &&
                null != getChild(edge.getTargets().getAt(0))) {
            getEdges().push(edge);
        }
    }

    @JsIgnore
    public ELKNode getChild(String uuid) {
        return findChild(children, uuid);
    }

    @JsIgnore
    private ELKNode findChild(JsArray<ELKNode> children, String uuid) {
        for (int i = 0; i < children.length; i++) {
            if (children.getAt(i).getId().equals(uuid)) {
                return children.getAt(i);
            } else if (children.getAt(i).getChildren().length > 0) {
                findChild(children.getAt(i).getChildren(), uuid);
            }
        }
        return null;
    }

    @JsIgnore
    public ELKNode sortEdges() {
        edges.sort((a, b) -> a.getPriority() - b.getPriority());
        return this;
    }
}
