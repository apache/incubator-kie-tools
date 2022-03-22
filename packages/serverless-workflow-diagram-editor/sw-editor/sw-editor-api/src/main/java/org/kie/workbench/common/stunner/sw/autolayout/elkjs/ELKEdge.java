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

    @JsIgnore
    public ELKEdge(String id, String source, String target) {
        this.id = id;
        this.sources = new JsArray<>(source);
        this.targets = new JsArray<>(target);
        this.sections = new JsArray<>();
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
    public JsArray<Point2D> getBendPoints() {
        JsArray<Point2D> points = new JsArray<>();
        JsPropertyMap<?> parsedSection = Js.cast(sections.getAt(0));
        JsArray<Object> bendPoints = Js.cast(parsedSection.get("bendPoints"));

        if (null != bendPoints) {
            for (Object elkBendPoint : bendPoints.asList()) {
                JsPropertyMap<?> parsedBendPoint = Js.cast(elkBendPoint);
                final double x = Js.cast(parsedBendPoint.get("x"));
                final double y = Js.cast(parsedBendPoint.get("y"));
                points.push(new Point2D(x, y));
            }
        }

        return points;
    }
}
