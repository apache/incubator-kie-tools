/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.gateway;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition
public class ExclusiveGatewayExecutionSet implements BPMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FormField(
            type = ListBoxFieldType.class

    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.forms.fields.gateway.DefaultRouteFormProvider")

    @Valid
    protected DefaultRoute defaultRoute;

    public ExclusiveGatewayExecutionSet() {
        this(new DefaultRoute());
    }

    public ExclusiveGatewayExecutionSet(final @MapsTo("defaultRoute") DefaultRoute defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    public ExclusiveGatewayExecutionSet(final String defaultRoute) {
        this.defaultRoute = new DefaultRoute(defaultRoute);
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public DefaultRoute getDefaultRoute() {
        return defaultRoute;
    }

    public void setDefaultRoute(final DefaultRoute defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    @Override
    public int hashCode() {
        return defaultRoute.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExclusiveGatewayExecutionSet) {
            ExclusiveGatewayExecutionSet other = (ExclusiveGatewayExecutionSet) o;
            return defaultRoute.equals(other.defaultRoute);
        }
        return false;
    }
}
