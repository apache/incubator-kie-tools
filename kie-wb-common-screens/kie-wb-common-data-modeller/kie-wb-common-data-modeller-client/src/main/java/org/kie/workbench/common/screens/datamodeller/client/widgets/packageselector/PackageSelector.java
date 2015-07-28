/**
 * Copyright 2012 JBoss Inc
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils.*;

@Dependent
public class PackageSelector extends Composite {

    interface PackageSelectorUIBinder
            extends UiBinder<Widget, PackageSelector> {

    }

    private static PackageSelectorUIBinder uiBinder = GWT.create( PackageSelectorUIBinder.class );

    @UiField
    Select packageList;

    @UiField
    Button newPackage;

    @Inject
    ValidatorService validatorService;

    @Inject
    NewPackagePopup newPackagePopup;

    private DataModelerContext context;

    public PackageSelector() {
        initWidget( uiBinder.createAndBindUi( this ) );
        newPackage.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                newPackagePopup.show();
            }
        } );
    }

    @PostConstruct
    private void init() {
        Command command = new Command() {
            @Override
            public void execute() {
                String newPackage = newPackagePopup.getPackageName();
                processNewPackage( newPackage );
            }
        };
        newPackagePopup.setAfterAddCommand( command );
        clean();
    }

    private void processNewPackage( String newPackageName ) {
        if ( newPackageName != null && !"".equals( newPackageName.trim() ) ) {
            boolean exists = false;
            newPackageName = newPackageName.trim();
            int count = packageList.getItemCount();
            if ( count > 0 ) {
                for ( int i = 0; i < count; i++ ) {
                    if ( ( exists = newPackageName.equals( packageList.getValue( i ) ) ) ) {
                        break;
                    }
                }
            }
            if ( exists ) {
                setSelectedValue( packageList, newPackageName );
            } else {
                packageList.add( newOption( newPackageName, newPackageName ) );
                setSelectedValue( packageList, newPackageName );
                DomEvent.fireNativeEvent( Document.get().createChangeEvent(), packageList );
            }
            if ( context != null ) {
                context.appendPackage( newPackageName.trim() );
            }
        }
    }

    public void enableCreatePackage( boolean enable ) {
        newPackage.setVisible( enable );
    }

    public void setEnabled( boolean enabled ) {
        newPackage.setVisible( enabled );
        packageList.setEnabled( enabled );
        refreshSelect( packageList );
    }

    public Boolean isValueSelected() {
        return packageList.getValue() != null && !"".equals( packageList.getValue().trim() ) && !NOT_SELECTED.equals( packageList.getValue().trim() );
    }

    public Select getPackageList() {
        return packageList;
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
        initList( null, true );
    }

    public void setCurrentPackage( String currentPackage ) {
        boolean enableEmptyPackageOption = false;

        if ( currentPackage == null || "".equals( currentPackage ) ) {
            enableEmptyPackageOption = true;
            currentPackage = NOT_SELECTED;
        }
        initList( currentPackage, enableEmptyPackageOption );
    }

    public void clean() {
        packageList.clear();
        packageList.add( emptyOption() );
    }

    private void initList( String currentPackage, boolean enableEmptyPackageOption ) {
        packageList.clear();
        List<String> packageNames = new ArrayList<String>();

        if ( context != null && context.getCurrentProjectPackages() != null ) {
            for ( String packageName : context.getCurrentProjectPackages() ) {
                packageNames.add( packageName );
            }
        }

        if ( currentPackage != null && !packageNames.contains( currentPackage ) ) {
            packageNames.add( currentPackage );
        }

        Collections.sort( packageNames );

        if ( enableEmptyPackageOption ) {
            packageList.add( emptyOption() );
        }

        for ( String packageName : packageNames ) {
            packageList.add( newOption( packageName, packageName ) );
        }

        setSelectedValue( packageList, currentPackage );
    }

}