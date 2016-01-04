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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RuntimePlugin {

    private String style;
    private String script;

    public RuntimePlugin( @MapsTo("style") final String style,
                          @MapsTo("script") final String script ) {
        this.style = style;
        this.script = script;
    }

    public String getStyle() {
        return style;
    }

    public String getScript() {
        return script;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof RuntimePlugin ) ) {
            return false;
        }

        RuntimePlugin that = (RuntimePlugin) o;

        if ( script != null ? !script.equals( that.script ) : that.script != null ) {
            return false;
        }
        if ( style != null ? !style.equals( that.style ) : that.style != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = style != null ? style.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( script != null ? script.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
