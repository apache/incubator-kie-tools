/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.drools.workbench.screens.guided.dtree.backend.server.indexing;

import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Visitor to extract index information from a Guided Decision Trees
 */
public class GuidedDecisionTreeModelIndexVisitor {

    private final DefaultIndexBuilder builder;
    private final GuidedDecisionTree model;
    private final Set<Pair<String, String>> results = new HashSet<Pair<String, String>>();

    public GuidedDecisionTreeModelIndexVisitor( final DefaultIndexBuilder builder,
                                                final GuidedDecisionTree model ) {
        this.builder = PortablePreconditions.checkNotNull( "builder",
                                                           builder );
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
    }

    public Set<Pair<String, String>> visit() {
        visit( model );
        results.addAll( builder.build() );
        return results;
    }

    private void visit( final Object o ) {
        //TODO {manstis} Need to define the model first!
        if ( o instanceof GuidedDecisionTree ) {
            visit( (GuidedDecisionTree) o );
        }
    }

    private void visit( final GuidedDecisionTree o ) {
        //TODO {manstis} Need to define the model first!
    }

}
