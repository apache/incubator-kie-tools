package org.uberfire.properties.editor.client.widgets;

import com.google.gwt.user.client.ui.Composite;

public class AbstractPropertyEditorWidget extends Composite {

    PropertyEditorItemsWidget parent;
    PropertyEditorErrorWidget errorWidget;

    public void setParent( PropertyEditorItemsWidget parent ) {
        this.parent = parent;
    }

    public void setErrorWidget( PropertyEditorErrorWidget errorWidget ) {
        this.errorWidget = errorWidget;
    }

    public void setValidationError(String error) {
        parent.setError();
        errorWidget.setText( error );
    }

    public void clearOldValidationErrors() {
        parent.clearError();
        errorWidget.setText( "" );
    }
}
