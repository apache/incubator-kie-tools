package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.BasePopupField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.BasePopupPropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class IdGeneratorField extends BasePopupField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEvent;

    @Override protected BasePopupPropertyEditorWidget createPopupPropertyEditor( PropertyEditorFieldInfo property ) {
        final BasePopupPropertyEditorWidget widget = GWT.create( IdGeneratorEditorWidget.class );
        widget.setProperty( property );
        return widget;
    }
}
