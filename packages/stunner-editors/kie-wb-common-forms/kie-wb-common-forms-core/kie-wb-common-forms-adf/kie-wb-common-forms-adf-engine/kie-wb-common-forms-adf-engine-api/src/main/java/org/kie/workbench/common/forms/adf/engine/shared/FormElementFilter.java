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

package org.kie.workbench.common.forms.adf.engine.shared;

import java.util.function.Predicate;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FormElement;

/**
 * Determines if a {@link FormElement} must be added to the form or not.
 * @param <T>
 */
public class FormElementFilter<T> {

    private String elementName;

    private Predicate<T> predicate;

    /**
     * @param elementName String containing the name of the form element. It does support nested filtering by adding
     * expressions like "element.childElement" so the filter will apply to the child element.
     * @param predicate Predicate to validate if the {@link FormElement} must be added to the form or not.
     */
    public FormElementFilter(String elementName, Predicate<T> predicate) {
        PortablePreconditions.checkNotNull("elementName", elementName);
        PortablePreconditions.checkNotNull("predicate", predicate);
        this.elementName = elementName;
        this.predicate = predicate;
    }

    public String getElementName() {
        return elementName;
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }
}
