package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import org.uberfire.ext.properties.editor.client.PropertyEditorItemsWidget;

public class AbstractPropertyEditorWidget extends Composite {

    PropertyEditorItemsWidget parent;

    public void setParent( PropertyEditorItemsWidget parent ) {
        this.parent = parent;
    }

    public void setValidationError( String errorMessage ) {
        parent.setError( errorMessage );
    }

    public void clearOldValidationErrors() {
        parent.clearError();
    }

}
