/*
 * Copyright 2012 JBoss Inc
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
package org.drools.workbench.screens.guided.scorecard.backend.server;

import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A ScoreCardModel Visitor to identify fully qualified class names used by the ScoreCardModel
 */
public class GuidedScoreCardModelVisitor {

    private final ScoreCardModel model;
    private final String packageName;
    private final Imports imports;

    public GuidedScoreCardModelVisitor( final ScoreCardModel model ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.packageName = model.getPackageName();
        this.imports = model.getImports();
    }

    public Set<String> getConsumedModelClasses() {
        final Set<String> factTypes = new HashSet<String>();
        //Extract Fact Types from model
        factTypes.add( model.getFactName() );
        for ( Characteristic c : model.getCharacteristics() ) {
            factTypes.add( c.getFact() );
        }

        //Convert Fact Types into Fully Qualified Class Names
        final Set<String> fullyQualifiedClassNames = new HashSet<String>();
        for ( String factType : factTypes ) {
            fullyQualifiedClassNames.add( convertToFullyQualifiedClassName( factType ) );
        }

        return fullyQualifiedClassNames;
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
