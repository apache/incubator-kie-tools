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

package org.uberfire.client.views.pfly.menu;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.NavbarBrand;

/**
 * Denotes that the target class or producer of type {@link NavbarBrand} provides the main branding link that appears above
 * the application's menu bar. Each application that uses PatternFly should have one such type or producer.
 * <p>
 * To fit with the overall layout, <b>the NavbarBrand widget that bears this qualifier must have a height of 25px.</b>
 */
public interface MainBrand extends IsWidget {

}
