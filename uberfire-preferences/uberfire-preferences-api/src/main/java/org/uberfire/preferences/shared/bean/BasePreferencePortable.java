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

package org.uberfire.preferences.shared.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.PropertyValidator;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;

/**
 * Represents a generated portable implementation of a preference bean.
 * @param <T> The preference bean type implementing the interface.
 */
public interface BasePreferencePortable<T> extends BasePreference<T> {

    /**
     * Returns the preference bean class.
     * @return The preference bean class.
     */
    Class<T> getPojoClass();

    /**
     * Unique identifier, used by this preference children to determine their parents.
     * Also used to name the file containing its value.
     * @return A unique identifier for the preference bean.
     */
    String identifier();

    /**
     * The preferences which will have this preference as their child.
     * All parents will share the same preference value.
     * @return The parents of this preference. Empty if there is not one.
     */
    String[] parents();

    /**
     * Returns the bundle key registered in the {@link WorkbenchPreference} annotation.
     * @return The preference bundle key.
     */
    String bundleKey();

    /**
     * Sets a property value by its name.
     * @param property Name of the property field to be set.
     * @param value New value for the property.
     */
    void set(String property,
             Object value);

    /**
     * Returns a property value by its name.
     * @param property Name of the property field to be fetched.
     * @return The property value.
     */
    Object get(String property);

    /**
     * Returns the form types of all simple (non-preference) properties by their field name.
     * @return The properties form types by their field names.
     */
    Map<String, PropertyFormType> getPropertiesTypes();

    default Map<String, List<PropertyValidator>> getPropertiesValidators() {
        return new HashMap<>();
    }

    /**
     * Returns the form type of a specific simple (non-preference) property by its field name.
     * @param propertyName The property field name.
     * @return The property form type.
     */
    default PropertyFormType getPropertyType(String propertyName) {
        return getPropertiesTypes().get(propertyName);
    }

    default List<PropertyValidator> getPropertyValidators(String propertyName) {
        final List<PropertyValidator> propertyValidators = getPropertiesValidators().get(propertyName);
        return propertyValidators != null ? propertyValidators : new ArrayList<>();
    }

    /**
     * A preference is persistable when it has at least one non-shared property.
     * @return true if it is persistable and false otherwise.
     */
    boolean isPersistable();
}
