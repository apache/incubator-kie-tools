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

package org.uberfire.experimental.definition.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an element as a workbench Experimental Feature. It can be used to annotate:
 * <p>
 * <ul>
 * <li>WorkbenchPerspectives</li>
 * <li>WorkbenchScreens</li>
 * <li>WorkbenchEditors</li>
 * <li>ResourceTypes</li>
 * <li>LayoutDragComponents</li>
 * </ul>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExperimentalFeature {

    /**
     * Scope of the ExperimentalFeature. By default a feature is user
     */
    Scope scope() default Scope.USER;

    /**
     * Determines the a logic group of features. When no group is defined features are added into a default group.
     */
    String group() default "";

    /**
     * Determines the i18n key to get the feature name if no key is defined the class name will be used as a key
     */
    String nameI18nKey() default "";

    /**
     * Determines the i18n key to get the description of the feature. If no key is defined it won't have any description
     */
    String descriptionI18nKey() default "";

    /**
     * Determines the scope of the feature.
     */
    enum Scope {
        /**
         * Experimental features with GLOBAL scope can only be enabled/disabled by admin users.
         */
        GLOBAL,
        /**
         * Experimental features with USER scope can be enabled/disabled by each user.
         */
        USER
    }
}

