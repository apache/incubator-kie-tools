package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.client.resources.i18n.DateIntervalTypeConstants;
import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Provides an editor for a the refreshInterval data set definition attribute..</p>
 * 
 * @since 0.4.0
 */
@Dependent
public class DataSetDefRefreshIntervalEditor implements IsWidget, org.dashbuilder.dataset.client.editor.DataSetDefRefreshIntervalEditor {

    private static final DateIntervalType DEFAULT_INTERVAL_TYPE = DateIntervalType.HOUR;

    private static List<DateIntervalType> ALLOWED_TYPES = Arrays.asList(
            DateIntervalType.SECOND,
            DateIntervalType.MINUTE,
            DateIntervalType.HOUR,
            DateIntervalType.DAY,
            DateIntervalType.MONTH,
            DateIntervalType.YEAR);

    public interface View extends UberView<DataSetDefRefreshIntervalEditor> {

        View addHelpContent(final String title, final String content, final Placement placement);
        
        View addIntervalTypeItem(String item);

        View setSelectedIntervalType(int index);

        int getSelectedIntervalTypeIndex();

        View setQuantity(double value);
        
        double getQuantity();
        
        View setEnabled(boolean enabled);
    }
    
    Event<org.dashbuilder.common.client.event.ValueChangeEvent<String>> valueChangeEvent;
    public View view;
    
    @Inject
    public DataSetDefRefreshIntervalEditor(final View view,
                                           final Event<org.dashbuilder.common.client.event.ValueChangeEvent<String>> valueChangeEvent) {
        this.view = view;
        this.valueChangeEvent = valueChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);

        // List box for available date interval types.
        for (final DateIntervalType dateInterval : ALLOWED_TYPES) {
            final String s = DateIntervalTypeConstants.INSTANCE.getString(dateInterval.name());
            view.addIntervalTypeItem(s);
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void addHelpContent(final String title, final String content, final Placement placement) {
        view.addHelpContent(title, content, placement);
    }
    
    @Override
    public void setEnabled(final boolean isEnabled) {
        view.setEnabled(isEnabled);
    }

    @Override
    public void setValue(final String value) {
        double quantity = 1;
        int index = -1;
        if (value != null) {
            final TimeAmount timeAmount = TimeAmount.parse(value);
            quantity = timeAmount.getQuantity();
            final DateIntervalType dType = timeAmount.getType();
            index = getIntervalTypeIndex(dType);
        }
        
        view.setQuantity(quantity);
        view.setSelectedIntervalType(index > -1 ? index : getIntervalTypeIndex(DEFAULT_INTERVAL_TYPE));
    }

    @Override
    public String getValue() {
        final Double quantity = view.getQuantity();
        final DateIntervalType type = getSelectedIntervalType();
        return new TimeAmount(quantity.longValue(), type).toString();
    }
    
    private DateIntervalType getSelectedIntervalType() {
        return ALLOWED_TYPES.get(view.getSelectedIntervalTypeIndex());
    }

    private int getIntervalTypeIndex(DateIntervalType type) {
        for (int i=0; i<ALLOWED_TYPES.size(); i++) {
            if (ALLOWED_TYPES.get(i).equals(type)) {
                return i;
            }
        }
        return 0;
    }
}
