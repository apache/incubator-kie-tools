/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.basicset.definition;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.basicset.definition.property.Name;
import org.kie.workbench.common.stunner.basicset.definition.property.background.BackgroundAndBorderSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanConnect;

@Portable
@Bindable
@Definition(graphFactory = EdgeFactory.class, builder = BasicConnector.BasicConnectorBuilder.class)
@CanConnect(startRole = "all", endRole = "all")
@FormDefinition(startElement = "backgroundSet")
public class BasicConnector implements BasicSetDefinition {

    @Category
    public static final transient String category = Categories.CONNECTORS;

    @Title
    public static final transient String title = "Basic Connector";

    @Description
    public static final transient String description = "A Basic Connector";

    @Property
    @FormField(afterElement = "backgroundSet")
    @Valid
    private Name name;

    @PropertySet
    @FormField
    @Valid
    private BackgroundAndBorderSet backgroundSet;

    @Labels
    private final Set<String> labels = new HashSet<String>() {{
        add("all");
        add("connector");
    }};

    @NonPortable
    public static class BasicConnectorBuilder implements Builder<BasicConnector> {

        public static final transient String COLOR = "#000000";
        public static final transient String BORDER_COLOR = "#000000";
        public static final Double BORDER_SIZE = 3d;

        @Override
        public BasicConnector build() {
            return new BasicConnector(new Name("Connector"),
                                      new BackgroundAndBorderSet(COLOR,
                                                                 BORDER_COLOR,
                                                                 BORDER_SIZE));
        }
    }

    public BasicConnector() {
    }

    public BasicConnector(final @MapsTo("name") Name name,
                          final @MapsTo("backgroundSet") BackgroundAndBorderSet backgroundSet) {
        this.name = name;
        this.backgroundSet = backgroundSet;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public Name getName() {
        return name;
    }

    public void setName(final Name name) {
        this.name = name;
    }

    public BackgroundAndBorderSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(final BackgroundAndBorderSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }
}
