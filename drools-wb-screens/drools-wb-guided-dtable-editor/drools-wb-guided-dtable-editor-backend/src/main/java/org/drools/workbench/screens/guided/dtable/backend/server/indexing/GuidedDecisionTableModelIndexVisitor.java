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
package org.drools.workbench.screens.guided.dtable.backend.server.indexing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.model.index.Rule;
import org.kie.workbench.common.services.refactoring.model.index.RuleAttribute;
import org.kie.workbench.common.services.refactoring.model.index.Type;
import org.kie.workbench.common.services.refactoring.model.index.TypeField;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueRuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueRuleAttributeValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueRuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Visitor to extract index information from a Guided Decision Table
 */
public class GuidedDecisionTableModelIndexVisitor {

    private final DefaultIndexBuilder builder;
    private final GuidedDecisionTable52 model;
    private final Set<Pair<String, String>> results = new HashSet<Pair<String, String>>();

    public GuidedDecisionTableModelIndexVisitor( final DefaultIndexBuilder builder,
                                                 final GuidedDecisionTable52 model ) {
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
        if ( o instanceof GuidedDecisionTable52 ) {
            visit( (GuidedDecisionTable52) o );
        } else if ( o instanceof AttributeCol52 ) {
            visit( (AttributeCol52) o );
        } else if ( o instanceof Pattern52 ) {
            visit( (Pattern52) o );
        } else if ( o instanceof BRLConditionColumn ) {
            visit( (BRLConditionColumn) o );
        } else if ( o instanceof ConditionCol52 ) {
            visit( (ConditionCol52) o );
        } else if ( o instanceof BRLActionColumn ) {
            visit( (BRLActionColumn) o );
        } else if ( o instanceof ActionInsertFactCol52 ) {
            visit( (ActionInsertFactCol52) o );
        } else if ( o instanceof ActionSetFieldCol52 ) {
            visit( (ActionSetFieldCol52) o );
        }
    }

    private void visit( final GuidedDecisionTable52 o ) {
        //Add attributes
        for ( AttributeCol52 c : o.getAttributeCols() ) {
            visit( c );
        }
        //Add Types and Fields used by Conditions
        for ( CompositeColumn<? extends BaseColumn> c : o.getConditions() ) {
            visit( c );
        }
        //Add Types and Fields used by Actions
        for ( ActionCol52 c : o.getActionCols() ) {
            visit( c );
        }
        //Add rule names
        final String parentRuleName = model.getParentName();
        for ( List<DTCellValue52> row : model.getData() ) {
            final String ruleName = "Row " + row.get( 0 ).getNumericValue().longValue() + " " + model.getTableName();
            builder.addGenerator( new Rule( new ValueRuleIndexTerm( ruleName ),
                                            ( parentRuleName == null ? null : new ValueRuleIndexTerm( parentRuleName ) ) ) );
        }
    }

    private void visit( final AttributeCol52 o ) {
        final int iCol = model.getExpandedColumns().indexOf( o );
        for ( List<DTCellValue52> row : model.getData() ) {
            final String attributeValue = row.get( iCol ).getStringValue();
            if ( !( attributeValue == null || attributeValue.isEmpty() ) ) {
                builder.addGenerator( new RuleAttribute( new ValueRuleAttributeIndexTerm( o.getAttribute() ),
                                                         new ValueRuleAttributeValueIndexTerm( attributeValue ) ) );
            }
        }
    }

    private void visit( final Pattern52 o ) {
        builder.addGenerator( new Type( new ValueTypeIndexTerm( getFullyQualifiedClassName( o.getFactType() ) ) ) );
        for ( ConditionCol52 c : o.getChildColumns() ) {
            visit( c );
        }
    }

    private void visit( final BRLConditionColumn o ) {
        final RuleModel rm = new RuleModel();
        rm.setImports( model.getImports() );
        for ( IPattern p : o.getDefinition() ) {
            rm.addLhsItem( p );
        }
        final GuidedRuleModelIndexVisitor visitor = new GuidedRuleModelIndexVisitor( builder,
                                                                                     rm );
        results.addAll( visitor.visit() );
    }

    private void visit( final ConditionCol52 o ) {
        final Pattern52 p = model.getPattern( o );
        final String fullyQualifiedClassName = getFullyQualifiedClassName( p.getFactType() );
        builder.addGenerator( new TypeField( new ValueFieldIndexTerm( o.getFactField() ),
                                             new ValueTypeIndexTerm( getFullyQualifiedClassName( o.getFieldType() ) ),
                                             new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
    }

    private void visit( final BRLActionColumn o ) {
        final RuleModel rm = new RuleModel();
        rm.setImports( model.getImports() );
        for ( IAction a : o.getDefinition() ) {
            rm.addRhsItem( a );
        }
        final GuidedRuleModelIndexVisitor visitor = new GuidedRuleModelIndexVisitor( builder,
                                                                                     rm );
        results.addAll( visitor.visit() );
    }

    private void visit( final ActionInsertFactCol52 o ) {
        final String fullyQualifiedClassName = getFullyQualifiedClassName( o.getFactType() );
        builder.addGenerator( new Type( new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
        builder.addGenerator( new TypeField( new ValueFieldIndexTerm( o.getFactField() ),
                                             new ValueTypeIndexTerm( getFullyQualifiedClassName( o.getType() ) ),
                                             new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
    }

    private void visit( final ActionSetFieldCol52 o ) {
        final Pattern52 p = model.getConditionPattern( o.getBoundName() );
        final String fullyQualifiedClassName = getFullyQualifiedClassName( p.getFactType() );
        builder.addGenerator( new TypeField( new ValueFieldIndexTerm( o.getFactField() ),
                                             new ValueTypeIndexTerm( getFullyQualifiedClassName( o.getType() ) ),
                                             new ValueTypeIndexTerm( fullyQualifiedClassName ) ) );
    }

    private String getFullyQualifiedClassName( final String typeName ) {
        if ( typeName.contains( "." ) ) {
            return typeName;
        }

        for ( Import i : model.getImports().getImports() ) {
            if ( i.getType().endsWith( typeName ) ) {
                return i.getType();
            }
        }
        final String packageName = model.getPackageName();
        return ( !( packageName == null || packageName.isEmpty() ) ? packageName + "." + typeName : typeName );
    }

}
