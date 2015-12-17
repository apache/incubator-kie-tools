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

package org.kie.workbench.common.screens.home.client.widgets.sections;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Section positioning it's children vertically
 */
public class VerticalSectionWidget extends Composite {

    private HeaderWidget header = new HeaderWidget();
    private VerticalPanel container = new VerticalPanel();
    private VerticalPanel contents = new VerticalPanel();

    public VerticalSectionWidget() {
        initWidget( container );
        container.add( header );
        container.add( contents );
    }

    public void setHeaderText( final String headerText ) {
        this.header.setText( headerText );
    }

    public void add( final Widget w ) {
        contents.add( w );
    }

    public void clear() {
        contents.clear();
    }

}
