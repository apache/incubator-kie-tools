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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff;

import java.util.HashSet;
import java.util.Set;

import elemental2.dom.HTMLElement;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.portable.ChangeType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.diff.DiffMode;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DiffItemPresenterTest {

    private DiffItemPresenter presenter;

    @Mock
    private DiffItemPresenter.View view;

    @Mock
    private ResourceTypeManagerCache resourceTypeManagerCache;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private TranslationService ts;

    @Mock
    private ChangeRequestDiff diff;

    @Mock
    private Path oldFilePath;

    @Mock
    private Path newFilePath;

    @Before
    public void setUp() {
        doReturn(oldFilePath).when(diff).getOldFilePath();
        doReturn(newFilePath).when(diff).getNewFilePath();
        doReturn("my/old/file").when(oldFilePath).getFileName();
        doReturn("my/new/file").when(newFilePath).getFileName();

        presenter = spy(new DiffItemPresenter(view,
                                              resourceTypeManagerCache,
                                              placeManager,
                                              ts));
    }

    @Test
    public void postConstructTest() {
        presenter.postConstruct();

        verify(view).init(presenter);
    }

    @Test
    public void preDestroyTextualDiffTest() {
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);

        presenter.preDestroy();

        verify(placeManager, never()).closePlace(any(PlaceRequest.class));
        verify(view).removeTextualContent();
    }

    @Test
    public void preDestroyVisualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.VISUAL);
        setPresenterPrivateField("placeRequestCustomLeft", mock(PlaceRequest.class));
        setPresenterPrivateField("placeRequestCustomRight", mock(PlaceRequest.class));
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);

        presenter.preDestroy();

        verify(placeManager, times(2)).closePlace(any(PlaceRequest.class));
    }

    @Test
    public void preDestroyVisualDiffOnlyLeftTest() {
        setPresenterPrivateField("diffMode", DiffMode.VISUAL);
        setPresenterPrivateField("placeRequestCustomLeft", mock(PlaceRequest.class));
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);

        presenter.preDestroy();

        verify(placeManager).closePlace(any(PlaceRequest.class));
    }

    @Test
    public void preDestroyVisualDiffOnlyRightTest() {
        setPresenterPrivateField("diffMode", DiffMode.VISUAL);
        setPresenterPrivateField("placeRequestCustomRight", mock(PlaceRequest.class));
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);

        presenter.preDestroy();

        verify(placeManager).closePlace(any(PlaceRequest.class));
    }

    @Test(expected = IllegalStateException.class)
    public void drawDoNothingWhenNotReadyTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", false);

        presenter.draw();

        verify(view, never()).drawTextualContent(Mockito.<String> any(),
                                                 Mockito.<Boolean> any());
    }

    @Test
    public void drawWhenRegularTextualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn(10).when(diff).getAddedLinesCount();
        doReturn(5).when(diff).getDeletedLinesCount();

        presenter.draw();

        verify(view).drawTextualContent(Mockito.<String> any(),
                                        anyBoolean());
    }

    @Test
    public void drawWhenAddAndRegularTextualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn(10).when(diff).getAddedLinesCount();
        doReturn(5).when(diff).getDeletedLinesCount();

        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn("Added").when(ts).getTranslation(LibraryConstants.Added);

        presenter.draw();

        verify(view).drawTextualContent(Mockito.<String> any(),
                                        eq(true));
    }

    @Test
    public void drawWhenDeleteAndRegularTextualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn(10).when(diff).getAddedLinesCount();
        doReturn(5).when(diff).getDeletedLinesCount();

        doReturn(ChangeType.DELETE).when(diff).getChangeType();
        doReturn("Deleted").when(ts).getTranslation(LibraryConstants.Deleted);

        presenter.draw();

        verify(view).drawTextualContent(Mockito.<String> any(),
                                        eq(true));
    }

    @Test
    public void drawWhenUpdateAndRegularTextualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn(10).when(diff).getAddedLinesCount();
        doReturn(5).when(diff).getDeletedLinesCount();

        doReturn(ChangeType.MODIFY).when(diff).getChangeType();
        doReturn("Updated").when(ts).getTranslation(LibraryConstants.Updated);

        presenter.draw();

        verify(view).drawTextualContent(Mockito.<String> any(),
                                        eq(false));
    }

    @Test
    public void drawWhenBinaryTextualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn("Binary files differ").when(diff).getDiffText();

        presenter.draw();

        verify(view).drawBinaryContent();
    }

    @Test
    public void drawWhenUnmodifiedTextualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn("Diff header only").when(diff).getDiffText();

        presenter.draw();

        verify(view).drawUnmodifiedContent();
    }

    @Test
    public void drawWhenVisualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.VISUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(oldFilePath);
        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(newFilePath);

        presenter.draw();

        verify(view).getCustomLeftContainer();
        verify(view).getCustomRightContainer();
        verify(placeManager, times(2)).goTo(Mockito.<PlaceRequest> any(), Mockito.<HTMLElement> any());
    }

    @Test
    public void drawWhenAddTypeAndVisualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.VISUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn("Added").when(ts).getTranslation(LibraryConstants.Added);

        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(newFilePath);

        presenter.draw();

        verify(view, never()).getCustomLeftContainer();
        verify(view).getCustomRightContainer();
        verify(placeManager).goTo(Mockito.<PlaceRequest> any(), Mockito.<HTMLElement> any());
    }

    @Test
    public void drawWhenDeleteTypeAndVisualDiffTest() {
        setPresenterPrivateField("diffMode", DiffMode.VISUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("diff", diff);

        doReturn(ChangeType.DELETE).when(diff).getChangeType();
        doReturn("Deleted").when(ts).getTranslation(LibraryConstants.Deleted);

        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(oldFilePath);

        presenter.draw();

        verify(view).getCustomLeftContainer();
        verify(view, never()).getCustomRightContainer();
        verify(placeManager).goTo(Mockito.<PlaceRequest> any(), Mockito.<HTMLElement> any());
    }

    @Test
    public void setupTextualDiffTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();

        presenter.setup(diff, true);

        verify(view).setupTextualContent(Mockito.<String> any(),
                                                   Mockito.<String> any(),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenWarnConflictDisabledTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();

        presenter.setup(diff, false);

        verify(view).setupTextualContent(Mockito.<String> any(),
                                                   Mockito.<String> any(),
                                                   eq(false));
    }

    @Test
    public void setupTextualDiffWhenNoConflictTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn(false).when(diff).isConflict();

        presenter.setup(diff, true);

        verify(view).setupTextualContent(Mockito.<String> any(),
                                         Mockito.<String> any(),
                                         eq(false));
    }

    @Test
    public void setupTextualDiffWhenHasConflictTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn(true).when(diff).isConflict();

        presenter.setup(diff, true);

        verify(view).setupTextualContent(Mockito.<String> any(),
                                                   Mockito.<String> any(),
                                                   eq(true));
    }

    @Test
    public void setupTextualDiffWhenHasConflictButWarnConflictDisabledTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn(true).when(diff).isConflict();

        presenter.setup(diff, false);

        verify(view).setupTextualContent(Mockito.<String> any(),
                                                   Mockito.<String> any(),
                                                   eq(false));
    }

    @Test
    public void setupTextualDiffWhenAddTypeTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn("Added").when(ts).getTranslation(LibraryConstants.Added);

        presenter.setup(diff, true);

        verify(diff).getOldFilePath();
        verify(diff, times(2)).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view).setupTextualContent(eq("my/new/file"),
                                                   eq("Added"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenDeleteTypeTest() {
        doReturn(ChangeType.DELETE).when(diff).getChangeType();
        doReturn("Deleted").when(ts).getTranslation(LibraryConstants.Deleted);

        presenter.setup(diff, true);

        verify(diff, times(2)).getOldFilePath();
        verify(diff).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view).setupTextualContent(eq("my/old/file"),
                                                   eq("Deleted"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenModifyTypeTest() {
        doReturn(ChangeType.MODIFY).when(diff).getChangeType();
        doReturn("Updated").when(ts).getTranslation(LibraryConstants.Updated);

        presenter.setup(diff, true);
        verify(diff, times(2)).getOldFilePath();
        verify(diff).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view).setupTextualContent(eq("my/old/file"),
                                                   eq("Updated"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenCopyTypeTest() {
        doReturn(ChangeType.COPY).when(diff).getChangeType();
        doReturn("Copied").when(ts).getTranslation(LibraryConstants.Copied);

        presenter.setup(diff, true);

        verify(diff, times(2)).getOldFilePath();
        verify(diff).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view).setupTextualContent(eq("my/old/file -> my/new/file"),
                                                   eq("Copied"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenRenameTypeTest() {
        doReturn(ChangeType.RENAME).when(diff).getChangeType();
        doReturn("Renamed").when(ts).getTranslation(LibraryConstants.Renamed);

        presenter.setup(diff, true);

        verify(diff, times(2)).getOldFilePath();
        verify(diff).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view).setupTextualContent(eq("my/old/file -> my/new/file"),
                                                   eq("Renamed"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();
        ResourceTypeDefinition resourceTypeDefinition = mock(ResourceTypeDefinition.class);

        doReturn(true).when(resourceTypeDefinition).accept(any());
        doReturn(DiffMode.TEXTUAL).when(resourceTypeDefinition).getDiffMode();

        Set<ResourceTypeDefinition> resourceTypeDefinitions = new HashSet<ResourceTypeDefinition>() {{
            add(resourceTypeDefinition);
        }};

        doReturn(resourceTypeDefinitions).when(resourceTypeManagerCache).getResourceTypeDefinitions();

        presenter.setup(diff, true);

        verify(view).setupTextualContent(Mockito.<String> any(),
                                                   Mockito.<String> any(),
                                                   anyBoolean());
    }

    @Test
    public void toggleCollapsibleContainerStateWhenReadyOpenVisualTest() {
        setPresenterPrivateField("diffMode", DiffMode.VISUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", true);
        setPresenterPrivateField("canClose", true);
        setPresenterPrivateField("placeRequestCustomLeft", mock(PlaceRequest.class));

        presenter.toggleCollapsibleContainerState();

        verify(placeManager).closePlace(any(PlaceRequest.class));
    }

    @Test
    public void toggleCollapsibleContainerStateWhenReadyOpenTextualTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("canClose", true);
        setPresenterPrivateField("open", true);

        presenter.toggleCollapsibleContainerState();

        verify(view).removeTextualContent();
    }

    @Test
    public void toggleCollapsibleContainerStateWhenReadyOpenCannotCloseTextualTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("canClose", false);
        setPresenterPrivateField("open", true);

        presenter.toggleCollapsibleContainerState();

        verify(view, never()).removeTextualContent();
    }

    @Test
    public void toggleCollapsibleContainerStateWhenReadyClosedVisualTest() {
        setPresenterPrivateField("diffMode", DiffMode.VISUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", false);
        setPresenterPrivateField("diff", diff);

        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn("Added").when(ts).getTranslation(LibraryConstants.Added);

        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(newFilePath);

        presenter.toggleCollapsibleContainerState();

        verify(placeManager).goTo(Mockito.<PlaceRequest> any(), Mockito.<HTMLElement> any());
    }

    @Test
    public void toggleCollapsibleContainerStateWhenReadyClosedTextualTest() {
        setPresenterPrivateField("diffMode", DiffMode.TEXTUAL);
        setPresenterPrivateField("ready", true);
        setPresenterPrivateField("open", false);
        setPresenterPrivateField("diff", diff);

        doReturn(10).when(diff).getAddedLinesCount();
        doReturn(5).when(diff).getDeletedLinesCount();

        presenter.toggleCollapsibleContainerState();

        verify(view).drawTextualContent(Mockito.<String> any(),
                                        anyBoolean());
    }

    private void setPresenterPrivateField(final String fieldName, final Object value) {
        try {
            FieldUtils.writeField(DiffItemPresenter.class.getDeclaredField(fieldName), presenter, value, true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Assert.fail();
        }
    }
}
