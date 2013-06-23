package org.uberfire.client;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOC;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

@EntryPoint
public class JSEntryPoint {

    private static boolean type;

    @PostConstruct
    public void setup() {
        publish();
    }

    public static void registerPlugin( final Object obj ) {
        IOC.getBeanManager().addBean( (Class) JSNativePlugin.class, JSNativePlugin.class, null, new JSNativePlugin( (JavaScriptObject) obj ), DEFAULT_QUALIFIERS, null, true );
    }

    // Alias registerPlugin with a global JS function.
    private native void publish() /*-{
        $wnd.$registerPlugin = @org.uberfire.client.JSEntryPoint::registerPlugin(Ljava/lang/Object;);
    }-*/;
}
