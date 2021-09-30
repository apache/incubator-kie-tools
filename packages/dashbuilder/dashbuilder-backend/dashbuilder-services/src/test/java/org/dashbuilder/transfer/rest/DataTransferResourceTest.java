/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.transfer.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.ws.rs.core.Response;

import org.dashbuilder.transfer.DataTransferServices;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DataTransferResourceTest {

    @Mock
    private DataTransferServices dataTransferServices;

    @InjectMocks
    DataTransferResource dataTransferResource;

    @Test
    public void testSuccessExport() throws IOException, URISyntaxException {
        var empty = getClass().getResource("/empty.zip").toURI();
        
        when(dataTransferServices.doExport(any())).thenReturn(Paths.get(empty).toString());
        var response = dataTransferResource.export();
        assertEquals(Response.Status.OK.getStatusCode(),
                     response.getStatus());
    }

    @Test
    public void testBadExport() throws IOException {
        when(dataTransferServices.doExport(any())).thenThrow(new IOException());
        var response = dataTransferResource.export();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                     response.getStatus());
    }

}