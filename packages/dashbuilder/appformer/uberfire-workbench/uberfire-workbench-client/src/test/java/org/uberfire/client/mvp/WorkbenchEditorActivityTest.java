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

package org.uberfire.client.mvp;

import com.google.gwt.user.client.ui.IsWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ExternalPathPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy.EDITOR_PROVIDED;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchEditorActivityTest {

    @Mock
    private PathPlaceRequest place;

    @Mock
    private ObservablePath path;

    @Mock
    private ExternalPathPlaceRequest extPlace;

    @Mock
    private Path plainPath;

    @Mock
    private IsWidget isWidget;

    @Mock
    private PlaceManager placeManager;

    @Test
    public void editorCreatesObservablePathForExternalPlaceRequest() {
        var activity = Mockito.spy(new EditorTestActivity(placeManager, EDITOR_PROVIDED));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                return null;
            }
        }).when(activity).onStartup(any(Path.class),
                                    any(PlaceRequest.class));

        when(extPlace.getPath()).thenReturn(plainPath);
        activity.onStartup(extPlace);

        verify(activity).onStartup(any(Path.class),
                                   any(PlaceRequest.class));
    }

    private class EditorTestActivity extends AbstractWorkbenchEditorActivity {

        private LockingStrategy strategy;

        public EditorTestActivity(PlaceManager placeManager,
                                  LockingStrategy strategy) {
            super(placeManager);
            this.strategy = strategy;
        }

        @Override
        public String getIdentifier() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public IsWidget getWidget() {
            return isWidget;
        }

        @Override
        protected LockingStrategy getLockingStrategy() {
            return strategy;
        }

        @Override
        public boolean isDynamic() {
            return true;
        }
    }
}