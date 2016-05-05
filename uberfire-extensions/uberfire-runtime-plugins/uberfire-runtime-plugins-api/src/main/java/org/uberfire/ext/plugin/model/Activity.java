/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Activity {

    private String name;
    private PluginType type;

    public Activity() {
    }

    public Activity( final String name,
                     final PluginType type ) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public PluginType getType() {
        return type;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Activity ) ) {
            return false;
        }

        return name.equals( ( (Activity) o ).name );
    }

    @Override
    public int hashCode() {
        return ~~name.hashCode();
    }
}
