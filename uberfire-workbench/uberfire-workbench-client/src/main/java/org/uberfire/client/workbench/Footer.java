package org.uberfire.client.workbench;

import org.jboss.errai.ioc.client.container.BeanActivator;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;


/**
 * CDI beans that implement Footer are automatically created and added to the bottom of the Workbench screen. They stick
 * to the bottom of the viewport even when the browser window is resized.
 * <p>
 * To disable a particular footer, you can use a {@link BeanActivator}. The bean activator would have to report that the
 * bean is disabled very early in the workbench startup process, before {@link ApplicationReadyEvent} is fired. If you
 * need to do an asynchronous check in your activator, use {@link Workbench#addStartupBlocker(Class)} to block startup
 * until your activator obtains the data it needs. (The blocker could be registered in your entry point's
 * {@code @PostConstruct} method).
 */
public interface Footer extends OrderableIsWidget {

}
