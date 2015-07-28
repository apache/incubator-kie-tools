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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.extras.select.client.ui.Select;

public interface NewFieldPopupView
        extends IsWidget {


    interface Presenter {

        void onCreate();

        void onCreateAndContinue();

        void onCancel();

        void onTypeChange();

    }

    interface NewFieldPopupHandler {

        void onCreate( String fieldName, String fieldLabel, String type, boolean multiple );

        void onCreateAndContinue( String fieldName, String fieldLabel, String type, boolean multiple );

        void onCancel();
    }

    void setPresenter( Presenter presenter );

    Select getPropertyTypeList();

    String getSelectedType();

    String getFieldName();

    String getFieldLabel();

    void setErrorMessage( String errorMessage );

    boolean getIsMultiple();

    void setIsMultiple( boolean multiple );

    void enableIsMultiple( boolean enabled );

    void setFocusOnFieldName();

    void clear();

    void show();

    void hide();

}
