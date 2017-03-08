/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.plugin.client.widget.media;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.plugin.event.MediaAdded;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class MediaLibraryWidgetTest {

    public static final String PLUGIN_NAME = "plugin";

    private MediaLibraryWidget mediaLibraryWidget;

    private Media media;
    private Collection<Media> existentMedia;
    private ParameterizedCommand<Media> onMediaDelete;

    @Before
    public void setup() {
        mediaLibraryWidget = new MediaLibraryWidget();
        mediaLibraryWidget.library = mock(Row.class);

        media = mock(Media.class);
        existentMedia = new ArrayList<>();
        onMediaDelete = spy(new ParameterizedCommand<Media>() {
            @Override
            public void execute(final Media parameter) {
            }
        });
    }

    @Test
    public void newMediaDeletedOnCloseTest() {
        mediaLibraryWidget.setup(PLUGIN_NAME,
                                 existentMedia,
                                 onMediaDelete);
        mediaLibraryWidget.onNewMedia(new MediaAdded(PLUGIN_NAME,
                                                     media));
        mediaLibraryWidget.updateMediaOnClose();

        verify(onMediaDelete).execute(media);
    }

    @Test
    public void newMediaNotDeletedOnSaveTest() {
        mediaLibraryWidget.setup(PLUGIN_NAME,
                                 existentMedia,
                                 onMediaDelete);
        mediaLibraryWidget.onNewMedia(new MediaAdded(PLUGIN_NAME,
                                                     media));
        mediaLibraryWidget.updateMediaOnSave();

        verify(onMediaDelete,
               never()).execute(media);
    }

    @Test
    public void existentMediaNotDeletedOnCloseTest() {
        existentMedia.add(mock(Media.class));
        existentMedia.add(mock(Media.class));

        mediaLibraryWidget.setup(PLUGIN_NAME,
                                 existentMedia,
                                 onMediaDelete);
        mediaLibraryWidget.onNewMedia(new MediaAdded(PLUGIN_NAME,
                                                     media));
        mediaLibraryWidget.updateMediaOnClose();

        verify(onMediaDelete,
               times(1)).execute(any(Media.class));
        verify(onMediaDelete).execute(media);
    }

    @Test
    public void existentAndUnexistentMediaNotDeletedOnSaveTest() {
        existentMedia.add(mock(Media.class));
        existentMedia.add(mock(Media.class));

        mediaLibraryWidget.setup(PLUGIN_NAME,
                                 existentMedia,
                                 onMediaDelete);
        mediaLibraryWidget.onNewMedia(new MediaAdded(PLUGIN_NAME,
                                                     media));
        mediaLibraryWidget.updateMediaOnSave();

        verify(onMediaDelete,
               never()).execute(any(Media.class));
    }
}
