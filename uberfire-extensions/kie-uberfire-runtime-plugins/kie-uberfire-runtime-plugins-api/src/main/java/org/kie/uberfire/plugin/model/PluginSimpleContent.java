package org.kie.uberfire.plugin.model;

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
    private Set<Framework> frameworks;
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
        this.frameworks = new HashSet<Framework>( pluginContent.getFrameworks() != null ? pluginContent.getFrameworks() : Collections.<Framework>emptyList() );
        if ( frameworks != null && !frameworks.isEmpty() ) {
            for ( final Framework framework : frameworks ) {
                this.frameworks.add( framework );
            }
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

}
