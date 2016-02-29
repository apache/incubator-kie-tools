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

package org.kie.workbench.common.screens.examples.client.wizard.pages.repository;

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
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.examples.client.wizard.widgets.ComboBox;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;

@Dependent
@Templated
public class RepositoryPageViewImpl extends Composite implements RepositoryPageView {

    @DataField("repository-form")
    Element repositoryGroup = DOM.createDiv();

    @DataField
    ComboBox repositoryDropdown = GWT.create( ComboBox.class );

    @DataField("repository-help")
    Element repositoryHelp = DOM.createSpan();

    private RepositoryPage presenter;

    @PostConstruct
    public void setup() {
        repositoryDropdown.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                presenter.setSelectedRepository( new ExampleRepository( event.getValue() ) );
            }
        } );
    }

    @Override
    public void init( final RepositoryPage presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void initialise() {
        repositoryDropdown.setText( "" );
    }

    @Override
    public void setPlaceHolder( final String placeHolder ) {
        repositoryDropdown.setPlaceholder( placeHolder );
    }

    @Override
    public void setRepositories( final List<ExampleRepository> repositories ) {
        this.repositoryDropdown.clear();
        for ( ExampleRepository repository : repositories ) {
            this.repositoryDropdown.addItem( repository.getUrl() );
        }
    }

    @Override
    public void setRepository( final ExampleRepository repository ) {
        this.repositoryDropdown.setText( repository.getUrl() );
    }

    @Override
    public void setUrlGroupType( final ValidationState state ) {
        StyleHelper.addUniqueEnumStyleName( repositoryGroup,
                                            ValidationState.class,
                                            state );
    }

    @Override
    public void showUrlHelpMessage( final String message ) {
        repositoryHelp.getStyle().setVisibility( Style.Visibility.VISIBLE );
        repositoryHelp.setInnerText( message );
    }

    @Override
    public void hideUrlHelpMessage() {
        repositoryHelp.getStyle().setVisibility( Style.Visibility.HIDDEN );
        repositoryHelp.setInnerText( "" );
    }
}
