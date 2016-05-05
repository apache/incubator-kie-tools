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
import org.uberfire.backend.vfs.Path;

@Portable
public class Plugin extends Activity {

    private Path path;

    public Plugin() {
    }

    public Plugin( final String name,
                   final PluginType type,
                   final Path path ) {
        super( name, type );
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Plugin ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        Plugin plugin = (Plugin) o;

        if ( path != null ? !path.equals( plugin.path ) : plugin.path != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
