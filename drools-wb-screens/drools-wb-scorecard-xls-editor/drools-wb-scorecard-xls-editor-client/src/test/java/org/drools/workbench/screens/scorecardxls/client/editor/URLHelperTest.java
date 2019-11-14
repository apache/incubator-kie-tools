/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scorecardxls.client.editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class URLHelperTest {

    @Test
    public void getDownloadUrl() {

        final Path path = mock(Path.class);

        when(path.toURI()).thenReturn("default://master@MySpace/Mortgages/src/main/resources/a & b.scorecard.xls");

        final String downloadUrl = URLHelper.getDownloadUrl(path, "clientId");

        assertEquals("scorecardxls/file?clientId=clientId&attachmentPath=default%3A%2F%2Fmaster%40MySpace%2FMortgages%2Fsrc%2Fmain%2Fresources%2Fa+%26+b.scorecard.xls", downloadUrl);
    }
}