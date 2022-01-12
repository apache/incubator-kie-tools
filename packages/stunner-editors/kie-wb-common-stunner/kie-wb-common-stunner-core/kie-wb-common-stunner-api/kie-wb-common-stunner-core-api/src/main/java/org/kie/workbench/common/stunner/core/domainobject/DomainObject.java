/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.core.domainobject;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;

/**
 * A DomainObject belongs to the underlying model represented by the Graph but is not represented by a Node.
 * It is ordinarily itself a property of a Node Content that can have its properties displayed by the Properties Panel.
 * A DomainObject must adhere to the same requirements as Nodes bound to the Properties Panel; namely:
 * 1) It must be {@link Portable}
 * 2) It must be {@link Bindable}
 * 3) It must be a {@link Definition}
 * 4) It must have a {@link FormDefinition}
 * 5) It must have a {@link Category} property and associated public getter.
 * 6) It must have a {@link Labels} property and associated public getter.
 * 7) It must be included in a {@link DefinitionSet} declaration.
 * 8) It can have one or more {@link Property}
 */
public interface DomainObject {

    String getDomainObjectUUID();

    String getDomainObjectNameTranslationKey();
}
