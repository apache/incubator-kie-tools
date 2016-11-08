/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.dynamic.service.context.generation.TransformerContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;

public class DMOBasedTransformerContext implements TransformerContext<StaticModelFormRenderingContext> {

    private Object model;
    private String type;

    private StaticModelFormRenderingContext renderingContext;

    private ProjectDataModelOracle oracle;

    private Map<String, FactBuilder> factBuilders;

    private DMOBasedTransformerContext( Object model, ProjectDataModelOracle oracle, Map<String, FactBuilder> factBuilders, StaticModelFormRenderingContext context ) {
        this.model = model;
        this.type = model.getClass().getName();
        this.oracle = oracle;
        this.factBuilders = factBuilders;
        this.renderingContext = context;
    }

    private DMOBasedTransformerContext( Object model, String type, ProjectDataModelOracle oracle, Map<String, FactBuilder> factBuilders, StaticModelFormRenderingContext context ) {
        this.model = model;
        this.type = type;
        this.oracle = oracle;
        this.factBuilders = factBuilders;
        this.renderingContext = context;
    }

    @Override
    public DMOBasedTransformerContext copyFor( String type ) {
        if ( this.getType().equals( type ) ) {
            return this;
        }

        return new DMOBasedTransformerContext( model, type, oracle, factBuilders, renderingContext );
    }

    @Override
    public Object getModel() {
        return model;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public StaticModelFormRenderingContext getRenderingContext() {
        return renderingContext;
    }

    public ProjectDataModelOracle getOracle() {
        return oracle;
    }

    public static DMOBasedTransformerContext getTransformerContextFor( Object model ) throws IOException {
        Assert.notNull( "Model cannot be null", model );

        final ProjectDataModelOracleBuilder builder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();
        ProjectDataModelOracle oracle = new ProjectDataModelOracleImpl();

        final ClassFactBuilder modelFactBuilder = new ClassFactBuilder( builder, model.getClass(), false, TypeSource.JAVA_PROJECT );

        oracle = modelFactBuilder.getDataModelBuilder().build();

        Map<String, FactBuilder> builders = new HashMap<>();

        for ( FactBuilder factBuilder : modelFactBuilder.getInternalBuilders().values() ) {
            if ( factBuilder instanceof ClassFactBuilder ) {
                builders.put( ( (ClassFactBuilder) factBuilder ).getType(), factBuilder );
                factBuilder.build( (ProjectDataModelOracleImpl) oracle );
            }
        }
        builders.put( modelFactBuilder.getType(), modelFactBuilder );

        modelFactBuilder.build( (ProjectDataModelOracleImpl) oracle );

        return new DMOBasedTransformerContext( model, oracle, builders, new StaticModelFormRenderingContext() );
    }
}
