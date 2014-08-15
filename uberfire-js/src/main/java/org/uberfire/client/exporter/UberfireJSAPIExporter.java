package org.uberfire.client.exporter;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;

@ApplicationScoped
public class UberfireJSAPIExporter {

    @AfterInitialization
    public void export() {
        Collection<IOCBeanDef<UberfireJSExporter>> jsAPIs = IOC.getBeanManager().lookupBeans( UberfireJSExporter.class );
        for ( IOCBeanDef<UberfireJSExporter> bean : jsAPIs ) {
            UberfireJSExporter jsAPI = bean.getInstance();
            jsAPI.export();
        }
    }

}
