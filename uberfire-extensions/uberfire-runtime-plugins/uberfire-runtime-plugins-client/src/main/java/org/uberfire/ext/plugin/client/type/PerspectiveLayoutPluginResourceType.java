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

package org.uberfire.ext.plugin.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.ext.plugin.type.PerspectiveLayoutPluginResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class PerspectiveLayoutPluginResourceType
        extends PerspectiveLayoutPluginResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return new Icon( IconType.PENCIL );
    }
}
