/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class ScoreCardXLSEditorViewImplTest {

    private ScoreCardXLSEditorViewImpl view;

    @Before
    public void setup() {
        view = new ScoreCardXLSEditorViewImpl() {

            @Override
            String getClientId() {
                return "123";
            }
        };
    }

    @Test
    public void getServletUrl() throws Exception {
        assertEquals( "scorecardxls/file?clientId=123", view.getServletUrl() );
    }

    @Test
    public void getDownloadUrl() throws Exception {
        assertEquals( "scorecardxls/file?clientId=123&attachmentPath=", view.getDownloadUrl( path() ) );

    }

    private Path path() {
        return new Path() {
            @Override
            public String getFileName() {
                return "";
            }

            @Override
            public String toURI() {
                return "";
            }

            @Override
            public int compareTo( final Path o ) {
                return 0;
            }
        };
    }
}