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

package org.dashbuilder.client.cms.screen.transfer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.client.cms.screen.transfer.export.wizard.ExportWizard;
import org.dashbuilder.transfer.DataTransferExportModel;
import org.dashbuilder.transfer.DataTransferServices;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class DataTransferScreenTest {

    @Mock
    private DataTransferScreen.View view;

    @Mock
    private DataTransferServices dataTransferServices;

    @Mock
    private DataTransferPopUp dataTransferPopUp;

    @Mock
    private ExportWizard exportWizard;
    
    @Mock
    BusyIndicatorView busyIndicatorView;

    private DataTransferScreen dataTransferScreen;

    private CallerMock<DataTransferServices> dataTransferServicesCaller;

    @Before
    public void prepare() {
        dataTransferServicesCaller = new CallerMock<>(dataTransferServices);
        dataTransferScreen = new DataTransferScreen(
            view,
            dataTransferPopUp,
            dataTransferServicesCaller,
            exportWizard,
            busyIndicatorView);
    }

    @Test
    public void doExportTest() throws Exception {
        String path = "path";
        DataTransferExportModel exportAll = DataTransferExportModel.exportAll();
        when(dataTransferServices.doExport(exportAll)).thenReturn(path);
        dataTransferScreen.init();
        
        dataTransferScreen.doExport();
        
        verify(dataTransferServices).doExport(exportAll);
        verify(view).exportOK();
        verify(view).download(path);
    }

    @Test
    public void doExportFailureTest() throws Exception {
        IOException exception = new IOException();
        DataTransferExportModel exportAll = DataTransferExportModel.exportAll();
        when(dataTransferServices.doExport(exportAll)).thenThrow(exception);
        
        dataTransferScreen.doExport();
        
        verify(dataTransferServices).doExport(exportAll);
        verify(view).exportError(exception);
    }

    @Test
    public void doImportTest() throws Exception {
        List<String> list = new ArrayList<>();
        when(dataTransferServices.doImport()).thenReturn(list);
        dataTransferScreen.doImport();
        verify(dataTransferServices).doImport();
        verify(view).importOK();
        verify(dataTransferPopUp).show(list);
    }

    @Test
    public void doImportFailureTest() throws Exception {
        Exception exception = new Exception();
        when(dataTransferServices.doImport()).thenThrow(exception);
        dataTransferScreen.doImport();
        verify(dataTransferServices).doImport();
        verify(view).importError(exception);
    }
}
