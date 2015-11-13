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

package org.uberfire.client.workbench.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.resources.i18n.UberfireConstants;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

@ApplicationScoped
public class DotResourceType
        extends DotResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        String desc = UberfireConstants.INSTANCE.dotResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
