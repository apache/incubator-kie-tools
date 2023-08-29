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


package org.kie.workbench.common.stunner.core.definition.adapter;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@JsType
public class DefinitionId {

    public final static String DYNAMIC_ID_DELIMITER = ".";

    private final String id;
    private final int dynamicIdStartIndex;

    public static DefinitionId build(final String id) {
        return build(id, -1);
    }

    @JsIgnore
    public static DefinitionId build(final String type,
                                     final String id) {
        final String value = generateId(type, id);
        return build(value, type.length());
    }

    @JsIgnore
    public static DefinitionId build(final String id,
                                     final int dynamicIdStartIndex) {
        return new DefinitionId(id, dynamicIdStartIndex);
    }

    public static String generateId(final String type,
                                    final String id) {
        return type + DYNAMIC_ID_DELIMITER + id;
    }

    public DefinitionId(final String id,
                        final int dynamicIdStartIndex) {
        this.id = id;
        this.dynamicIdStartIndex = dynamicIdStartIndex;
    }

    public String value() {
        return id;
    }

    public boolean isDynamic() {
        return dynamicIdStartIndex >= 0;
    }

    public String type() {
        return isDynamic() ?
                id.substring(0, dynamicIdStartIndex) :
                id;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id.hashCode(),
                                         dynamicIdStartIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefinitionId) {
            DefinitionId other = (DefinitionId) o;
            return id.equals(other.id) &&
                    dynamicIdStartIndex == other.dynamicIdStartIndex;
        }
        return false;
    }
}
