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
package org.drools.workbench.screens.guided.rule.backend.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionPart;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A RuleModel Visitor to identify fully qualified class names used by the RuleModel
 */
public class GuidedRuleModelVisitor {

    private final RuleModel model;
    private final String packageName;
    private final Imports imports;

    public GuidedRuleModelVisitor( final RuleModel model ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.packageName = model.getPackageName();
        this.imports = model.getImports();
    }

    public Set<String> getConsumedModelClasses() {
        final Set<String> factTypes = new HashSet<String>();
        //Extract Fact Types from model
        if ( model.lhs != null ) {
            for ( int i = 0; i < model.lhs.length; i++ ) {
                IPattern pattern = model.lhs[ i ];
                factTypes.addAll( visit( pattern ) );
            }
        }
        if ( model.rhs != null ) {
            for ( int i = 0; i < model.rhs.length; i++ ) {
                IAction action = model.rhs[ i ];
                factTypes.addAll( visit( action ) );
            }
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

    private Set<String> visit( Object o ) {
        if ( o == null ) {
            return Collections.EMPTY_SET;
        }
        if ( o instanceof FactPattern ) {
            return visitFactPattern( (FactPattern) o );
        } else if ( o instanceof CompositeFieldConstraint ) {
            return visitCompositeFieldConstraint( (CompositeFieldConstraint) o );
        } else if ( o instanceof SingleFieldConstraintEBLeftSide ) {
            return visitSingleFieldConstraint( (SingleFieldConstraintEBLeftSide) o );
        } else if ( o instanceof SingleFieldConstraint ) {
            return visitSingleFieldConstraint( (SingleFieldConstraint) o );
        } else if ( o instanceof ExpressionFormLine ) {
            return visitExpressionFormLine( (ExpressionFormLine) o );
        } else if ( o instanceof ConnectiveConstraint ) {
            return visitConnectiveConstraint( (ConnectiveConstraint) o );
        } else if ( o instanceof CompositeFactPattern ) {
            return visitCompositeFactPattern( (CompositeFactPattern) o );
        } else if ( o instanceof FromAccumulateCompositeFactPattern ) {
            return visitFromAccumulateCompositeFactPattern( (FromAccumulateCompositeFactPattern) o );
        } else if ( o instanceof FromCollectCompositeFactPattern ) {
            return visitFromCollectCompositeFactPattern( (FromCollectCompositeFactPattern) o );
        } else if ( o instanceof FromCompositeFactPattern ) {
            return visitFromCompositeFactPattern( (FromCompositeFactPattern) o );
        } else if ( o instanceof ActionInsertFact ) {
            return visitActionInsertFact( (ActionInsertFact) o );
        }
        return Collections.EMPTY_SET;
    }

    private Set<String> visitActionInsertFact( ActionInsertFact afl ) {
        final Set<String> factTypes = new HashSet<String>();
        factTypes.add( afl.getFactType() );
        return factTypes;
    }

    private Set<String> visitCompositeFactPattern( CompositeFactPattern pattern ) {
        final Set<String> factTypes = new HashSet<String>();
        if ( pattern.getPatterns() != null ) {
            for ( IFactPattern fp : pattern.getPatterns() ) {
                factTypes.addAll( visit( fp ) );
            }
        }
        return factTypes;
    }

    private Set<String> visitCompositeFieldConstraint( CompositeFieldConstraint cfc ) {
        final Set<String> factTypes = new HashSet<String>();
        if ( cfc.getConstraints() != null ) {
            for ( int i = 0; i < cfc.getConstraints().length; i++ ) {
                FieldConstraint fc = cfc.getConstraints()[ i ];
                factTypes.addAll( visit( fc ) );
            }
        }
        return factTypes;
    }

    private Set<String> visitFactPattern( FactPattern pattern ) {
        final Set<String> factTypes = new HashSet<String>();
        factTypes.add( pattern.getFactType() );
        for ( FieldConstraint fc : pattern.getFieldConstraints() ) {
            factTypes.addAll( visit( fc ) );
        }
        return factTypes;
    }

    private Set<String> visitFromAccumulateCompositeFactPattern( FromAccumulateCompositeFactPattern pattern ) {
        final Set<String> factTypes = new HashSet<String>();
        factTypes.addAll( visit( pattern.getFactPattern() ) );
        factTypes.addAll( visit( pattern.getSourcePattern() ) );
        return factTypes;
    }

    private Set<String> visitFromCollectCompositeFactPattern( FromCollectCompositeFactPattern pattern ) {
        final Set<String> factTypes = new HashSet<String>();
        factTypes.addAll( visit( pattern.getFactPattern() ) );
        factTypes.addAll( visit( pattern.getRightPattern() ) );
        factTypes.addAll( visit( pattern.getExpression() ) );
        return factTypes;
    }

    private Set<String> visitFromCompositeFactPattern( FromCompositeFactPattern pattern ) {
        final Set<String> factTypes = new HashSet<String>();
        factTypes.addAll( visit( pattern.getFactPattern() ) );
        factTypes.addAll( visit( pattern.getExpression() ) );
        return factTypes;
    }

    private Set<String> visitSingleFieldConstraint( SingleFieldConstraint sfc ) {
        final Set<String> factTypes = new HashSet<String>();
        if ( sfc.getFactType() != null ) {
            factTypes.add( sfc.getFactType() );
        }
        factTypes.addAll( visit( sfc.getExpressionValue() ) );
        if ( sfc.getConnectives() != null ) {
            for ( int i = 0; i < sfc.getConnectives().length; i++ ) {
                factTypes.addAll( visit( sfc.getConnectives()[ i ] ) );
            }
        }
        return factTypes;
    }

    private Set<String> visitExpressionFormLine( ExpressionFormLine efl ) {
        final Set<String> factTypes = new HashSet<String>();
        for ( ExpressionPart part : efl.getParts() ) {
            if ( part.getClassType() != null ) {
                factTypes.add( part.getClassType() );
            }
        }
        return factTypes;
    }

    private Set<String> visitConnectiveConstraint( ConnectiveConstraint cc ) {
        final Set<String> factTypes = new HashSet<String>();
        if ( cc.getFactType() != null ) {
            factTypes.add( cc.getFactType() );
        }
        return factTypes;
    }

    private Set<String> visitSingleFieldConstraint( SingleFieldConstraintEBLeftSide sfexp ) {
        final Set<String> factTypes = new HashSet<String>();
        if ( sfexp.getFactType() != null ) {
            factTypes.add( sfexp.getFactType() );
        }
        factTypes.addAll( visit( sfexp.getExpressionValue() ) );
        factTypes.addAll( visit( sfexp.getExpressionLeftSide() ) );
        if ( sfexp.getConnectives() != null ) {
            for ( int i = 0; i < sfexp.getConnectives().length; i++ ) {
                factTypes.addAll( visit( sfexp.getConnectives()[ i ] ) );
            }
        }
        return factTypes;
    }

}
