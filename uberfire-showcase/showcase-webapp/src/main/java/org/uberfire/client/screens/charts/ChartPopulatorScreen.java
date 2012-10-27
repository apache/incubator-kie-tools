package org.uberfire.client.screens.charts;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.charts.ChartPopulateEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "chartPopulator")
public class ChartPopulatorScreen {

    @Inject
    private Event<ChartPopulateEvent> chartPopulateEvents;

    private final TextBox personName = new TextBox();
    private final TextBox amount = new TextBox();

    @WorkbenchPartTitle
    public String getName() {
        return "Demo Chart Populator";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        VerticalPanel widgets = new VerticalPanel();
        widgets.add(getColumnNameRow());
        widgets.add(getAmountRow());
        Button send = new Button("Send");
        send.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                chartPopulateEvents.fire(
                        new ChartPopulateEvent(
                                personName.getText(),
                                Double.parseDouble(amount.getText())));
            }
        });
        widgets.add(send);
        return widgets;
    }

    private Widget getAmountRow() {
        HorizontalPanel widgets = new HorizontalPanel();
        widgets.add(new Label("Amount:"));
        widgets.add(amount);
        return widgets;
    }

    private Widget getColumnNameRow() {
        HorizontalPanel widgets = new HorizontalPanel();
        widgets.add(new Label("Person Name:"));
        widgets.add(personName);
        return widgets;
    }
}
