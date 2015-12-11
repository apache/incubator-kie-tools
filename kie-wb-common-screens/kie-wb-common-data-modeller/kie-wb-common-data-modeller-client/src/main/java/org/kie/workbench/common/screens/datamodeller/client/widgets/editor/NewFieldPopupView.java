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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.List;

import org.uberfire.client.mvp.UberView;
import org.uberfire.commons.data.Pair;

public interface NewFieldPopupView
        extends UberView<NewFieldPopupView.Presenter> {

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

    void initTypeList( List<Pair<String, String>> options, boolean includeEmptyItem );

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
