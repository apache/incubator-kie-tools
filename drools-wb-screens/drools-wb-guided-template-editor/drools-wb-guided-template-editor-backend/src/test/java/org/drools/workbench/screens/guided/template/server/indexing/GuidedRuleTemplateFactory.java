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

package org.drools.workbench.screens.guided.template.server.indexing;

import java.util.Collection;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.template.shared.TemplateModel;

public class GuidedRuleTemplateFactory {

    public static TemplateModel makeModelWithAttributes( final String packageName,
                                                         final Collection<Import> imports,
                                                         final String name ) {
        final TemplateModel model = new TemplateModel();
        model.getImports().getImports().addAll( imports );
        model.setPackageName( packageName );
        model.name = name;

        model.addAttribute( new RuleAttribute( "ruleflow-group",
                                               "myRuleFlowGroup" ) );
        return model;
    }

    public static TemplateModel makeModelWithConditions( final String packageName,
                                                         final Collection<Import> imports,
                                                         final String name ) {
        final TemplateModel model = new TemplateModel();
        model.getImports().getImports().addAll( imports );
        model.setPackageName( packageName );
        model.name = name;

        final FactPattern p1 = new FactPattern( "Applicant" );
        final SingleFieldConstraint con1 = new SingleFieldConstraint();
        con1.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con1.setFactType( "Applicant" );
        con1.setFieldName( "age" );
        con1.setOperator( "==" );
        con1.setValue( "f1" );
        con1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        p1.addConstraint( con1 );

        model.addLhsItem( p1 );

        final FactPattern p2 = new FactPattern( "Mortgage" );
        final SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con1.setFactType( "Mortgage" );
        con2.setFieldName( "amount" );
        con2.setOperator( "==" );
        con2.setValue( "f2" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        p2.addConstraint( con2 );

        model.addLhsItem( p2 );

        model.addRow( new String[]{ "33", null } );

        return model;
    }

    public static TemplateModel makeModelWithActions( final String packageName,
                                                      final Collection<Import> imports,
                                                      final String name ) {
        final TemplateModel model = new TemplateModel();
        model.getImports().getImports().addAll( imports );
        model.setPackageName( packageName );
        model.name = name;

        final ActionInsertFact ifc1 = new ActionInsertFact();
        ifc1.setFactType( "Applicant" );
        ifc1.setBoundName( "$a" );
        final ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature( FieldNatureType.TYPE_TEMPLATE );
        afv1.setField( "age" );
        afv1.setValue( "f1" );
        ifc1.addFieldValue( afv1 );

        model.addRhsItem( ifc1 );

        final ActionInsertFact ifc2 = new ActionInsertFact();
        ifc2.setFactType( "Mortgage" );
        ifc2.setBoundName( "$m" );
        final ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setNature( FieldNatureType.TYPE_TEMPLATE );
        afv2.setField( "amount" );
        afv2.setValue( "f2" );
        ifc2.addFieldValue( afv2 );

        model.addRhsItem( ifc2 );

        final ActionSetField asf = new ActionSetField();
        asf.setVariable( "$a" );
        asf.addFieldValue( new ActionFieldValue( "age",
                                                 "33",
                                                 DataType.TYPE_NUMERIC_INTEGER ) );

        model.addRhsItem( asf );

        final ActionUpdateField auf = new ActionUpdateField();
        asf.setVariable( "$m" );
        asf.addFieldValue( new ActionFieldValue( "amount",
                                                 "10000",
                                                 DataType.TYPE_NUMERIC_INTEGER ) );

        model.addRhsItem( auf );

        model.addRow( new String[]{ "33", null } );

        return model;
    }

}
