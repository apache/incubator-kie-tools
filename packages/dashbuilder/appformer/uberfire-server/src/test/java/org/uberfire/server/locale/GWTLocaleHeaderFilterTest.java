/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.server.locale;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Scanner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore("Broken due prettier formatting")
public class GWTLocaleHeaderFilterTest {

    @Test
    public void testLocaleDefault() throws IOException, ServletException {
        final var localeHeaderFilter = getFilter();

        final Writer sw = new StringWriter();

        final HttpServletRequest req = mock(HttpServletRequest.class);
        final HttpServletResponse resp = mock(HttpServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);

        when(req.getLocale()).thenReturn(Locale.US);

        when(resp.getWriter()).thenReturn(new PrintWriter(sw));

        localeHeaderFilter.doFilter(req,
                                    resp,
                                    chain);

        assertEquals(new Scanner(getClass().getResourceAsStream("/expected-sample.html"),
                                 "UTF-8").useDelimiter("\\A").next(),
                     sw.toString());
    }

    @Test
    public void testLocaleWithLanguageParameter() throws IOException, ServletException {
        final var localeHeaderFilter = getFilter();

        final var sw = new StringWriter();

        final var req = mock(HttpServletRequest.class);
        final var resp = mock(HttpServletResponse.class);
        final var chain = mock(FilterChain.class);

        when(req.getParameter("locale")).thenReturn("ja");

        when(resp.getWriter()).thenReturn(new PrintWriter(sw));

        localeHeaderFilter.doFilter(req,
                                    resp,
                                    chain);

        assertEquals(new Scanner(getClass().getResourceAsStream("/expected-2-sample.html"),
                                 "UTF-8").useDelimiter("\\A").next(),
                     sw.toString());
    }

    @Test
    public void testLocaleWithLanguageAndCountryParameter() throws IOException, ServletException {
        final var localeHeaderFilter = getFilter();

        final var sw = new StringWriter();

        final var req = mock(HttpServletRequest.class);
        final var resp = mock(HttpServletResponse.class);
        final var chain = mock(FilterChain.class);

        when(req.getParameter("locale")).thenReturn("ja_JP");

        when(resp.getWriter()).thenReturn(new PrintWriter(sw));

        localeHeaderFilter.doFilter(req,
                                    resp,
                                    chain);

        assertEquals(new Scanner(getClass().getResourceAsStream("/expected-3-sample.html"),
                                 "UTF-8").useDelimiter("\\A").next(),
                     sw.toString());
    }

    @Test
    public void testNonExistentLocaleParameter() throws IOException, ServletException {
        final var localeHeaderFilter = getFilter();

        final var sw = new StringWriter();

        final var req = mock(HttpServletRequest.class);
        final var resp = mock(HttpServletResponse.class);
        final var chain = mock(FilterChain.class);

        when(req.getParameter("locale")).thenReturn("xxx_xxx");
        when(req.getLocale()).thenReturn(Locale.US);

        when(resp.getWriter()).thenReturn(new PrintWriter(sw));

        localeHeaderFilter.doFilter(req,
                                    resp,
                                    chain);

        assertEquals(new Scanner(getClass().getResourceAsStream("/expected-4-sample.html"),
                                 "UTF-8").useDelimiter("\\A").next(),
                     sw.toString());
    }

    private GWTLocaleHeaderFilter getFilter() {
        return new GWTLocaleHeaderFilter() {
            protected CharResponseWrapper getWrapper(final HttpServletResponse response) {
                final var wrapper = new CharResponseWrapper(response);
                final String text = new Scanner(getClass().getResourceAsStream("/sample.html"),
                                                "UTF-8").useDelimiter("\\A").next();
                try {
                    wrapper.getOutputStream().write(text.getBytes());
                } catch (final IOException ignored) {
                }
                return wrapper;
            }
        };
    }
}
