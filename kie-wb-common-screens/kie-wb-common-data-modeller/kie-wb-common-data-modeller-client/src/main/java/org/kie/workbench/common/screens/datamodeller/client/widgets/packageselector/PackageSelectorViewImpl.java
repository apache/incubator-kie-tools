/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;

@Dependent
public class PackageSelectorViewImpl
        extends Composite
        implements PackageSelectorView {

    interface PackageSelectorUIBinder
            extends UiBinder<Widget, PackageSelectorViewImpl> {

    }

    private static PackageSelectorUIBinder uiBinder = GWT.create( PackageSelectorUIBinder.class );

    @UiField
    Select packageSelector;

    @UiField
    Button newPackage;

    NewPackagePopup newPackagePopup;

    private Presenter presenter;

    @Inject
    public PackageSelectorViewImpl( NewPackagePopup newPackagePopup ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.newPackagePopup = newPackagePopup;
    }

    @PostConstruct
    private void init() {

        packageSelector.addValueChangeHandler( e -> presenter.onPackageChange() );

        newPackage.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onNewPackage();
            }
        } );

        clear();
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        UIUtil.initList( packageSelector, true );
    }

    @Override
    public void initPackageList( final List<Pair<String, String>> packageList, final String selectedPackage, boolean enableEmptyPackageOption ) {
        UIUtil.initList( packageSelector, packageList, selectedPackage, enableEmptyPackageOption );
    }

    @Override
    public void addToPackageList( String packageName, boolean selected ) {

        packageSelector.add( UIUtil.newOption( packageName, packageName ) );
        if ( selected ) {
            UIUtil.setSelectedValue( packageSelector, packageName );
        }
        UIUtil.refreshSelect( packageSelector );
    }

    @Override
    public void showNewPackagePopup( final Command afterAddCommand ) {
        newPackagePopup.show( afterAddCommand );
    }

    @Override
    public String getNewPackage() {
        return newPackagePopup.getPackageName();
    }

    @Override
    public void enableCreatePackage( boolean enable ) {
        newPackage.setVisible( enable );
    }

    @Override
    public void setEnabled( boolean enabled ) {
        newPackage.setVisible( enabled );
        packageSelector.setEnabled( enabled );
        UIUtil.refreshSelect( packageSelector );
    }

    @Override
    public String getPackage() {
        return packageSelector.getValue();
    }

    @Override
    public void setPackage( String currentPackage ) {
        UIUtil.setSelectedValue( packageSelector, currentPackage );
    }
}