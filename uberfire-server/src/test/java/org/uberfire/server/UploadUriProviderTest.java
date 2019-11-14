/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UploadUriProviderTest {

    private static final String PARAM_PATH = "path";
    private static final String PARAM_FOLDER = "folder";
    private static final String PARAM_FILENAME = "fileName";

    @Test(expected = FileUploadException.class)
    public void requestEmpty() throws URISyntaxException, FileUploadException {
        final HttpServletRequest request = mock(HttpServletRequest.class);

        UploadUriProvider.getTargetLocation(request);
    }

    @Test
    public void paramIsNotEncoded() throws URISyntaxException, FileUploadException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("project/hello & goodbye.txt").when(request).getParameter(PARAM_PATH);

        final URI uri = UploadUriProvider.getTargetLocation(request);
        assertEquals("project/hello+%26+goodbye.txt", uri.toString());
    }

    @Test
    public void paramIsEncoded() throws URISyntaxException, FileUploadException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("project/hello+%26+goodbye.txt").when(request).getParameter(PARAM_PATH);

        final URI uri = UploadUriProvider.getTargetLocation(request);
        assertEquals("project/hello+%26+goodbye.txt", uri.toString());
    }

    @Test
    public void fileNameIsNotEncoded() throws URISyntaxException, FileUploadException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("otherProject").when(request).getParameter(PARAM_FOLDER);
        doReturn("blaa & blaa.txt").when(request).getParameter(PARAM_FILENAME);

        final URI uri = UploadUriProvider.getTargetLocation(request);
        assertEquals("otherProject/blaa+%26+blaa.txt", uri.toString());
    }

    @Test
    public void fileNameIsEncoded() throws URISyntaxException, FileUploadException {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        doReturn("otherProject/subfolder").when(request).getParameter(PARAM_FOLDER);
        doReturn("do+%26+it.txt").when(request).getParameter(PARAM_FILENAME);

        final URI uri = UploadUriProvider.getTargetLocation(request);
        assertEquals("otherProject/subfolder/do+%26+it.txt", uri.toString());
    }
}