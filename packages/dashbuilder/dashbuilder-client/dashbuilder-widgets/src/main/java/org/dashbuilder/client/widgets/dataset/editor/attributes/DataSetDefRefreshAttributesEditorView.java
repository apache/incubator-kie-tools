package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;

/**
 * <p>The Data Set refresh attributes editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetDefRefreshAttributesEditorView extends Composite implements DataSetDefRefreshAttributesEditor.View {

    interface Binder extends UiBinder<Widget, DataSetDefRefreshAttributesEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    DataSetDefRefreshAttributesEditor presenter;

    @UiField
    ToggleSwitch refreshEnabled;
    
    @UiField(provided = true)
    DataSetDefRefreshIntervalEditor.View refreshTimeView;

    @UiField(provided = true)
    IsWidget refreshOnStaleDataView;

    private HandlerRegistration handlerRegistration;
    
    @Override
    public void init(final DataSetDefRefreshAttributesEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(final IsWidget enabledView, final DataSetDefRefreshIntervalEditor.View refreshTimeView) {
        this.refreshOnStaleDataView = enabledView;
        this.refreshTimeView = refreshTimeView;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void addRefreshEnabledButtonHandler(final Command handler) {
        
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
        }
        
        handlerRegistration = refreshEnabled.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> event) {
                handler.execute();;
            }
        });
        
    }

    @Override
    public void setEnabled(final boolean isEnabled) {
        refreshEnabled.setValue(isEnabled);
    }
    
}
