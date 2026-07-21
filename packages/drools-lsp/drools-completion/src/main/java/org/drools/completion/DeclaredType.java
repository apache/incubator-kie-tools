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

import java.util.List;

/** A type declared in DRL via a {@code declare} block. */
public class DeclaredType {

    public final String name;
    public final List<Field> fields;
    public final boolean isEnum;
    /** 0-based line of the type name token (for future go-to-definition). */
    public final int nameLine;
    /** 0-based column of the type name token (for future go-to-definition). */
    public final int nameCol;
    /**
     * Simple name of the parent type from {@code extends}, or {@code null}
     * when no inheritance is declared.
     */
    public final String extendsName;

    DeclaredType(String name, List<Field> fields, boolean isEnum,
                 int nameLine, int nameCol) {
        this(name, fields, isEnum, nameLine, nameCol, null);
    }

    DeclaredType(String name, List<Field> fields, boolean isEnum,
                 int nameLine, int nameCol, String extendsName) {
        this.name = name;
        this.fields = fields;
        this.isEnum = isEnum;
        this.nameLine = nameLine;
        this.nameCol = nameCol;
        this.extendsName = extendsName;
    }
}
