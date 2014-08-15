package org.uberfire.client.exporter;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class PlaceManagerJSExporter implements UberfireJSExporter {

    @Override
    public void export() {
        publish();
    }

    private native void publish() /*-{
        $wnd.$goToPlace = @org.uberfire.client.exporter.PlaceManagerJSExporter::goTo(Ljava/lang/String;);
    }-*/;

    public static void goTo( final String place ) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final PlaceManager placeManager = beanManager.lookupBean( PlaceManager.class ).getInstance();
        placeManager.goTo( new DefaultPlaceRequest( place ) );
    }
}
