/*
* Copyright 2013 JBoss Inc
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
package org.kie.uberfire.perspective.editor.client.panels.components.popup;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.perspective.editor.client.panels.perspective.PerspectivePresenter;

public class SavePerspective
        extends PopupPanel {

    private final PerspectivePresenter perspectivePresenter;
    @UiField
    Modal popup;

    @UiField
    TextBox name;

    interface Binder
            extends
            UiBinder<Widget, SavePerspective> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public SavePerspective( PerspectivePresenter perspectivePresenter ) {
        this.perspectivePresenter = perspectivePresenter;
        setWidget( uiBinder.createAndBindUi( this ) );

    }

    public void show() {
        String perspectiveName = perspectivePresenter.getView().getPerspectiveEditor().getName();
        name.setText( perspectiveName );
        popup.show();
    }

    @UiHandler("save")
    void save( final ClickEvent event ) {
        perspectivePresenter.save( name.getText() );
        popup.hide();
    }


}
