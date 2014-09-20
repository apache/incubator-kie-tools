package org.kie.uberfire.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RuntimePlugin {

    private String style;
    private String script;

    public RuntimePlugin() {
    }

    public RuntimePlugin( final String style,
                          final String script ) {
        this.style = style;
        this.script = script;
    }

    public String getStyle() {
        return style;
    }

    public String getScript() {
        return script;
    }
}
