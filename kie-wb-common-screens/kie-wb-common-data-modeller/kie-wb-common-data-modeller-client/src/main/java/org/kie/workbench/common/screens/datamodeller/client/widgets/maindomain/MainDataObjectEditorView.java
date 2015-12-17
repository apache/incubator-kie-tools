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

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import java.util.List;

import com.google.gwt.user.client.Command;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.uberfire.commons.data.Pair;

public interface MainDataObjectEditorView
        extends MainEditorView<MainDataObjectEditorView.Presenter> {

    interface Presenter {

        void onNameChange();

        void onLabelChange();

        void onDescriptionChange();

        void onSuperClassChange();

        void onPackageChange();

        void onPackageAdded();

    }

    void setName( String name );

    String getName();

    void setNameOnError( boolean onError );

    void setAllNameNameText();

    void setDescription( String description );

    String getDescription( );

    void setLabel( String label );

    String getLabel();

    void setSuperClass( String superClass );

    void setSuperClassOnError( boolean onError );

    void setSuperClassOnFocus();

    String getSuperClass();

    void setPackageName( String packageName );

    void setPackageNameOnError( boolean b );

    String getPackageName();

    String getNewPackageName();

    void setReadonly( boolean readonly );

    void showErrorPopup( String errorMessage, final Command afterShow, final Command afterClose );

    void initSuperClassList( List<Pair<String, String>> values, String selectedValue );

    void clearSuperClassList();

    void initPackageSelector( DataModelerContext context );

    void clearPackageList();

}
