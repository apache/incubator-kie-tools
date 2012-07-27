/*
 * Copyright 2010 JBoss Inc
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

import java.lang.reflect.Field;
/**
 * @deprecated use org.drools.guvnor.server.security.RoleType
 */
@Deprecated
public class RoleTypes {
    

    /** Admin can do everything */
    public final static String ADMIN             = "admin";

    /**
     * Analyst only see the "rules" view, and we specify what category paths they
     * can see. They can't create anything, only edit rules, and run tests etc,
     * but only things that are exposed to them via categories
     */
    public final static String ANALYST           = "analyst";

    /**
     * Read only for categories (analyst view)
     */
    public final static String ANALYST_READ      = "analyst.readonly";

    /** package.admin can do everything within this package */
    public final static String PACKAGE_ADMIN     = "package.admin";

    /**
     * package.developer can do anything in that package but not snapshots. This
     * includes creating a new package (in which case they inherit permissions
     * for it).
     */
    public final static String PACKAGE_DEVELOPER = "package.developer";

    /**
     * Read only for package.
     */
    public final static String PACKAGE_READONLY  = "package.readonly";

    /**
     * @return A list of all available types.
     */
    public static String[] listAvailableTypes() {
        try {
            Field[] flds = RoleTypes.class.getFields();
            String[] r = new String[flds.length];
            for ( int i = 0; i < flds.length; i++ ) {
                r[i] = flds[i].get( RoleTypes.class ).toString();
            }
            return r;
        } catch ( Exception e ) {
            throw new IllegalStateException( "Can't get list of permission types." );
        }
    }
}
