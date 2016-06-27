package org.uberfire.ext.layout.editor.client.infra;

import org.jboss.errai.ioc.client.container.IOC;

public class BeanHelper {

    public static void destroy( Object o ) {
        IOC.getBeanManager().destroyBean( o );
    }
}
