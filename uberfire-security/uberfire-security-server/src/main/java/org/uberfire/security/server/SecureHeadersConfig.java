package org.uberfire.security.server;

import javax.servlet.FilterConfig;


/**
 * HTTP headers related to security
 * For example: HSTS and Clickjacking mitigation support
 * <p/>
 * Note: This implementation has been borrowed from Aerogear Security.
 */
public class SecureHeadersConfig {

    private final Long maxAge;
    private final Boolean includeSubDomains;
    private final String location;
    private final String frameOptions;
    private final String xssOptions;

    public SecureHeadersConfig( final FilterConfig config ) {
        final String _maxAge = config.getInitParameter( "max-age" );
        if ( _maxAge != null ) {
            this.maxAge = Long.valueOf( _maxAge );
        } else {
            this.maxAge = null;
        }
        final String _includeSubDomains = config.getInitParameter( "include-subdomains" );
        if ( _includeSubDomains != null ) {
            this.includeSubDomains = Boolean.valueOf( _includeSubDomains );
        } else {
            this.includeSubDomains = false;
        }
        this.location = config.getInitParameter( "Location" );
        this.frameOptions = config.getInitParameter( "x-frame-options" );
        final String _xssOptionsEnable = config.getInitParameter( "x-xss-protection-enable" );
        final String _xssOptionsBlock = config.getInitParameter( "x-xss-protection-block" );
        String _xssOptions;
        if ( _xssOptionsEnable == null ) {
            xssOptions = null;
        } else {
            if ( toBoolean( _xssOptionsEnable, false ) ) {
                _xssOptions = "1";
            } else {
                _xssOptions = "0";
            }

            if ( toBoolean( _xssOptionsBlock, false ) ) {
                _xssOptions += "; mode=block";
            }
            xssOptions = _xssOptions;
        }
    }

    private boolean toBoolean( final String value,
                               final boolean defaultValue ) {
        try {
            return Boolean.valueOf( value );
        } catch ( Exception ex ) {
            return defaultValue;
        }
    }

    /**
     * Specifies the number of seconds, after the reception of the STS header field
     * @return max-age directive
     * @see <a href="https://tools.ietf.org/html/rfc6797#section-6.1.1">The max-age Directive</a>
     */
    public String getMaxAge() {
        final StringBuilder header = new StringBuilder( "max-age=" + maxAge );
        if ( includeSubDomains ) {
            header.append( "; includeSubdomains" );
        }
        return header.toString();
    }

    /**
     * Retrieve the Location header
     * @return Location header field value
     * @see <a href="https://tools.ietf.org/html/rfc6797#section-7.2">HTTP Request Type</a>
     */
    public String getLocation() {
        return location;
    }

    /**
     * Allows a secure web page from host B to declare
     * that its content (for example a button, links, text, etc.) must not
     * be displayed in a frame (<frame> or <iframe>) of another page (e.g.
     * from host A).  In principle this is done by a policy declared in the
     * HTTP header and enforced by conforming browser implementations
     * @return X-Frame-Options HTTP header field
     * @see <a href="https://tools.ietf.org/html/draft-ietf-websec-x-frame-options-02#section-1"> X-Frame-Options</a>
     */
    public String getFrameOptions() {
        return frameOptions;
    }

    /**
     * Verify if the option "max-age" is present
     * @return boolean
     */
    public boolean hasMaxAge() {
        return maxAge != null && maxAge >= 0;
    }

    /**
     * Verify if the option "Location" is present
     * @return boolean
     */
    public boolean hasLocation() {
        return isEmpty( location );
    }

    /**
     * Verify if "x-frame-options" is present
     * @return boolean
     */
    public boolean hasFrameOptions() {
        return isEmpty( frameOptions );
    }

    /**
     * Verify if "x-xss-protection" is present
     * @return boolean
     */
    public boolean hasXSSOptions() {
        return isEmpty( xssOptions );
    }

    public String getXssOptions() {
        return xssOptions;
    }

    private boolean isEmpty( final String value ) {
        return value != null && !value.trim().isEmpty();
    }
}