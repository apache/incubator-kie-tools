/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server.security;

public enum RoleType {
    /** Admin can do everything */
    ADMIN("admin"),
    /**
     * Analyst only see the "rules" view, and we specify what category paths they
     * can see. They can't create anything, only edit rules, and run tests etc,
     * but only things that are exposed to them via categories
     */
    ANALYST("analyst"),
    /**
     * Read only for categories (analyst view)
     */
    ANALYST_READ("analyst.readonly"),
    /** package.admin can do everything within this package */
    PACKAGE_ADMIN("package.admin"),
    /**
     * package.developer can do anything in that package but not snapshots. This
     * includes creating a new package (in which case they inherit permissions
     * for it).
     */
    PACKAGE_DEVELOPER("package.developer"),
    /**
     * Read only for package.
     */
    PACKAGE_READONLY("package.readonly");

    private final String name;

    RoleType(String name) {
        this.name = name;
    }

    //Only here because of backwards compatibility. Ideal would be to change codebase to use enums
    public String getName(){
        return name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
