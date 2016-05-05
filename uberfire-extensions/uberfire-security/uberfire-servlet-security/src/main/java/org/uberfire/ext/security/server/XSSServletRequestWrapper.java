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

package org.uberfire.ext.security.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Note: This implementation has been borrowed from Aerogear Security.
 */
public class XSSServletRequestWrapper extends HttpServletRequestWrapper {

    public XSSServletRequestWrapper( final HttpServletRequest request ) {
        super( request );
    }

    @Override
    public String[] getParameterValues( final String param ) {
        final String[] values = super.getParameterValues( param );

        for ( int i = 0; i < values.length; i++ ) {
            values[ i ] = StringEscapeUtils.escapeHtml4( values[ i ] );
        }

        return values;
    }

    @Override
    public String getParameter( final String param ) {
        return StringEscapeUtils.escapeHtml4( super.getParameter( param ) );
    }

}
