package org.uberfire.backend.server.cdi;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startable;

@Singleton
@Startup
public class AlternativeStarterBean {

    private static final Logger logger = LoggerFactory.getLogger(AlternativeStarterBean.class);

    private static final String EJB_METHOD = "ejb";
    private static final String START_METHOD = System.getProperty( "org.uberfire.start.method", "cdi" );

    @Inject
    private Startable startableBean;

    @PostConstruct
    public void configure() {
        if (EJB_METHOD.equals(START_METHOD)) {
            logger.debug("Starting all beans defined as startable...");
            startableBean.start();
            logger.info("All startable beans properly started");
        }
    }
}
