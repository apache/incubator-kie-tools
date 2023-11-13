/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.uberfire.workbench.model;

import java.util.Objects;

/**
 * Represents the position of a child panel by name. For example, within a templated perspective, panels are positioned
 * by ErraiUI DataField names.
 * <p>
 * Positions that refer to the same field name compare equal to each other.
 * <p>
 * Instances of this class are immutable.
 */
public class NamedPosition implements Position {

    /**
     * Represents the root panel of any templated perspective.
     */
    public static final NamedPosition ROOT = new NamedPosition("");

    private final String fieldName;

    /**
     * Creates a new position representing the ErraiUI {@code @DataField} with the given name.
     * If you are trying to refer to the root of the template itself, use {@link #ROOT}.
     *
     * @param fieldName the name of the data field. Must be non-null.
     */
    public NamedPosition(String fieldName) {
        this.fieldName = Objects.requireNonNull(fieldName, "Parameter named 'fieldName' should be not null!");
    }

    /**
     * Returns the data field name this Position refers to.
     *
     * @return a non-null string. For the special {@link #ROOT} position constant, this is the empty string.
     */
    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NamedPosition other = (NamedPosition) obj;
        if (fieldName == null) {
            if (other.fieldName != null) {
                return false;
            }
        } else if (!fieldName.equals(other.fieldName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NamedPosition{" +
                "fieldName='" + fieldName + '\'' +
                '}';
    }
}
