/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget;

import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionFieldFunction;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CEPWindow;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.ExpressionCollectionIndex;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class RuleModelCloneVisitorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCloneWithUnsupportedClass() {
        RuleModel model = new RuleModel();
        model.addLhsItem( new TestFactPattern() );
        RuleModelCloneVisitor visitor = new RuleModelCloneVisitor();

        thrown.expect( RuntimeException.class );
        thrown.expectMessage( TestFactPattern.class.getSimpleName() );
        visitor.visitRuleModel( model );
    }

    @Test
    public void testRuleModelCloning() {
        RuleModel model = new RuleModel();

        //--------------------------------------------------------------------------------------------------------------
        // LHS
        //--------------------------------------------------------------------------------------------------------------
        //
        RuleAttribute attribute = new RuleAttribute( "att1_name", "att1_val" );
        model.addAttribute( attribute );

        RuleMetadata metadata = new RuleMetadata( "meta1_name", "meta1_val" );
        model.addMetadata( metadata );

        FactPattern f1 = buildFactPattern();

        FactPattern f2 = new FactPattern();
        f2.setBoundName( "$f2" );
        f2.setFactType( "Fact2" );
        f2.addConstraint( buildCompositeFieldConstraint() );

        CompositeFactPattern composite = new CompositeFactPattern();
        composite.setType( CompositeFactPattern.COMPOSITE_TYPE_EXISTS );
        composite.addFactPattern( f1 );
        composite.addFactPattern( f2 );
        model.addLhsItem( composite );

        model.addLhsItem( buildFromAccumulateCompositeFactPattern() );
        model.addLhsItem( buildFromCollectCompositeFactPattern() );
        model.addLhsItem( buildFromCompositeFactPattern() );

        model.addLhsItem( buildFreeFormLine() );
        model.addLhsItem( buildDslSentence() );

        ActionGlobalCollectionAdd addGlobal = new ActionGlobalCollectionAdd();
        addGlobal.setFactName( "MyFact" );
        addGlobal.setGlobalName( "glist" );
        model.addRhsItem( addGlobal );

        //--------------------------------------------------------------------------------------------------------------
        // RHS
        //--------------------------------------------------------------------------------------------------------------
        //
        ActionInsertFact aif = new ActionInsertFact();
        aif.setBoundName( "$f" );
        aif.setFactType( "FactType" );
        aif.addFieldValue( buildActionFieldValue() );
        aif.addFieldValue( buildActionFieldValue() );
        model.addRhsItem( aif );

        ActionUpdateField auf = new ActionUpdateField();
        auf.setVariable( "$var" );
        auf.addFieldValue( buildActionFieldValue() );
        auf.addFieldValue( buildActionFieldValue() );
        model.addRhsItem( auf );

        ActionSetField asf = new ActionSetField();
        asf.setVariable( "$var" );
        asf.addFieldValue( buildActionFieldValue() );
        asf.addFieldValue( buildActionFieldValue() );
        model.addRhsItem( asf );

        ActionRetractFact arf = new ActionRetractFact();
        arf.setVariableName( "$f" );
        model.addRhsItem( arf );

        ActionCallMethod callMethod1 = new ActionCallMethod();
        callMethod1.setVariable( "var1" );
        callMethod1.setMethodName( "testMethod1 " );
        callMethod1.setState( ActionCallMethod.TYPE_DEFINED );
        callMethod1.addFieldValue( new ActionFieldFunction( "field1", "value1", "type1" ) );
        model.addRhsItem( callMethod1 );

        ActionCallMethod callMethod2 = new ActionCallMethod();
        callMethod2.setVariable( "var2" );
        callMethod2.setMethodName( "testMethod2 " );
        callMethod2.setState( ActionCallMethod.TYPE_UNDEFINED );
        callMethod2.addFieldValue( new ActionFieldFunction( "field2", "value2", "type2" ) );
        model.addRhsItem( callMethod2 );

        model.addRhsItem( buildFreeFormLine() );

        //--------------------------------------------------------------------------------------------------------------
        // Clone and verify
        //--------------------------------------------------------------------------------------------------------------
        //
        RuleModel clone = new RuleModelCloneVisitor().visitRuleModel( model );

        assertArrayEquals( model.attributes, clone.attributes );
        int attIndex = 0;
        for ( RuleAttribute att : model.attributes ) {
            assertNotSame( att, clone.attributes[attIndex++] );
        }

        assertArrayEquals( model.metadataList, clone.metadataList );
        int metIndex = 0;
        for ( RuleMetadata met : model.metadataList ) {
            assertNotSame( met, clone.metadataList[metIndex++] );
        }

        assertArrayEquals( model.lhs, clone.lhs );
        int lhsIndex = 0;
        for ( IPattern pattern : model.lhs ) {
            assertNotSame( pattern, clone.lhs[lhsIndex++] );
        }

        assertArrayEquals( model.rhs, clone.rhs );
        int rhsIndex = 0;
        for ( IAction action : model.rhs ) {
            assertNotSame( action, clone.rhs[rhsIndex++] );
        }
    }

    private static FromCompositeFactPattern buildFromCompositeFactPattern() {
        FromCompositeFactPattern fcomp = new FromCompositeFactPattern();
        fcomp.setExpression( buildExpressionFormLine() );
        fcomp.setFactPattern( buildFactPattern() );
        return fcomp;
    }

    private static FromAccumulateCompositeFactPattern buildFromAccumulateCompositeFactPattern() {
        FromAccumulateCompositeFactPattern facc = new FromAccumulateCompositeFactPattern();
        facc.setActionCode( "action code; " );
        facc.setExpression( buildExpressionFormLine() );
        facc.setFactPattern( buildFactPattern() );
        facc.setFunction( "function;" );
        facc.setInitCode( "init code;" );
        facc.setResultCode( "result code;" );
        facc.setReverseCode( "reverse code;" );
        facc.setSourcePattern( buildExpressionFormLine() );
        return facc;
    }

    private static FromCollectCompositeFactPattern buildFromCollectCompositeFactPattern() {
        FromCollectCompositeFactPattern fcoll = new FromCollectCompositeFactPattern();
        fcoll.setExpression( buildExpressionFormLine() );
        fcoll.setFactPattern( buildFactPattern() );
        fcoll.setRightPattern( buildExpressionFormLine() );
        return fcoll;
    }

    private static FactPattern buildFactPattern() {
        FactPattern fp = new FactPattern();
        fp.setBoundName( "$f" );
        fp.setFactType( "FactType" );
        fp.setNegated( true );
        CEPWindow win = new CEPWindow();
        win.setOperator( "winOp" );
        win.setParameter( "winKey", "winPar" );
        fp.setWindow( win );
        fp.addConstraint( buildSingleFieldConstraint() );
        return fp;
    }

    private static CompositeFieldConstraint buildCompositeFieldConstraint() {
        CompositeFieldConstraint cfc = new CompositeFieldConstraint();
        cfc.addConstraint( buildSingleFieldConstraint() );
        cfc.addConstraint( buildSingleFieldConstraintEBLeftSide() );
        cfc.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        return cfc;
    }

    private static SingleFieldConstraint buildSingleFieldConstraint() {
        SingleFieldConstraint sfc = new SingleFieldConstraint();
        sfc.setExpressionValue( buildExpressionFormLine() );
        sfc.setFactType( "FactType" );
        sfc.setFieldBinding( "fieldBinding" );
        sfc.setFieldName( "fieldName" );
        sfc.setFieldType( "FieldType" );
        sfc.setId( "id" );
        sfc.setOperator( "operator" );
        sfc.setParameter( "key", "parameter" );
        sfc.setParent( new SingleFieldConstraint( "parentFieldName" ) );
        sfc.addNewConnective();
        return sfc;
    }

    private static SingleFieldConstraintEBLeftSide buildSingleFieldConstraintEBLeftSide() {
        SingleFieldConstraintEBLeftSide sfc = new SingleFieldConstraintEBLeftSide();
        sfc.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        sfc.setExpressionValue( buildExpressionFormLine() );
        sfc.setExpressionLeftSide( buildExpressionFormLine() );
        sfc.setFactType( "FactType" );
        // do not set fieldBinding, fieldName and fieldType, these are computed from expressionLeftSide
        sfc.setId( "id" );
        sfc.setOperator( "operator" );
        sfc.setParameter( "key", "parameter" );
        sfc.setParent( new SingleFieldConstraint( "parentFieldName" ) );
        sfc.setValue( "value" );
        sfc.addNewConnective();
        return sfc;
    }

    private static ExpressionFormLine buildExpressionFormLine() {
        ExpressionFormLine efl = new ExpressionFormLine();
        efl.setBinding( "eflBinding" );
        efl.appendPart( new ExpressionText( "text" ) );
        efl.appendPart( new ExpressionCollectionIndex( "collectionIndex", "CT", "GT" ) );
        return efl;
    }

    private DSLSentence buildDslSentence() {
        DSLSentence dsl = new DSLSentence();
        dsl.setDrl( "Person( sex == {$sex} )" );
        dsl.setDefinition( "Person is {$sex:ENUM:Person.sex}" );
        return dsl;
    }

    private static FreeFormLine buildFreeFormLine() {
        FreeFormLine ffl = new FreeFormLine();
        ffl.setText( "text" );
        return ffl;
    }

    private static ActionFieldValue buildActionFieldValue() {
        ActionFieldValue afv = new ActionFieldValue();
        afv.setField( "field" );
        afv.setNature( FieldNatureType.TYPE_LITERAL );
        afv.setType( "Type" );
        afv.setValue( "value" );
        return afv;
    }

    private static class TestFactPattern implements IFactPattern {

        @Override
        public String getFactType() {
            throw new UnsupportedOperationException( "Not expected to be called." );
        }
    }
}
