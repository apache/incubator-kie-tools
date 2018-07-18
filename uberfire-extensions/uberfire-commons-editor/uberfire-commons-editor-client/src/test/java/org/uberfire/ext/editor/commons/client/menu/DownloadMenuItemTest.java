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
public class DownloadMenuItemTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private Path path;

    private Supplier<Path> pathSupplier = () -> path;

    private DownloadMenuItem downloadMenuItem;

    @Before
    public void setup() {
        downloadMenuItem = spy(new DownloadMenuItem(translationService));
    }

    @Test
    public void testBuild() {

        final String caption = "Download";
        final Command menuItemCommand = () -> {/* Nothing */};

        when(translationService.format(Constants.DownloadMenuItem_Download)).thenReturn(caption);
        doReturn(menuItemCommand).when(downloadMenuItem).makeMenuItemCommand(pathSupplier);

        downloadMenuItem.build(pathSupplier);

        verify(downloadMenuItem).makeMenuItem(eq(caption), eq(menuItemCommand));
    }

    @Test
    public void testMenuItemCommand() {

        final Command command = downloadMenuItem.makeMenuItemCommand(pathSupplier);

        doNothing().when(downloadMenuItem).open(any());

        command.execute();

        verify(downloadMenuItem).download(pathSupplier);
    }

    @Test
    public void testDownload() {

        final String expectedDownloadURL = "defaulteditor/download?path=default://master@MySpace/Mortgages/src/main/resources/rule.drl";

        when(path.toURI()).thenReturn("default://master@MySpace/Mortgages/src/main/resources/rule.drl");
        doNothing().when(downloadMenuItem).open(any());

        downloadMenuItem.download(pathSupplier);

        verify(downloadMenuItem).open(eq(expectedDownloadURL));
    }
}
