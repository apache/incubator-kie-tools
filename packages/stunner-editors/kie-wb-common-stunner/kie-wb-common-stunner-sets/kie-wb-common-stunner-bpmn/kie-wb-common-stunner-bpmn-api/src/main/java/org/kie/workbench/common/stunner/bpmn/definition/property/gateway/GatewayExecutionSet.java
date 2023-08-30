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


package org.kie.workbench.common.stunner.bpmn.definition.property.gateway;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;

@Portable
@Bindable
@FormDefinition
public class GatewayExecutionSet implements BPMNPropertySet {

    @Property
    @FormField(
            type = ListBoxFieldType.class

    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.DefaultRouteFormProvider")

    @Valid
    protected DefaultRoute defaultRoute;

    public GatewayExecutionSet() {
        this(new DefaultRoute());
    }

    public GatewayExecutionSet(final @MapsTo("defaultRoute") DefaultRoute defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    public GatewayExecutionSet(final String defaultRoute) {
        this.defaultRoute = new DefaultRoute(defaultRoute);
    }

    public DefaultRoute getDefaultRoute() {
        return defaultRoute;
    }

    public void setDefaultRoute(final DefaultRoute defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(defaultRoute);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GatewayExecutionSet) {
            GatewayExecutionSet other = (GatewayExecutionSet) o;
            return Objects.equals(defaultRoute,
                                  other.defaultRoute);
        }
        return false;
    }
}
