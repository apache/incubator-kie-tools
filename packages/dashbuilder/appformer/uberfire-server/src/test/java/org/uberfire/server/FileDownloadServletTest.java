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

package org.uberfire.server;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dashbuilder.project.storage.impl.ProjectStorageServicesImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileDownloadServletTest {

    private static final String PARAM_PATH = "path";

    private static final String TEST_ROOT_PATH = "file:/test-project/src/main/resources/test";

    @Mock
    ServletOutputStream servletOutputStream;
    

    private FileDownloadServlet downloadServlet;

    private ProjectStorageServicesImpl projectStorageServices;
    
    @Before
    public void setup() {
        projectStorageServices = new ProjectStorageServicesImpl();
        downloadServlet = new FileDownloadServlet();
        downloadServlet.setProjectStorageServices(projectStorageServices);
        
        projectStorageServices.clear();
        projectStorageServices.createStructure();
    }

    /**
     * Tests the downloading of a file given the following parameters:
     * <p>
     * 1) the file path on the server side of a file with no blank spaces in the name.
     */
    @Test
    public void downloadByPathWithNoSpaces() throws Exception {

        //test the download of a file name with no blank spaces.
        var fileName = "FileNameWithNoSpaces.someextension";
        var fileContent = "the local file content";
        
        
        projectStorageServices.createTempContent(fileName, fileContent);
        doDownloadByPath(TEST_ROOT_PATH,
                         fileName,
                         fileContent);
    }

    private void doDownloadByPath(String sourceFolder,
                                  String sourceFileName,
                                  String fileContent) throws Exception {

        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);

        var sourcePath = sourceFolder + "/" + sourceFileName;

        //mock the servlet parameters
        when(request.getParameter(PARAM_PATH)).thenReturn(sourcePath);

        //mock the servlet output stream
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        downloadServlet.doGet(request,
                              response);

        verify(response,
               times(1)).setHeader("Content-Disposition",
                                   format("attachment; filename=\"%s\";",
                                          sourceFileName));
        verify(response,
               times(1)).setContentType(eq("application/octet-stream"));
        verify(response,
               times(1)).getOutputStream();

        verify(servletOutputStream,
               times(1)).write(fileContent.getBytes(),
                               0,
                               fileContent.getBytes().length);

    }
}
