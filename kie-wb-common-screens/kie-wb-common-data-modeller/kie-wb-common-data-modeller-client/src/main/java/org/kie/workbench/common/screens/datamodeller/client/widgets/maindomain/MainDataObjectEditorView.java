/*
 * Copyright 2015 JBoss Inc
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
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.uberfire.commons.data.Pair;

public interface MainDataObjectEditorView
        extends IsWidget {

    interface Presenter {

        void onNameChanged();

        void onLabelChanged();

        void onDescriptionChanged();

        void onSuperClassChanged();

        void onPackageChanged();

    }

    void setPresenter( Presenter presenter );

    void setName( String name );

    String getName();

    void setNameOnError( boolean onError );

    void setNameSelected();

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

    boolean isPackageSelected();

    String getPackageName();

    void setReadonly( boolean readonly );

    void showErrorPopup( String errorMessage, final Command afterShow, final Command afterClose );

    void initSuperClassList( List<Pair<String, String>> values, String selectedValue );

    void cleanSuperClassList();

    //TODO temporal method
    void initPackageSelector( DataModelerContext context );

    void cleanPackageList();

}
