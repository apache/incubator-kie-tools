/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.screens.guided.scorecard.backend.server.indexing;

import java.util.Collection;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;

public class GuidedScoreCardFactory {

    public static ScoreCardModel makeScoreCardWithCharacteristics( final String packageName,
                                                                   final Collection<Import> imports,
                                                                   final String name ) {
        final ScoreCardModel model = new ScoreCardModel();
        model.getImports().getImports().addAll( imports );
        model.setPackageName( packageName );
        model.setName( name );

        model.setFactName( "Applicant" );
        model.setFieldName( "age" );

        final Characteristic c = new Characteristic();
        c.setName( "c1" );
        c.setFact( "Mortgage" );
        c.setField( "amount" );
        c.setDataType( DataType.TYPE_NUMERIC_INTEGER );

        model.getCharacteristics().add( c );

        return model;
    }

    public static ScoreCardModel makeScoreCardWithoutCharacteristics( final String packageName,
                                                                      final Collection<Import> imports,
                                                                      final String name ) {
        final ScoreCardModel model = new ScoreCardModel();
        model.getImports().getImports().addAll( imports );
        model.setPackageName( packageName );
        model.setName( name );

        model.setFactName( "Applicant" );
        model.setFieldName( "age" );

        return model;
    }

}
