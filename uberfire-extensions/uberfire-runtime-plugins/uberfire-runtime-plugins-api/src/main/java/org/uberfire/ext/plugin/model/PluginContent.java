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

import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class PluginContent extends PluginSimpleContent {

    private Set<Media> mediaLibrary;

    public PluginContent() {
    }

    public PluginContent( final String name,
                          final PluginType type,
                          final Path path,
                          final String template,
                          final String css,
                          final Map<CodeType, String> codeMap,
                          final Set<Framework> frameworks,
                          final Language language,
                          final Set<Media> mediaLibrary ) {
        super( name, type, path, template, css, codeMap, frameworks, language );
        this.mediaLibrary = mediaLibrary;
    }

    public Set<Media> getMediaLibrary() {
        return mediaLibrary;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof PluginContent ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        PluginContent that = (PluginContent) o;

        if ( mediaLibrary != null ? !mediaLibrary.equals( that.mediaLibrary ) : that.mediaLibrary != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( mediaLibrary != null ? mediaLibrary.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
