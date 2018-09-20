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

package org.drools.workbench.services.verifier.plugin.client.api;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;

@Portable
public class FactTypes {

    private final Set<FactType> factTypes;

    public FactTypes() {
        this(new HashSet<>());
    }

    public FactTypes(@MapsTo("factTypes") final Set<FactType> factTypes) {
        this.factTypes = PortablePreconditions.checkNotNull("factTypes",
                                                            factTypes);
    }

    public FactType getFactType(final String factTypeName) {
        for (final FactType factType : factTypes) {
            if (factType.getName()
                    .equals(factTypeName)) {
                return factType;
            }
        }

        return null;
    }

    public Set<FactType> getFactTypes() {
        return factTypes;
    }

    public String getFieldType(final String factTypeName,
                               final String fieldName) {

        PortablePreconditions.checkNotNull("factTypeName",
                                           factTypeName);
        PortablePreconditions.checkNotNull("fieldName",
                                           fieldName);

        final FactType factType = getFactType(factTypeName);
        if (factType == null) {
            return null;
        }

        for (final Field field : factType.getFields()) {
            if (field.getFieldName()
                    .equals(fieldName)) {
                return field.getType();
            }
        }

        return null;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        for (final FactType key : factTypes) {
            builder.append(key.getName());
            builder.append("{");

            for (final Field field : key.getFields()) {
                builder.append(field.toString());
                builder.append(", ");
            }
            builder.append("}");
        }

        return builder.toString();
    }

    public void add(final FactType factType) {
        factTypes.add(factType);
    }

    @Portable
    public static class FactType {

        private final String name;

        private final Set<Field> fields;

        public FactType(@MapsTo("name") final String name,
                        @MapsTo("fields") final Set<Field> fields) {
            this.name = name;
            this.fields = fields;
        }

        public String getName() {
            return name;
        }

        public Set<Field> getFields() {
            return fields;
        }
    }

    @Portable
    public static class Field {

        private final String fieldName;
        private final String type;

        public Field(@MapsTo("fieldName") final String fieldName,
                     @MapsTo("type") final String type) {

            this.fieldName = fieldName;
            this.type = type;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "fieldName='" + fieldName + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
