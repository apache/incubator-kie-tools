/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
