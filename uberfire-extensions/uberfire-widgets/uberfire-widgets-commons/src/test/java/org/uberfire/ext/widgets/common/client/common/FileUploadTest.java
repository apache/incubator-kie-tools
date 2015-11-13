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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Input;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class FileUploadTest {

    @Mock
    Input file;

    @Mock
    Input fileText;

    @InjectMocks
    private FileUpload fileUpload;

    @Test
    public void testRegularFileName1() {
        testFileName( "newfile.txt", "newfile.txt" );
    }

    @Test
    public void testRegularFileName2() {
        testFileName( "NewFile.txt", "NewFile.txt" );
    }

    @Test
    public void testSecuredFileName1() {
        testFileName( "c:\\fakepath\\newfile.txt", "newfile.txt" );
    }

    @Test
    public void testSecuredFileName2() {
        testFileName( "C:\\fakepath\\NewFile.txt", "NewFile.txt" );
    }

    private void testFileName( String fileInput, String expected ) {
        when( file.getValue() ).thenReturn( fileInput );
        fileUpload.getFileChangeHandler().onChange( mock( ChangeEvent.class ) );
        verify( fileText ).setValue( expected );
    }

}
