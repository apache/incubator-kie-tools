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
package org.kie.workbench.common.services.refactoring.model.index;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

public class TypeField implements IndexElementsGenerator {

    private ValueFieldIndexTerm fieldTerm;
    private ValueTypeIndexTerm fieldTypeTerm;
    private ValueTypeIndexTerm classTypeTerm;

    public TypeField( final ValueFieldIndexTerm fieldTerm,
                      final ValueTypeIndexTerm fieldTypeTerm,
                      final ValueTypeIndexTerm classTypeTerm ) {
        this.fieldTerm = PortablePreconditions.checkNotNull( "fieldTerm",
                                                             fieldTerm );
        this.fieldTypeTerm = PortablePreconditions.checkNotNull( "fieldTypeTerm",
                                                                 fieldTypeTerm );
        this.classTypeTerm = PortablePreconditions.checkNotNull( "classTypeTerm",
                                                                 classTypeTerm );
    }

    @Override
    public List<Pair<String, String>> toIndexElements() {
        final List<Pair<String, String>> indexElements = new ArrayList<Pair<String, String>>();
        indexElements.add( new Pair<String, String>( fieldTerm.getTerm(),
                                                     fieldTerm.getValue() ) );
        indexElements.add( new Pair<String, String>( fieldTypeTerm.getTerm(),
                                                     fieldTypeTerm.getValue() ) );
        indexElements.add( new Pair<String, String>( classTypeTerm.getTerm() + ":" + classTypeTerm.getValue() + ":" + fieldTerm.getTerm(),
                                                     fieldTerm.getValue() ) );
        return indexElements;
    }

}
