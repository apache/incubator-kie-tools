package org.uberfire.backend.server.impl.charts;


import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.shared.charts.ChartPopulateEvent;
import org.uberfire.shared.charts.ChartRefreshEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Service
@ApplicationScoped
public class ChartContentProducer {

    @Inject
    private Event<ChartPopulateEvent> chartPopulateEvents;

    public void addNotification(@Observes ChartRefreshEvent event) {

        chartPopulateEvents.fire(new ChartPopulateEvent("Toni", Math.random() * 100));
        chartPopulateEvents.fire(new ChartPopulateEvent("Mark", Math.random() * 100));
        chartPopulateEvents.fire(new ChartPopulateEvent("Salaboy", Math.random() * 100));
        chartPopulateEvents.fire(new ChartPopulateEvent("Michael", Math.random() * 100));

    }

}
