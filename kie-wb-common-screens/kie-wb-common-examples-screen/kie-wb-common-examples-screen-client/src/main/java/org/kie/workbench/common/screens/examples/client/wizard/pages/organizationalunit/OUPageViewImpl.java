/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.client.wizard.pages.organizationalunit;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.examples.client.wizard.widgets.ComboBox;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;

@Dependent
@Templated
public class OUPageViewImpl extends Composite implements OUPageView {

    @DataField("target-repository-form")
    Element targetRepositoryGroup = DOM.createDiv();

    @DataField("targetRepositoryTextBox")
    TextBox targetRepositoryTextBox = GWT.create( TextBox.class );

    @DataField("target-repository-help")
    Element targetRepositoryHelp = DOM.createSpan();

    @DataField("organizational-units-form")
    Element organizationalUnitsGroup = DOM.createDiv();

    @DataField
    ComboBox organizationalUnitsDropdown = GWT.create( ComboBox.class );

    @DataField("organizational-units-help")
    Element organizationalUnitsHelp = DOM.createSpan();

    private OUPage presenter;

    @PostConstruct
    public void setup() {
        targetRepositoryTextBox.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                presenter.setTargetRepository( new ExampleTargetRepository( event.getValue() ) );
            }
        } );
        organizationalUnitsDropdown.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                presenter.setTargetOrganizationalUnit( new ExampleOrganizationalUnit( event.getValue() ) );
            }
        } );
    }

    @Override
    public void init( final OUPage presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void initialise() {
        targetRepositoryTextBox.setText( "" );
        organizationalUnitsDropdown.setText( "" );
    }

    @Override
    public void setTargetRepositoryPlaceHolder( final String placeHolder ) {
        targetRepositoryTextBox.setPlaceholder( placeHolder );
    }

    @Override
    public void setOrganizationalUnitsPlaceHolder( final String placeHolder ) {
        organizationalUnitsDropdown.setPlaceholder( placeHolder );
    }

    @Override
    public void setOrganizationalUnits( final List<ExampleOrganizationalUnit> organizationalUnits ) {
        this.organizationalUnitsDropdown.clear();
        for ( ExampleOrganizationalUnit organizationalUnit : organizationalUnits ) {
            this.organizationalUnitsDropdown.addItem( organizationalUnit.getName() );
        }
    }

    @Override
    public void setOrganizationalUnit( final ExampleOrganizationalUnit organizationalUnit ) {
        this.organizationalUnitsDropdown.setText( organizationalUnit.getName() );
    }

    @Override
    public void setTargetRepositoryGroupType( final ValidationState state ) {
        StyleHelper.addUniqueEnumStyleName( targetRepositoryGroup,
                                            ValidationState.class,
                                            state );
    }

    @Override
    public void showTargetRepositoryHelpMessage( final String message ) {
        targetRepositoryHelp.getStyle().setVisibility( Style.Visibility.VISIBLE );
        targetRepositoryHelp.setInnerText( message );
    }

    @Override
    public void hideTargetRepositoryHelpMessage() {
        targetRepositoryHelp.getStyle().setVisibility( Style.Visibility.HIDDEN );
        targetRepositoryHelp.setInnerText( "" );
    }

    @Override
    public void setTargetOrganizationalUnitGroupType( final ValidationState state ) {
        StyleHelper.addUniqueEnumStyleName( organizationalUnitsGroup,
                                            ValidationState.class,
                                            state );
    }

    @Override
    public void showTargetOrganizationalUnitHelpMessage( final String message ) {
        organizationalUnitsHelp.getStyle().setVisibility( Style.Visibility.VISIBLE );
        organizationalUnitsHelp.setInnerText( message );
    }

    @Override
    public void hideTargetOrganizationalUnitHelpMessage() {
        organizationalUnitsHelp.getStyle().setVisibility( Style.Visibility.HIDDEN );
        organizationalUnitsHelp.setInnerText( "" );
    }

}
