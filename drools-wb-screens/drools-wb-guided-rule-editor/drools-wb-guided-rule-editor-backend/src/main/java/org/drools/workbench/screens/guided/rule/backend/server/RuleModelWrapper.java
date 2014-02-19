/*
 * Copyright 2014 JBoss Inc
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

import java.util.List;

import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;

/**
 * Delegating RuleModel that controls whether the RuleModel should be rendered with DSL expansions
 */
public class RuleModelWrapper extends RuleModel {

    private final RuleModel model;
    private final boolean hasDSLSentences;

    public RuleModelWrapper( final RuleModel model,
                             final boolean hasDSLSentences ) {
        this.model = model;
        this.hasDSLSentences = hasDSLSentences;

        this.name = model.name;
        this.parentName = model.parentName;
        this.attributes = model.attributes;
        this.metadataList = model.metadataList;
        this.lhs = model.lhs;
        this.rhs = model.rhs;
    }

    @Override
    public List<String> getLHSBoundFacts() {
        return model.getLHSBoundFacts();
    }

    @Override
    public FactPattern getLHSBoundFact( String var ) {
        return model.getLHSBoundFact( var );
    }

    @Override
    public SingleFieldConstraint getLHSBoundField( String var ) {
        return model.getLHSBoundField( var );
    }

    @Override
    public String getLHSBindingType( String var ) {
        return model.getLHSBindingType( var );
    }

    @Override
    public String getFieldBinding( FieldConstraint fc,
                                   String var ) {
        return model.getFieldBinding( fc, var );
    }

    @Override
    public List<String> getRHSBoundFacts() {
        return model.getRHSBoundFacts();
    }

    @Override
    public ActionInsertFact getRHSBoundFact( String var ) {
        return model.getRHSBoundFact( var );
    }

    @Override
    public FactPattern getLHSParentFactPatternForBinding( String var ) {
        return model.getLHSParentFactPatternForBinding( var );
    }

    @Override
    public List<String> getAllLHSVariables() {
        return model.getAllLHSVariables();
    }

    @Override
    public List<String> getAllRHSVariables() {
        return model.getAllRHSVariables();
    }

    @Override
    public List<String> getAllVariables() {
        return model.getAllVariables();
    }

    @Override
    public List<String> getFieldBinding( FieldConstraint f ) {
        return model.getFieldBinding( f );
    }

    @Override
    public boolean removeLhsItem( int idx ) {
        return model.removeLhsItem( idx );
    }

    @Override
    public boolean isBoundFactUsed( String binding ) {
        return model.isBoundFactUsed( binding );
    }

    @Override
    public void addLhsItem( IPattern pat ) {
        model.addLhsItem( pat );
    }

    @Override
    public void addLhsItem( IPattern pat,
                            boolean append ) {
        model.addLhsItem( pat, append );
    }

    @Override
    public void addLhsItem( IPattern pat,
                            int position ) {
        model.addLhsItem( pat, position );
    }

    @Override
    public void moveLhsItemDown( int itemIndex ) {
        model.moveLhsItemDown( itemIndex );
    }

    @Override
    public void moveLhsItemUp( int itemIndex ) {
        model.moveLhsItemUp( itemIndex );
    }

    @Override
    public void moveRhsItemDown( int itemIndex ) {
        model.moveRhsItemDown( itemIndex );
    }

    @Override
    public void moveRhsItemUp( int itemIndex ) {
        model.moveRhsItemUp( itemIndex );
    }

    @Override
    public void addRhsItem( IAction action ) {
        model.addRhsItem( action );
    }

    @Override
    public void addRhsItem( IAction action,
                            boolean append ) {
        model.addRhsItem( action, append );
    }

    @Override
    public void addRhsItem( IAction action,
                            int position ) {
        model.addRhsItem( action, position );
    }

    @Override
    public void removeRhsItem( int idx ) {
        model.removeRhsItem( idx );
    }

    @Override
    public void addAttribute( RuleAttribute attribute ) {
        model.addAttribute( attribute );
    }

    @Override
    public void removeAttribute( int idx ) {
        model.removeAttribute( idx );
    }

    @Override
    public void addMetadata( RuleMetadata metadata ) {
        model.addMetadata( metadata );
    }

    @Override
    public void removeMetadata( int idx ) {
        model.removeMetadata( idx );
    }

    @Override
    public RuleMetadata getMetaData( String attributeName ) {
        return model.getMetaData( attributeName );
    }

    @Override
    public boolean updateMetadata( RuleMetadata target ) {
        return model.updateMetadata( target );
    }

    @Override
    public List<String> getBoundVariablesInScope( BaseSingleFieldConstraint con ) {
        return model.getBoundVariablesInScope( con );
    }

    @Override
    public boolean isVariableNameUsed( String s ) {
        return model.isVariableNameUsed( s );
    }

    @Override
    public boolean hasDSLSentences() {
        return this.hasDSLSentences;
    }

    @Override
    public boolean isNegated() {
        return model.isNegated();
    }

    @Override
    public void setNegated( boolean isNegated ) {
        model.setNegated( isNegated );
    }

    @Override
    public Imports getImports() {
        return model.getImports();
    }

    @Override
    public void setImports( Imports imports ) {
        model.setImports( imports );
    }

    @Override
    public String getPackageName() {
        return model.getPackageName();
    }

    @Override
    public void setPackageName( String packageName ) {
        model.setPackageName( packageName );
    }

}
