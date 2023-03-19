/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.component;

import org.dashbuilder.external.model.ExternalComponent;

public class ExternalComponentBuilder extends AbstractComponentBuilder<ExternalComponentBuilder> {

    private String componentId;

    ExternalComponentBuilder(String componentId) {
        this.componentId = componentId;
        property(ExternalComponent.COMPONENT_ID_KEY, this.componentId);
    }

    public static ExternalComponentBuilder create(String componentId) {
        return new ExternalComponentBuilder(componentId);
    }

    public ExternalComponentBuilder componentProperty(String key, String value) {
        property(this.componentId + "." + key, value);
        return this;
    }
    
    public ExternalComponentBuilder partition(String value) {
        property(ExternalComponent.COMPONENT_PARTITION_KEY, value);
        return this;
    }

    @Override
    String getDragType() {
        return "org.dashbuilder.client.editor.external.ExternalDragComponent";
    }

}
