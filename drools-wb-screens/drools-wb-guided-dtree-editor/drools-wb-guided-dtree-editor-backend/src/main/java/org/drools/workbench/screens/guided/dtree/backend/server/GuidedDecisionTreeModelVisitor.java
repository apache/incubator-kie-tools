/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtree.backend.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A GuidedDecisionTree Visitor to identify fully qualified class names used by the GuidedDecisionTree
 */
public class GuidedDecisionTreeModelVisitor {

    private final GuidedDecisionTree model;
    private final String packageName;
    private final Imports imports;

    public GuidedDecisionTreeModelVisitor( final GuidedDecisionTree model ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.packageName = model.getPackageName();
        this.imports = model.getImports();
    }

    public Set<String> getConsumedModelClasses() {
        final Set<String> factTypes = new HashSet<String>();

        //Extract Fact Types from model
        factTypes.addAll( visitNode( model.getRoot() ) );

        //Convert Fact Types into Fully Qualified Class Names
        final Set<String> fullyQualifiedClassNames = new HashSet<String>();
        for ( String factType : factTypes ) {
            fullyQualifiedClassNames.add( convertToFullyQualifiedClassName( factType ) );
        }

        return fullyQualifiedClassNames;
    }

    private Set<String> visitNode( final Node node ) {
        if ( node == null ) {
            return Collections.EMPTY_SET;
        }
        final Set<String> factTypes = new HashSet<String>();
        if ( node instanceof TypeNode ) {
            final TypeNode tn = (TypeNode) node;
            factTypes.add( tn.getClassName() );
        } else if ( node instanceof ActionInsertNode ) {
            final ActionInsertNode an = (ActionInsertNode) node;
            factTypes.add( an.getClassName() );
        }
        for ( Node child : node.getChildren() ) {
            factTypes.addAll( visitNode( child ) );
        }
        return factTypes;
    }

    //Get the fully qualified class name of the fact type
    private String convertToFullyQualifiedClassName( final String factType ) {
        if ( factType.contains( "." ) ) {
            return factType;
        }
        String fullyQualifiedClassName = null;
        for ( Import imp : imports.getImports() ) {
            if ( imp.getType().endsWith( factType ) ) {
                fullyQualifiedClassName = imp.getType();
                break;
            }
        }
        if ( fullyQualifiedClassName == null ) {
            fullyQualifiedClassName = packageName + "." + factType;
        }
        return fullyQualifiedClassName;
    }

}
