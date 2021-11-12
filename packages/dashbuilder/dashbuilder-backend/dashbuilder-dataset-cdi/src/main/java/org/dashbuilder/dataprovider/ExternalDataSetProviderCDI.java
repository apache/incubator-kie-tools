package org.dashbuilder.dataprovider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataprovider.external.ExternalDataSetCaller;
import org.dashbuilder.dataprovider.external.ExternalDataSetProvider;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.events.DataSetStaleEvent;
import org.dashbuilder.scheduler.Scheduler;

@ApplicationScoped
public class ExternalDataSetProviderCDI extends ExternalDataSetProvider {
    
    
    public ExternalDataSetProviderCDI() {
        super();
    }

    @Inject
    public ExternalDataSetProviderCDI(StaticDataSetProvider staticDataSetProvider, Scheduler scheduler) {
        super(ExternalDataSetCaller.get(), staticDataSetProvider, scheduler);
    }
    

    protected void onDataSetStaleEvent(@Observes DataSetStaleEvent event) {
        var def = event.getDataSetDef();
        if (DataSetProviderType.EXTERNAL.equals(def.getProvider())) {
            var uuid = def.getUUID();
            staticDataSetProvider.removeDataSet(uuid);
        }
    }

    protected void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent event) {
        var def = event.getDataSetDef();
        if (DataSetProviderType.EXTERNAL.equals(def.getProvider())) {
            var uuid = def.getUUID();
            staticDataSetProvider.removeDataSet(uuid);
        }
    }

    protected void onDataSetDefModifiedEvent(@Observes DataSetDefModifiedEvent event) {
        var def = event.getOldDataSetDef();
        if (DataSetProviderType.EXTERNAL.equals(def.getProvider())) {
            var uuid = def.getUUID();
            staticDataSetProvider.removeDataSet(uuid);
        }
    }

}