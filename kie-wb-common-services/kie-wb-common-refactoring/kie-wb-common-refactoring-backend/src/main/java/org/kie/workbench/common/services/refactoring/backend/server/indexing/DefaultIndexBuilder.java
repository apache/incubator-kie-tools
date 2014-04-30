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
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.services.refactoring.model.index.IndexElementsGenerator;
import org.kie.workbench.common.services.refactoring.model.index.Rule;
import org.kie.workbench.common.services.refactoring.model.index.RuleAttribute;
import org.kie.workbench.common.services.refactoring.model.index.Type;
import org.kie.workbench.common.services.refactoring.model.index.TypeField;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.file.Path;

public class DefaultIndexBuilder {

    private Path path;
    private Set<Rule> rules;
    private Set<RuleAttribute> ruleAttributes;
    private Set<Type> types;
    private Set<TypeField> typeFields;

    public DefaultIndexBuilder( final Path path ) {
        this.path = PortablePreconditions.checkNotNull( "path",
                                                        path );
        this.rules = new HashSet<Rule>();
        this.ruleAttributes = new HashSet<RuleAttribute>();
        this.types = new HashSet<Type>();
        this.typeFields = new HashSet<TypeField>();
    }

    public DefaultIndexBuilder addRule( final Rule rule ) {
        this.rules.add( PortablePreconditions.checkNotNull( "rule",
                                                            rule ) );
        return this;
    }

    public DefaultIndexBuilder addRuleAttribute( final RuleAttribute ruleAttribute ) {
        this.ruleAttributes.add( PortablePreconditions.checkNotNull( "ruleAttribute",
                                                                     ruleAttribute ) );
        return this;
    }

    public DefaultIndexBuilder addType( final Type type ) {
        this.types.add( PortablePreconditions.checkNotNull( "type",
                                                            type ) );
        return this;
    }

    public DefaultIndexBuilder addField( final TypeField typeField ) {
        this.typeFields.add( PortablePreconditions.checkNotNull( "typeField",
                                                                 typeField ) );
        return this;
    }

    public Set<Pair<String, String>> build() {
        final Set<Pair<String, String>> indexElements = new HashSet<Pair<String, String>>();
        for ( Rule rule : rules ) {
            addIndexElements( indexElements,
                              rule );
        }
        for ( RuleAttribute ruleAttribute : ruleAttributes ) {
            addIndexElements( indexElements,
                              ruleAttribute );
        }
        for ( Type type : types ) {
            addIndexElements( indexElements,
                              type );
        }
        for ( TypeField typeField : typeFields ) {
            addIndexElements( indexElements,
                              typeField );
        }
        return indexElements;
    }

    private void addIndexElements( final Set<Pair<String, String>> indexElements,
                                   final IndexElementsGenerator generator ) {
        if ( generator == null ) {
            return;
        }
        final List<Pair<String, String>> generatorsIndexElements = generator.toIndexElements();
        indexElements.addAll( generatorsIndexElements );
    }

}
