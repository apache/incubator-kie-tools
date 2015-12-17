/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;

@Dependent
public class PackageSelector
        implements
        PackageSelectorView.Presenter,
        IsWidget {

    private DataModelerContext context;

    private List<String> packageList = new ArrayList<String>(  );

    private PackageSelectorView view;

    private List<PackageSelectorView.PackageSelectorHandler> handlers = new ArrayList<PackageSelectorView.PackageSelectorHandler>(  );

    @Inject
    public PackageSelector( PackageSelectorView view ) {
        this.view = view;
        view.init( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onPackageChange() {
        handlePackageChange( view.getPackage() );
    }

    @Override
    public void onNewPackage() {
        view.showNewPackagePopup( new Command() {
            @Override
            public void execute() {
                doPackageAdded( view.getNewPackage() );
            }
        } );
    }

    private void doPackageAdded( String newPackage ) {
        final String newPackageName = DataModelerUtils.trim( newPackage );
        if ( newPackageName != null && !"".equals( newPackageName ) ) {
            boolean exists = packageList.contains( newPackageName );
            if ( exists ) {
                view.setPackage( newPackageName );
            } else {
                view.addToPackageList( newPackageName, true );
            }
            if ( context != null ) {
                context.appendPackage( newPackageName.trim() );
            }
        }
        handlePackageAdded( newPackageName );
    }

    private void handlePackageAdded( final String packageName ) {
        for ( PackageSelectorView.PackageSelectorHandler handler : handlers ) {
            handler.onPackageAdded( packageName );
        }
    }
    private void handlePackageChange( String packageName ) {
        for ( PackageSelectorView.PackageSelectorHandler handler : handlers ) {
            handler.onPackageChange( packageName );
        }
    }

    public void enableCreatePackage( boolean enable ) {
        view.enableCreatePackage( enable );
    }

    public void setEnabled( boolean enabled ) {
        view.setEnabled( enabled );
    }

    public boolean isValueSelected() {
        String currentPackage = DataModelerUtils.trim( view.getPackage() );
        return currentPackage != null && !"".equals( currentPackage ) && !UIUtil.NOT_SELECTED.equals( currentPackage );
    }

    public String getPackage() {
        return view.getPackage();
    }

    public String getNewPackage() {
        return view.getNewPackage();
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
            currentPackage = UIUtil.NOT_SELECTED;
        }
        initList( currentPackage, enableEmptyPackageOption );
    }

    public void clear() {
        view.initPackageList( new ArrayList<Pair<String, String>>(), null, true );
    }

    public void addPackageSelectorHandler( PackageSelectorView.PackageSelectorHandler handler ) {
        if ( !handlers.contains( handler ) ) {
            handlers.add( handler );
        }
    }

    private void initList( String currentPackage, boolean enableEmptyPackageOption ) {
        packageList.clear();

        if ( context != null && context.getCurrentProjectPackages() != null ) {
            for ( String packageName : context.getCurrentProjectPackages() ) {
                packageList.add( packageName );
            }
        }

        if ( currentPackage != null && !packageList.contains( currentPackage ) ) {
            packageList.add( currentPackage );
        }

        Collections.sort( packageList );

        List<Pair<String, String>> packageOptions = new ArrayList<Pair<String, String>>( packageList.size() );
        for ( String packageName : packageList ) {
            packageOptions.add( new Pair<String, String>( packageName, packageName ) );
        }

        view.initPackageList( packageOptions, currentPackage, enableEmptyPackageOption );
    }
}