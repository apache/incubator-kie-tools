/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

/** A field, bean property, or enum constant of a {@link DeclaredType} or Java class. */
public class Field {

    public final String name;
    public final String type;
    /**
     * For enum constants declared with constructor arguments
     * (e.g. {@code LOW(1)}), the raw argument list as written in source —
     * without the surrounding parentheses. {@code null} for ordinary fields
     * and for enum constants without arguments.
     */
    public final String args;

    Field(String name, String type) {
        this(name, type, null);
    }

    Field(String name, String type, String args) {
        this.name = name;
        this.type = type;
        this.args = args;
    }
}
