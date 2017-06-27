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

package org.uberfire.preferences.shared.bean.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.PropertyValidator;
import org.uberfire.preferences.shared.annotations.PortablePreference;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;

/**
 * Created to be used in tests, and to avoid Errai errors due to unimplemented
 * interfaces used in portable classes.
 */
@Portable(mapSuperTypes = true)
@PortablePreference
public class PortablePreferenceMockPortableGeneratedImpl extends PortablePreferenceMock implements BasePreferencePortable<PortablePreferenceMock> {

    public PortablePreferenceMockPortableGeneratedImpl() {
    }

    public PortablePreferenceMockPortableGeneratedImpl(@MapsTo("property") java.lang.String property) {
        this.property = property;
    }

    @Override
    public Class<PortablePreferenceMock> getPojoClass() {
        return PortablePreferenceMock.class;
    }

    @Override
    public String identifier() {
        return "org.uberfire.preferences.shared.bean.PortablePreferenceMock";
    }

    @Override
    public String[] parents() {
        return new String[]{""};
    }

    @Override
    public String bundleKey() {
        return "org.uberfire.preferences.shared.bean.PortablePreferenceMock";
    }

    @Override
    public void set(String property,
                    Object value) {
        if (property.equals("property")) {
            property = (java.lang.String) value;
        } else {
            throw new RuntimeException("Unknown property: " + property);
        }
    }

    @Override
    public Object get(String property) {
        if (property.equals("property")) {
            return property;
        } else {
            throw new RuntimeException("Unknown property: " + property);
        }
    }

    @Override
    public Map<String, PropertyFormType> getPropertiesTypes() {
        Map<String, PropertyFormType> propertiesTypes = new HashMap<>();

        propertiesTypes.put("property",
                            PropertyFormType.TEXT);

        return propertiesTypes;
    }

    @Override
    public Map<String, List<PropertyValidator>> getPropertiesValidators() {
        Map<String, List<PropertyValidator>> validatorsByProperty = new HashMap<>();

        List<PropertyValidator> validatorsProperty = new ArrayList<>();
        validatorsProperty.add(new org.uberfire.preferences.shared.impl.validation.NotEmptyValidator());
        validatorsByProperty.put("property",
                                 validatorsProperty);

        return validatorsByProperty;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PortablePreferenceMockPortableGeneratedImpl that = (PortablePreferenceMockPortableGeneratedImpl) o;

        if (property != null ? !property.equals(that.property) : that.property != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;

        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = ~~result;

        return result;
    }

    @Override
    public boolean isPersistable() {
        return true;
    }
}
