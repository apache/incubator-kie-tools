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

import org.uberfire.client.mvp.UberView;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;

public interface PackageSelectorView
        extends UberView<PackageSelectorView.Presenter> {

    void addToPackageList( String newPackageName, boolean b );

    interface Presenter {

        void onPackageChange();

        void onNewPackage();

    }

    interface PackageSelectorHandler {

        void onPackageChange( String packageName );

        void onPackageAdded( String packageName );

    }

    void enableCreatePackage( boolean enable );

    void setEnabled( boolean enabled );

    void setPackage( String currentPackage );

    String getPackage();

    void clear();

    void initPackageList( List<Pair<String, String>> packageList,
            String selectedPackage,
            boolean enableEmptyPackageOption );

    void showNewPackagePopup( Command afterAddCommand );

    String getNewPackage();

}