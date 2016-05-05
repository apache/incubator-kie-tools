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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.security.server.CacheHeadersFilter.CACHE_CONTROL_HEADER;
import static org.uberfire.ext.security.server.CacheHeadersFilter.EXPIRES_HEADER;
import static org.uberfire.ext.security.server.CacheHeadersFilter.PRAGMA_HEADER;

import java.util.Calendar;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CacheHeadersFilterTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain chain;
    
    @Test
    public void cacheFilesWithCacheExtension() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/hash.cache.js");

        final CacheHeadersFilter filter = new CacheHeadersFilter();
        filter.doFilter( request, response, chain );
        verify(response).setHeader( CACHE_CONTROL_HEADER , "max-age=31536000, must-revalidate" );
        
        ArgumentCaptor<String> expiresHeader = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> expiresValue = ArgumentCaptor.forClass(Long.class);
        verify(response).setDateHeader( expiresHeader.capture(), expiresValue.capture() );
        assertEquals(EXPIRES_HEADER, expiresHeader.getValue());
        
        final Calendar expiryDate= Calendar.getInstance();
        expiryDate.setTimeInMillis( expiresValue.getValue() );
        final Calendar now = Calendar.getInstance();
        long expiryInDays = (expiryDate.getTimeInMillis() - now.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        assertTrue(expiryInDays >= 364);
    }
    
    @Test
    public void doNotCacheFilesWithNoCacheExtension() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/abc.nocache.js");
        verifyNoCache();
    }
    
    @Test
    public void doNotCacheHostPage() throws Exception {
        when(request.getRequestURI()).thenReturn("/host-page.html");
        verifyNoCache();
    }
    
    private void verifyNoCache() throws Exception {
        final CacheHeadersFilter filter = new CacheHeadersFilter();
        filter.doFilter( request, response, chain );
        
        verify(response).setHeader( CACHE_CONTROL_HEADER , "no-cache, no-store, must-revalidate" );
        verify(response).setDateHeader( EXPIRES_HEADER , 0 );
        verify(response).setHeader( PRAGMA_HEADER , "no-cache" );
    }
}
