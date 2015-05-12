package org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

public interface PropertyEditionPopup extends IsWidget {

    void show();

    void setOkCommand( Command command );

    String getStringValue();

    void setStringValue( String value );

    void setProperty( PropertyEditorFieldInfo property );

}
