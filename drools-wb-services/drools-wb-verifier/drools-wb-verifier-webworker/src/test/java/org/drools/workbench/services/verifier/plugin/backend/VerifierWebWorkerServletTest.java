/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.plugin.backend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VerifierWebWorkerServletTest {

    private VerifierWebWorkerServlet servlet;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ServletOutputStream servletOutputStream;

    @Before
    public void setUp() throws
                        Exception {
        servlet = new VerifierWebWorkerServlet();
        when( httpServletResponse.getOutputStream() ).thenReturn( servletOutputStream );
    }

    @Test
    public void loadMainFile() throws
                               Exception {

        when( httpServletRequest.getRequestURI() ).thenReturn( "/verifier/VerifierWebWorker.nocache.js" );

        downloadStarts();
    }

    @Test
    public void loadActualWebWorkerFile() throws
                                          Exception {

        when( httpServletRequest.getRequestURI() ).thenReturn( "/verifier/0BD650E7DC9A4B57B8AFCE8F27AACA84.cache.js" );

        downloadStarts();
    }

    @Test
    public void fileNameNeedsToContainVerifier() throws
                                                 Exception {

        when( httpServletRequest.getRequestURI() ).thenReturn( "0BD650E7DC9A4B57B8AFCE8F27AACA84.cache.js" );

        downloadDoesNotStart();
    }

    @Test
    public void fileNameNeedsToEndWithNoCacheJS() throws
                                                  Exception {

        when( httpServletRequest.getRequestURI() ).thenReturn( "/verifier/0BD650E7DC9A4B57B8AFCE8F27AACA84.cache" );

        downloadDoesNotStart();
    }

    private void downloadStarts() throws
                                  ServletException,
                                  IOException {
        servlet.doGet( httpServletRequest,
                       httpServletResponse );


        verify( httpServletResponse ).getOutputStream();
    }

    private void downloadDoesNotStart() throws
                                        ServletException,
                                        IOException {
        servlet.doGet( httpServletRequest,
                       httpServletResponse );


        verify( httpServletResponse,
                never() ).getOutputStream();
    }
}