/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.widgets;

import org.jboss.errai.common.client.dom.*;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.inject.Inject;

@Templated
public class LibraryBreadCrumbToolbarView implements LibraryBreadCrumbToolbarPresenter.View, IsElement {

    private LibraryBreadCrumbToolbarPresenter presenter;

    @DataField
    @Inject
    Label ouLabel;

    @DataField
    @Inject
    Select ouDropdown;

    @Inject
    Document document;

    @Override
    public void init( LibraryBreadCrumbToolbarPresenter presenter ) {
        this.presenter = presenter;
        ouDropdown.setOnchange( event -> {
            presenter.selectOrganizationUnit( ouDropdown.getValue() );
        } );
    }

    @Override
    public void addOrganizationUnit( String ou ) {
        ouDropdown.add( createOption( ou ) );
    }

    @Override
    public void clearOrganizationUnits() {
        DOMUtil.removeAllChildren( ouDropdown );
    }

    @Override
    public void setOrganizationUnitSelected( String identifier ) {
        ouDropdown.setValue( identifier );
    }

    public void setOuLabel( String label ) {
        ouLabel.setTextContent( label+ ":" );
    }

    private Option createOption( String ou ) {
        Option option = ( Option ) document.createElement( "option" );
        option.setText( ou );
        return option;
    }
}