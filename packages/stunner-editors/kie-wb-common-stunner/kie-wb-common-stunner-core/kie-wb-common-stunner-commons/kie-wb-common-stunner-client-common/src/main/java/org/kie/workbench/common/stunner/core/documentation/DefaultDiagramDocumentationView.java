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


package org.kie.workbench.common.stunner.core.documentation;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

/**
 * Default dummy implementation of {@link DocumentationView}. To implement a {@link DocumentationView} for the domain,
 * this should be extended and annotated with the applicable discriminator qualifier for the domain. For example
 * {@link @BPMN} or {@link @DMNEditor} etc.
 */
@Dependent
@Templated
public class DefaultDiagramDocumentationView extends Composite implements DocumentationView<Diagram> {

    private Optional<Diagram> diagram;

    public DefaultDiagramDocumentationView() {
        this.diagram = Optional.empty();
    }

    /**
     * Should be overridden to enable where on custom {@link DocumentationView}
     * @return
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public DocumentationView initialize(Diagram diagram) {
        this.diagram = Optional.ofNullable(diagram);
        return this;
    }

    @Override
    public DocumentationView<Diagram> refresh() {
        return this;
    }

    @Override
    public DocumentationView<Diagram> setIsSelected(Supplier<Boolean> isSelected) {
        return this;
    }

    public Optional<Diagram> getDiagram() {
        return diagram;
    }
}