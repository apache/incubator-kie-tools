package org.uberfire.ext.plugin.editor;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ScreenParameter {

    private String key;
    private String value;

    public ScreenParameter() {
    }

    public ScreenParameter( String key,
                            String value ) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ScreenParameter ) ) {
            return false;
        }

        ScreenParameter that = (ScreenParameter) o;

        if ( key != null ? !key.equals( that.key ) : that.key != null ) {
            return false;
        }
        if ( value != null ? !value.equals( that.value ) : that.value != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + ( value != null ? value.hashCode() : 0 );
        return result;
    }
}
