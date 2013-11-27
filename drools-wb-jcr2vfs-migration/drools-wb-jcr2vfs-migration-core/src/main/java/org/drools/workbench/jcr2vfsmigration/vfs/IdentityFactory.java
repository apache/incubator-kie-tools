/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.jcr2vfsmigration.vfs;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import org.uberfire.security.Identity;
import org.uberfire.security.Role;

@Alternative
public class IdentityFactory {

    private Identity identity;

    @PostConstruct
    public void onStartup() {
        identity = new Identity() {

            @Override
            public String getName() {
                return "jcr2vfs-migration";
            }

            @Override
            public List<Role> getRoles() {
                return Collections.emptyList();
            }

            @Override
            public boolean hasRole( Role role ) {
                return true;
            }

            @Override
            public Map<String, String> getProperties() {
                return Collections.emptyMap();
            }

            @Override
            public void aggregateProperty( String name,
                                           String value ) {
            }

            @Override
            public void removeProperty( String name ) {
            }

            @Override
            public String getProperty( String name,
                                       String defaultValue ) {
                return null;
            }

        };
    }

    @Produces
    public Identity getIdentity() {
        return identity;
    }

}
