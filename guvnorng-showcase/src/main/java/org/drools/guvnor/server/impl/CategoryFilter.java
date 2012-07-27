/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.impl;

//import org.drools.guvnor.server.security.CategoryPathType;
import org.drools.repository.RepositoryFilter;
//import org.jboss.seam.security.Identity;

public class CategoryFilter implements RepositoryFilter {

    //private final Identity identity;

    public CategoryFilter(/*Identity identity*/) {
        //this.identity = identity;
    }

    public boolean accept(Object artifact, String action) {
    	return true;
/*        if ( !(artifact instanceof String) ){
            return false;
        }
        if (identity == null) {
            // TODO for tests only ... Behaves like the pre-seam3 code: tests should be fixed to not require this!
            return true;
        }
        return identity.hasPermission( new CategoryPathType( (String) artifact ), action );
*/
    }

    public boolean acceptNavigate(String parentPath, String child) {
    	return true;
/*        if (identity == null) {
            // TODO for tests only ... Behaves like the pre-seam3 code: tests should be fixed to not require this!
            return true;
        }
        return identity.hasPermission( new CategoryPathType( makePath( parentPath, child ) ), "navigate" );
*/
    }

    String makePath(String parentPath, String child) {
        return (parentPath == null || parentPath.equals( "/" ) || parentPath.equals( "" )) ? child : parentPath + "/" + child;
    }

}
