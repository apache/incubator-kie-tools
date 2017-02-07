/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.basicset;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.basicset.definition.BasicConnector;
import org.kie.workbench.common.stunner.basicset.definition.Circle;
import org.kie.workbench.common.stunner.basicset.definition.Polygon;
import org.kie.workbench.common.stunner.basicset.definition.Rectangle;
import org.kie.workbench.common.stunner.basicset.definition.Ring;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.ShapeSet;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

@ApplicationScoped
@Bindable
@DefinitionSet(
        graphFactory = GraphFactory.class,
        definitions = {
                // Basic shapes.
                Rectangle.class,
                Circle.class,
                Ring.class,
                Polygon.class,
                // Connectors.
                BasicConnector.class
        },
        builder = BasicSet.BasicSetBuilder.class
)
@CanContain(roles = {"all"})
@ShapeSet
public class BasicSet {

    @Description
    public static final transient String description = "Basic Set";

    @NonPortable
    public static class BasicSetBuilder implements Builder<BasicSet> {

        @Override
        public BasicSet build() {
            return new BasicSet();
        }
    }

    public BasicSet() {
    }

    public String getDescription() {
        return description;
    }
}
