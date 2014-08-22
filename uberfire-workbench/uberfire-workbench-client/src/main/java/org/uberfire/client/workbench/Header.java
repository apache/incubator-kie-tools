package org.uberfire.client.workbench;

import org.jboss.errai.ioc.client.container.BeanActivator;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;


/**
 * CDI beans that implement Header are automatically discovered and added to the top of the Workbench screen. They stick
 * to the top of the viewport even when the main content area is scrolled up and down.
 * <p>
 * To disable a particular header, you can use a {@link BeanActivator}. The bean activator would have to report that the
 * bean is disabled very early in the workbench startup process, before {@link ApplicationReadyEvent} is fired. If you
 * need to do an asynchronous check in your activator, use {@link Workbench#addStartupBlocker(Class)} to block startup
 * until your activator obtains the data it needs. (The blocker could be registered in your entry point's
 * {@code @PostConstruct} method).
 */
public interface Header extends OrderableIsWidget {

}
