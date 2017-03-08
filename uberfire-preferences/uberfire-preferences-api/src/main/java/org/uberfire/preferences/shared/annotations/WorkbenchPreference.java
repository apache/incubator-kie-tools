/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.preferences.shared.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Annotation used to qualify preference beans.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface WorkbenchPreference {

    /**
     * A unique identifier used to reference parent nodes (see #parents).
     * @return A unique identifier for the preference bean.
     */
    String identifier();

    /**
     * The identifiers of all parents of this preference.
     * @return The parents of this preference. Empty if there is not one.
     */
    @Nonbinding String[] parents() default {};

    /**
     * Defines a bundle key that will be used to internationalize the property's label wherever
     * its necessary. It's expected that the TranslationService will have access to the key
     * translation.
     * @return The preference bundle key.
     */
    String bundleKey() default "";
}
