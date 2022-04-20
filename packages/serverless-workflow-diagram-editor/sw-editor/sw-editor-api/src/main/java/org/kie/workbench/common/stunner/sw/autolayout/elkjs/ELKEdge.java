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
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

@JsType
public class ELKEdge {

    @JsProperty
    String id;
    @JsProperty
    JsArray<String> sources;
    @JsProperty
    JsArray<String> targets;
    @JsProperty
    JsArray<Object> sections;
    @JsProperty
    int priority;
    @JsIgnore
    JsArray<Point2D> bendPoints;

    @JsIgnore
    public ELKEdge(String id, String source, String target) {
        this.id = id;
        this.sources = new JsArray<>(source);
        this.targets = new JsArray<>(target);
        this.sections = new JsArray<>();
        this.priority = 10;
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
    public JsArray<String> getSources() {
        return sources;
    }

    @JsIgnore
    public void setSources(JsArray<String> sources) {
        this.sources = sources;
    }

    @JsIgnore
    public JsArray<String> getTargets() {
        return targets;
    }

    @JsIgnore
    public void setTargets(JsArray<String> targets) {
        this.targets = targets;
    }

    @JsIgnore
    public JsArray<Object> getSections() {
        return sections;
    }

    @JsIgnore
    public void setSections(JsArray<Object> sections) {
        this.sections = sections;
    }

    @JsIgnore
    public void setBendPoints(JsArray<Point2D> bendPoints) {
        this.bendPoints = bendPoints;
    }

    @JsIgnore
    public int getPriority() {
        return priority;
    }

    @JsIgnore
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @JsIgnore
    public JsArray<Point2D> getBendPoints() {
        if (null != bendPoints) {
            return bendPoints;
        }

        bendPoints = new JsArray<>();
        JsPropertyMap<?> parsedSection = Js.cast(sections.getAt(0));
        JsArray<Object> points = Js.cast(parsedSection.get("bendPoints"));

        if (null != points) {
            for (Object elkBendPoint : points.asList()) {
                JsPropertyMap<?> parsedPoint = Js.cast(elkBendPoint);
                final double x = Js.cast(parsedPoint.get("x"));
                final double y = Js.cast(parsedPoint.get("y"));
                bendPoints.push(new Point2D(x, y));
            }
        }

        return bendPoints;
    }
}
