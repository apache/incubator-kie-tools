/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.messageconsole.client.console.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.kie.workbench.common.screens.messageconsole.client.console.resources.i18n.MessageConsoleConstants;

public interface MessageConsoleResources extends ClientBundle {

    public MessageConsoleResources INSTANCE = GWT.create( MessageConsoleResources.class );

    MessageConsoleConstants CONSTANTS = GWT.create( MessageConsoleConstants.class );

    @ClientBundle.Source("images/error.gif") ImageResource Error();

    @ClientBundle.Source("images/warning.gif") ImageResource Warning();

    @ClientBundle.Source("images/information.gif") ImageResource Information();

}
