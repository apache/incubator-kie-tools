package org.uberfire.backend.server.util.gzip;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import static javax.ws.rs.core.HttpHeaders.ACCEPT_ENCODING;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.backend.server.util.gzip.GzipFilter.Action.COMPRESS;
import static org.uberfire.backend.server.util.gzip.GzipFilter.Action.DO_NOT_ACCEPT_GZIP;
import static org.uberfire.backend.server.util.gzip.GzipFilter.Action.DO_NOT_COMPRESS;
import static org.uberfire.backend.server.util.gzip.GzipFilter.Action.HALT;
import static org.uberfire.backend.server.util.gzip.GzipFilter.GZIP;
import static org.uberfire.backend.server.util.gzip.GzipFilter.ORG_UBERFIRE_GZIP_ENABLE;

public class GzipFilterTest {

    @Before
    public void before() {
        System.clearProperty(ORG_UBERFIRE_GZIP_ENABLE);
    }

    @Test
    public void doFilter_compress() throws IOException, ServletException {
        final GzipFilter gzipFilter = spy(new GzipFilter());
        when(gzipFilter.getAction(any())).thenReturn(COMPRESS);

        final FilterChain chain = mock(FilterChain.class);
        gzipFilter.doFilter(mock(HttpServletRequest.class), mock(HttpServletResponse.class), chain);

        verify(gzipFilter, times(1)).compressAndContinue(any(), any(), any());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_doNotCompress() throws IOException, ServletException {
        final GzipFilter gzipFilter = spy(new GzipFilter());
        when(gzipFilter.getAction(any())).thenReturn(DO_NOT_COMPRESS);

        final FilterChain chain = mock(FilterChain.class);
        gzipFilter.doFilter(mock(HttpServletRequest.class), mock(HttpServletResponse.class), chain);

        verify(gzipFilter, never()).compressAndContinue(any(), any(), any());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_doNotAcceptGzip() throws IOException, ServletException {
        final GzipFilter gzipFilter = spy(new GzipFilter());
        when(gzipFilter.getAction(any())).thenReturn(DO_NOT_ACCEPT_GZIP);

        final FilterChain chain = mock(FilterChain.class);
        gzipFilter.doFilter(mock(HttpServletRequest.class), mock(HttpServletResponse.class), chain);

        verify(gzipFilter, never()).compressAndContinue(any(), any(), any());
        verify(chain, times(1)).doFilter(any(), any());
    }

    @Test
    public void doFilter_halt() throws IOException, ServletException {
        final GzipFilter gzipFilter = spy(new GzipFilter());
        when(gzipFilter.getAction(any())).thenReturn(HALT);

        final FilterChain chain = mock(FilterChain.class);
        gzipFilter.doFilter(mock(HttpServletRequest.class), mock(HttpServletResponse.class), chain);

        verify(gzipFilter, never()).compressAndContinue(any(), any(), any());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void getAction_notHttpServletRequest() {
        final GzipFilter gzipFilter = new GzipFilter();
        assertEquals(HALT, gzipFilter.getAction(mock(ServletRequest.class)));
    }

    @Test
    public void getAction_disableGzipCompressionViaSystemProperty() {
        final GzipFilter gzipFilter = new GzipFilter();

        System.setProperty(ORG_UBERFIRE_GZIP_ENABLE, "false");
        assertEquals(DO_NOT_COMPRESS, gzipFilter.getAction(mock(HttpServletRequest.class)));

        System.setProperty(ORG_UBERFIRE_GZIP_ENABLE, "falsy");
        assertEquals(DO_NOT_COMPRESS, gzipFilter.getAction(mock(HttpServletRequest.class)));
    }

    @Test
    public void getAction_doNotAcceptEncodingGzip() {
        final GzipFilter gzipFilter = new GzipFilter();
        final HttpServletRequest mock = mock(HttpServletRequest.class);

        assertEquals(DO_NOT_ACCEPT_GZIP, gzipFilter.getAction(mock));

        when(mock.getHeader(eq(ACCEPT_ENCODING))).thenReturn("foo");
        assertEquals(DO_NOT_ACCEPT_GZIP, gzipFilter.getAction(mock));
    }

    @Test
    public void getAction_acceptEncodingGzip() {
        final GzipFilter gzipFilter = new GzipFilter();

        final HttpServletRequest mock = mock(HttpServletRequest.class);
        when(mock.getHeader(eq(ACCEPT_ENCODING))).thenReturn(GZIP);

        assertEquals(COMPRESS, gzipFilter.getAction(mock));
    }
}