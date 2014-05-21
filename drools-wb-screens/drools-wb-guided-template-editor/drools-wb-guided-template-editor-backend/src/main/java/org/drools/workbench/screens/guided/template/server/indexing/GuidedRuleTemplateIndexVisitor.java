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
package org.drools.workbench.screens.guided.template.server.indexing;

import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.model.index.Rule;
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
 * Visitor to extract index information from a Guided Rule Model
 */
public class GuidedRuleTemplateIndexVisitor {

    private final DefaultIndexBuilder builder;
    private final TemplateModel model;

    public GuidedRuleTemplateIndexVisitor( final DefaultIndexBuilder builder,
                                           final TemplateModel model ) {
        this.builder = PortablePreconditions.checkNotNull( "builder",
                                                           builder );
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
    }

    public Set<Pair<String, String>> visit() {
        visit( model );
        return builder.build();
    }

    private void visit( final Object o ) {
        if ( o instanceof TemplateModel ) {
            visitRuleModel( (TemplateModel) o );
        } else if ( o instanceof RuleAttribute ) {
            visitRuleAttribute( (RuleAttribute) o );
        } else if ( o instanceof FactPattern ) {
            visitFactPattern( (FactPattern) o );
        } else if ( o instanceof CompositeFieldConstraint ) {
            visitCompositeFieldConstraint( (CompositeFieldConstraint) o );
        } else if ( o instanceof SingleFieldConstraintEBLeftSide ) {
            visitSingleFieldConstraint( (SingleFieldConstraintEBLeftSide) o );
        } else if ( o instanceof SingleFieldConstraint ) {
            visitSingleFieldConstraint( (SingleFieldConstraint) o );
        } else if ( o instanceof ConnectiveConstraint ) {
            visitConnectiveConstraint( (ConnectiveConstraint) o );
        } else if ( o instanceof CompositeFactPattern ) {
            visitCompositeFactPattern( (CompositeFactPattern) o );
        } else if ( o instanceof FreeFormLine ) {
            visitFreeFormLine( (FreeFormLine) o );
        } else if ( o instanceof FromAccumulateCompositeFactPattern ) {
            visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) o );
        } else if ( o instanceof FromCollectCompositeFactPattern ) {
            visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) o );
        } else if ( o instanceof FromCompositeFactPattern ) {
            visitFromCompositeFactPattern( (FromCompositeFactPattern) o );
        } else if ( o instanceof DSLSentence ) {
            visitDSLSentence( (DSLSentence) o );
        } else if ( o instanceof ActionInsertFact ) {
            visitActionFieldList( (ActionInsertFact) o );
        }
    }

    private void visitRuleAttribute( final RuleAttribute attr ) {
        builder.addGenerator( new org.kie.workbench.common.services.refactoring.model.index.RuleAttribute( new ValueRuleAttributeIndexTerm( attr.getAttributeName() ),
                                                                                                           new ValueRuleAttributeValueIndexTerm( attr.getValue() ) ) );
    }

    //ActionInsertFact, ActionSetField, ActionUpdateField
    private void visitActionFieldList( final ActionInsertFact afl ) {
        builder.addGenerator( new Type( new ValueTypeIndexTerm( getFullyQualifiedClassName( afl.getFactType() ) ) ) );
    }

    private void visitActionFieldList( final String fullyQualifiedClassName,
                                       final ActionSetField afl ) {
        for ( ActionFieldValue afv : afl.getFieldValues() ) {
            visit( fullyQualifiedClassName,
                   afv );
        }
    }

    private void visitCompositeFactPattern( final CompositeFactPattern pattern ) {
        builder.addGenerator( new Type( new ValueTypeIndexTerm( getFullyQualifiedClassName( pattern.getType() ) ) ) );
        if ( pattern.getPatterns() != null ) {
            for ( IFactPattern fp : pattern.getPatterns() ) {
                visit( fp );
            }
        }
    }

    private void visitCompositeFieldConstraint( final CompositeFieldConstraint cfc ) {
        if ( cfc.getConstraints() != null ) {
            for ( int i = 0; i < cfc.getConstraints().length; i++ ) {
                FieldConstraint fc = cfc.getConstraints()[ i ];
                visit( fc );
            }
        }
    }

    private void visitDSLSentence( final DSLSentence sentence ) {
        //TODO - Index DSLSentences
    }

    private void visitFactPattern( final FactPattern pattern ) {
        builder.addGenerator( new Type( new ValueTypeIndexTerm( getFullyQualifiedClassName( pattern.getFactType() ) ) ) );
        for ( FieldConstraint fc : pattern.getFieldConstraints() ) {
            visit( fc );
        }
    }

    private void visitFreeFormLine( final FreeFormLine ffl ) {
        //TODO - Index FreeFormLines
    }

    private void visitFromAccumulateCompositeFactPattern( final FromAccumulateCompositeFactPattern pattern ) {
        visit( pattern.getFactPattern() );
        visit( pattern.getExpression() );
        visit( pattern.getSourcePattern() );
    }

    private void visitFromCollectCompositeFactPattern( final FromCollectCompositeFactPattern pattern ) {
        visit( pattern.getExpression() );
        visit( pattern.getFactPattern() );
        visit( pattern.getRightPattern() );
    }

    private void visitFromCompositeFactPattern( final FromCompositeFactPattern pattern ) {
        visit( pattern.getExpression() );
        visit( pattern.getFactPattern() );
    }

    public void visitRuleModel( final TemplateModel model ) {
        //Add Attributes
        if ( model.attributes != null ) {
            for ( int i = 0; i < model.attributes.length; i++ ) {
                RuleAttribute attr = model.attributes[ i ];
                visit( attr );
            }
        }
        //Add Types and Fields used by LHS
        if ( model.lhs != null ) {
            for ( int i = 0; i < model.lhs.length; i++ ) {
                IPattern pattern = model.lhs[ i ];
                visit( pattern );
            }
        }
        //Add Types and Fields used by RHS
        if ( model.rhs != null ) {
            for ( int i = 0; i < model.rhs.length; i++ ) {
                IAction action = model.rhs[ i ];
                if ( action instanceof ActionSetField ) {
                    final ActionSetField asf = (ActionSetField) action;
                    final String typeName = getTypeNameForBinding( asf.getVariable() );
                    if ( typeName != null ) {
                        final String fullyQualifiedClassName = getFullyQualifiedClassName( typeName );
                        visitActionFieldList( fullyQualifiedClassName,
                                              asf );
                    }
                } else {
                    visit( action );
                }
            }
        }
        //Add rule names
        final String parentRuleName = model.parentName;
        for ( int i = 0; i < model.getRowsCount(); i++ ) {
            final String ruleName = model.name + "_" + i;
            builder.addGenerator( new Rule( new ValueRuleIndexTerm( ruleName ),
                                            ( parentRuleName == null ? null : new ValueRuleIndexTerm( parentRuleName ) ) ) );
        }

    }

    private String getTypeNameForBinding( final String binding ) {
        if ( model.getAllLHSVariables().contains( binding ) ) {
            return model.getLHSBindingType( binding );
        } else if ( model.getAllRHSVariables().contains( binding ) ) {
            return model.getRHSBoundFact( binding ).getFactType();
        }
        return null;
    }

    private void visitSingleFieldConstraint( final SingleFieldConstraint sfc ) {
        builder.addGenerator( new TypeField( new ValueFieldIndexTerm( sfc.getFieldName() ),
                                             new ValueTypeIndexTerm( getFullyQualifiedClassName( sfc.getFieldType() ) ),
                                             new ValueTypeIndexTerm( getFullyQualifiedClassName( sfc.getFactType() ) ) ) );
        if ( sfc.getConnectives() != null ) {
            for ( int i = 0; i < sfc.getConnectives().length; i++ ) {
                visit( sfc.getConnectives()[ i ] );
            }
        }
    }

    private void visitConnectiveConstraint( final ConnectiveConstraint cc ) {
        builder.addGenerator( new TypeField( new ValueFieldIndexTerm( cc.getFieldName() ),
                                             new ValueTypeIndexTerm( getFullyQualifiedClassName( cc.getFieldType() ) ),
                                             new ValueTypeIndexTerm( getFullyQualifiedClassName( cc.getFactType() ) ) ) );
    }

    private void visitSingleFieldConstraint( final SingleFieldConstraintEBLeftSide sfexp ) {
        visit( sfexp.getExpressionLeftSide() );
        visit( sfexp.getExpressionValue() );
        if ( sfexp.getConnectives() != null ) {
            for ( int i = 0; i < sfexp.getConnectives().length; i++ ) {
                visit( sfexp.getConnectives()[ i ] );
            }
        }
    }

    private void visit( final String fullyQualifiedClassName,
                        final ActionFieldValue afv ) {
        builder.addGenerator( new TypeField( new ValueFieldIndexTerm( afv.getField() ),
                                             new ValueTypeIndexTerm( getFullyQualifiedClassName( afv.getType() ) ),
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
