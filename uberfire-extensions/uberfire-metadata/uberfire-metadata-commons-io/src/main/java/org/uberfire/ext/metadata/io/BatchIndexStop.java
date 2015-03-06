package org.uberfire.ext.metadata.io;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.uberfire.io.IOService;

public class BatchIndexStop implements ServletContextListener {

    @Inject
    private Instance<IOService> ioServices;

    @Override
    public void contextInitialized( ServletContextEvent ce ) {
    }

    @Override
    public void contextDestroyed( ServletContextEvent ce ) {
        for ( final IOService ioService : ioServices ) {
            if ( ioService instanceof IOServiceIndexedImpl ) {
                ( (IOServiceIndexedImpl) ioService ).getIndexEngine().dispose();
            }
        }
    }
}
