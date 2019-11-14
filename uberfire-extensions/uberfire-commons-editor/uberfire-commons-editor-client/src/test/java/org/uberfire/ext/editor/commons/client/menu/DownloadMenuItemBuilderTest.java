/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.menu;

import java.net.URL;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({URL.class})
@RunWith(GwtMockitoTestRunner.class)
public class DownloadMenuItemBuilderTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private Path path;

    private Supplier<Path> pathSupplier = () -> path;

    private DownloadMenuItemBuilder downloadMenuItemBuilder;

    @Before
    public void setup() {
        downloadMenuItemBuilder = spy(new DownloadMenuItemBuilder(translationService));
    }

    @Test
    public void testBuild() {

        final String caption = "Download";
        final Command menuItemCommand = () -> {/* Nothing */};

        when(translationService.format(Constants.DownloadMenuItem_Download)).thenReturn(caption);
        doReturn(menuItemCommand).when(downloadMenuItemBuilder).makeMenuItemCommand(pathSupplier);

        downloadMenuItemBuilder.build(pathSupplier);

        verify(downloadMenuItemBuilder).makeMenuItem(eq(caption), eq(menuItemCommand));
    }

    @Test
    public void testMenuItemCommand() {

        final Command command = downloadMenuItemBuilder.makeMenuItemCommand(pathSupplier);

        when(path.toURI()).thenReturn("default://master@MySpace/Mortgages/src/main/resources/rule.drl");
        doNothing().when(downloadMenuItemBuilder).open(any());

        command.execute();

        verify(downloadMenuItemBuilder).download(pathSupplier);
    }

    @Test
    public void testDownload() {

        final String expectedDownloadURL = "defaulteditor/download?path=default%3A%2F%2Fmaster%40MySpace%2FMortgages%2Fsrc%2Fmain%2Fresources%2Frule.drl";

        when(path.toURI()).thenReturn("default://master@MySpace/Mortgages/src/main/resources/rule.drl");
        doNothing().when(downloadMenuItemBuilder).open(any());

        downloadMenuItemBuilder.download(pathSupplier);

        verify(downloadMenuItemBuilder).open(eq(expectedDownloadURL));
    }

    @Test
    public void testDownloadSpaceAndAmpersand() {

        final String expectedDownloadURL = "defaulteditor/download?path=default%3A%2F%2Fmaster%40MySpace%2FMortgages%2Fsrc%2Fmain%2Fresources%2Fa+%26+b.drl";

        when(path.toURI()).thenReturn("default://master@MySpace/Mortgages/src/main/resources/a & b.drl");
        doNothing().when(downloadMenuItemBuilder).open(any());

        downloadMenuItemBuilder.download(pathSupplier);

        verify(downloadMenuItemBuilder).open(eq(expectedDownloadURL));
    }
}
