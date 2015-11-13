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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class PluginSimpleContent extends Plugin {

    private String template;
    private String css;
    private Map<CodeType, String> codeMap;
    private Set<Framework> frameworks = new HashSet<Framework>();
    private Language language;

    public PluginSimpleContent() {
    }

    public PluginSimpleContent( final String name,
                                final PluginType type,
                                final Path path,
                                final String template,
                                final String css,
                                final Map<CodeType, String> codeMap,
                                final Set<Framework> frameworks,
                                final Language language ) {
        super( name, type, path );
        this.template = template;
        this.css = css;
        this.codeMap = codeMap;
        this.frameworks = frameworks;
        this.language = language;
    }

    public PluginSimpleContent( final PluginSimpleContent pluginContent,
                                final String template,
                                final String css,
                                final Map<CodeType, String> codeMap,
                                final Collection<Framework> frameworks,
                                final Language language ) {
        super( pluginContent.getName(), pluginContent.getType(), pluginContent.getPath() );
        this.codeMap = new HashMap<CodeType, String>( pluginContent.getCodeMap() != null ? pluginContent.getCodeMap() : Collections.<CodeType, String>emptyMap() );
        if ( codeMap != null && !codeMap.isEmpty() ) {
            for ( final Map.Entry<CodeType, String> codeTypeStringEntry : codeMap.entrySet() ) {
                this.codeMap.put( codeTypeStringEntry.getKey(), codeTypeStringEntry.getValue() );
            }
        }

        if (frameworks != null) {
            this.frameworks.clear();
            this.frameworks.addAll(frameworks);
        }

        this.language = pluginContent.getLanguage();
        if ( language != null ) {
            this.language = language;
        }

        this.template = pluginContent.getTemplate();
        if ( template != null ) {
            this.template = template;
        }

        this.css = pluginContent.getCss();
        if ( css != null ) {
            this.css = css;
        }
    }

    public PluginSimpleContent( final PluginSimpleContent pluginContent,
                                final String template,
                                final Map<CodeType, String> codeMap ) {
        super( pluginContent.getName(), pluginContent.getType(), pluginContent.getPath() );
        this.codeMap = new HashMap<CodeType, String>( pluginContent.getCodeMap() != null ? pluginContent.getCodeMap() : Collections.<CodeType, String>emptyMap() );
        if ( codeMap != null && !codeMap.isEmpty() ) {
            for ( final Map.Entry<CodeType, String> codeTypeStringEntry : codeMap.entrySet() ) {
                this.codeMap.put( codeTypeStringEntry.getKey(), codeTypeStringEntry.getValue() );
            }
        }
        this.template = pluginContent.getTemplate();
        if ( template != null ) {
            this.template = template;
        }
        this.css = pluginContent.getCss();
        this.frameworks = new HashSet<Framework>( pluginContent.getFrameworks() != null ? pluginContent.getFrameworks() : Collections.<Framework>emptyList() );
        this.language = pluginContent.getLanguage();
    }

    public String getTemplate() {
        return template;
    }

    public String getCss() {
        return css;
    }

    public Map<CodeType, String> getCodeMap() {
        return codeMap;
    }

    public Collection<Framework> getFrameworks() {
        return frameworks;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof PluginSimpleContent ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        PluginSimpleContent that = (PluginSimpleContent) o;

        if ( codeMap != null ? !codeMap.equals( that.codeMap ) : that.codeMap != null ) {
            return false;
        }
        if ( css != null ? !css.equals( that.css ) : that.css != null ) {
            return false;
        }
        if ( frameworks != null ? !frameworks.equals( that.frameworks ) : that.frameworks != null ) {
            return false;
        }
        if ( language != that.language ) {
            return false;
        }
        if ( template != null ? !template.equals( that.template ) : that.template != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( template != null ? template.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( css != null ? css.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( codeMap != null ? codeMap.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( frameworks != null ? frameworks.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( language != null ? language.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
