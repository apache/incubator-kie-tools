package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.BasePopupPropertyEditorWidget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

public class IdGeneratorEditorWidget extends BasePopupPropertyEditorWidget {

    @Override protected PropertyEditionPopup createEditionPopup( PropertyEditorFieldInfo property ) {
        final PropertyEditionPopup popup = GWT.create( IdGeneratorEditionDialog.class );
        popup.setProperty( property );
        return popup;
    }
}
