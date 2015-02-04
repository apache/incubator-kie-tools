package org.uberfire.ext.plugin.model;

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
