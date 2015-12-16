/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class NewFactWidget implements IsWidget,
                                      NewFactWidgetView.Presenter {

    public NewFactWidget( FieldConstraintHelper helper,
                          NewFactWidgetView view ) {
        view.setPresenter( this );
        view.setFactName( "Address" );
    }

    @Override
    public Widget asWidget() {
        return null;  //TODO: -Rikkola-
    }
}
