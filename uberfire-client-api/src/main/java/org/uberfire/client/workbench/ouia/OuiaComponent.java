/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.ouia;

import java.util.function.Consumer;

/**
 * This represents OUIA compliant components (https://ouia.readthedocs.io/en/latest/README.html#ouia-component)
 * OUIA compliant elements are leveraged by advanced UI testing tools
 */
public interface OuiaComponent {

    /**
     * Initializes OUIA attributes of given element
     */
    default void initOuiaComponentAttributes() {
        ouiaAttributeRenderer().accept(ouiaComponentType());
        ouiaAttributeRenderer().accept(ouiaComponentId());
    }

    /**
     * Returns 'data-ouia-component-type' attribute for given element
     * @return 'data-ouia-component-type' attribute value
     */
    OuiaComponentTypeAttribute ouiaComponentType();

    /**
     * Returns 'data-ouia-component-id' attribute for given element
     * @return 'data-ouia-component-id' attribute value
     */
    OuiaComponentIdAttribute ouiaComponentId();

    /**
     * Renderer of any OUIA attribute. Invoking of this renderer should display given attribute in produced html.
     * @return OUIA attribute renderer
     */
    Consumer<OuiaAttribute> ouiaAttributeRenderer();
}
