package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.extras.slider.client.ui.Slider;
import org.gwtbootstrap3.extras.slider.client.ui.base.event.SlideStopEvent;
import org.gwtbootstrap3.extras.slider.client.ui.base.event.SlideStopHandler;

import javax.enterprise.context.Dependent;

/**
 * <p>The Data Set cache attributes editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefCacheAttributesEditorViewImpl extends Composite implements DataSetDefCacheAttributesEditorView {

    interface Binder extends UiBinder<Widget, DataSetDefCacheAttributesEditorViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    ViewCallback callback;

    @UiField
    Container mainContainer;
    
    @UiField(provided = true)
    IsWidget enabledView;

    @UiField
    HTML title;
    
    @UiField(provided = true)
    ValueBoxEditor.View valueView;

    @UiField
    IntegerBox valueBox;
    
    @UiField
    HTML units;
    
    @UiField
    Slider slider;
    
    @Override
    public void init(final ViewCallback callback) {
        this.callback = callback;
    }

    @Override
    public void init(final String title, final String units, final IsWidget enabledView, final ValueBoxEditor.View valueView) {
        this.enabledView = enabledView;
        this.valueView = valueView;
        initWidget(Binder.BINDER.createAndBindUi(this));
        this.units.setText(units);
        this.units.setTitle(units);
        this.title.setText(title);
        slider.addSlideStopHandler(new SlideStopHandler<Double>() {
            @Override
            public void onSlideStop(final SlideStopEvent<Double> event) {
                // NOTE: Parse double from string value to avoid https://github.com/gwtbootstrap3/gwtbootstrap3-extras/issues/169
                final Object value = event.getValue();
                final Double _v = Double.parseDouble(value.toString());
                callback.onValueChange(_v);
            }
        });
    }
    
    public void setEnabled(final boolean isEnabled) {
        valueBox.setEnabled(isEnabled);
        slider.setEnabled(isEnabled);
    }

    public void setValue(final Double value) {
        mainContainer.setVisible(true);
        slider.setValue(value);
    }

    @Override
    public void setRange(final Double min, final Double max) {
        slider.setMin(min);
        slider.setMax(max);
    }

}
