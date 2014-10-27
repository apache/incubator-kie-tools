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
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;
import org.kie.uberfire.perspective.editor.client.structure.HTMLEditorWidgetUI;

public class EditHTML
        extends PopupPanel {

    private final HTMLEditorWidgetUI parent;

    private static final String DEFAULT_HTML = "Add your HTML here...";

    @UiField
    Modal popup;

    @UiField
    TextArea textArea;

    Listener listener;

    public interface Listener {
        void onSave();
        void onClose();
    }

    interface Binder
            extends
            UiBinder<Widget, EditHTML> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public EditHTML( EditorWidget parent, Listener listener) {
        setWidget( uiBinder.createAndBindUi( this ) );
        final HTMLEditorWidgetUI htmlParent = (HTMLEditorWidgetUI) parent;
        this.parent = htmlParent;
        this.listener = listener;
        setupHTMLEditor( htmlParent );
    }

    private void setupHTMLEditor( HTMLEditorWidgetUI htmlParent ) {
        if ( htmlParent.getHtmlCode() == null || htmlParent.getHtmlCode().isEmpty() ) {
            this.textArea.setText(DEFAULT_HTML);
        }
        else{
            this.textArea.setText( htmlParent.getHtmlCode() );
        }
    }

    public void show() {
        popup.show();
    }

    @UiHandler("close")
    void close( final ClickEvent event ) {
        popup.hide();
        if (listener != null) {
            listener.onClose();
        }
    }

    @UiHandler("save")
    void save( final ClickEvent event ) {
        parent.setHtmlCode( textArea.getText() );
        popup.hide();
        if (listener != null) {
            listener.onSave();
        }
    }
}
