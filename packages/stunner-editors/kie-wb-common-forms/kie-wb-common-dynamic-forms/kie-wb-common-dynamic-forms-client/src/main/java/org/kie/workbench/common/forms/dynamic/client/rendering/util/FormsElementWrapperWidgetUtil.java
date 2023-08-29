/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering.util;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

/**
 * Utility class that helps to handle the lifecycle of the generated {@link ElementWrapperWidget} on forms views.
 */
public interface FormsElementWrapperWidgetUtil {

    /**
     * Generates a {@link Widget} ({@link ElementWrapperWidget}) for a specific {@link HTMLElement} and keeps a reference
     * to the view (source) that is using it.
     * @param source The view object that requires the {@link Widget} to be generated
     * @param element The {@link HTMLElement} that needs to be converted into a {@link Widget}
     * @return a {@link Widget} wrapping the given {@link HTMLElement}
     */
    Widget getWidget(Object source, HTMLElement element);

    /**
     * Generates a {@link Widget} ({@link ElementWrapperWidget}) for a specific {@link elemental2.dom.HTMLElement} and keeps a reference
     * to the view (source) that is using it.
     * @param source The view object that requires the {@link Widget} to be generated
     * @param element The {@link elemental2.dom.HTMLElement} that needs to be converted into a {@link Widget}
     * @return a {@link Widget} wrapping the given {@link elemental2.dom.HTMLElement}
     */
    Widget getWidget(Object source, elemental2.dom.HTMLElement element);

    /**
     * Clears and detaches all the {@link ElementWrapperWidget} generated for a given view (source).
     * @param source The view that has been generating the {@link ElementWrapperWidget}
     */
    void clear(Object source);
}
