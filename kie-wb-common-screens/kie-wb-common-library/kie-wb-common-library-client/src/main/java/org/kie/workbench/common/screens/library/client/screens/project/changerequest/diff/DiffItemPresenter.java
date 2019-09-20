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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Random;
import elemental2.dom.HTMLElement;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.portable.ChangeType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.diff.DiffMode;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@Dependent
public class DiffItemPresenter {

    private final View view;
    private final ResourceTypeManagerCache resourceTypeManagerCache;
    private final PlaceManager placeManager;
    private final TranslationService ts;
    private DiffMode diffMode;
    private PlaceRequest placeRequestCustomLeft;
    private PlaceRequest placeRequestCustomRight;
    private ChangeRequestDiff diff;
    private boolean ready;
    private boolean open = false;
    private boolean canClose = false;

    @Inject
    public DiffItemPresenter(final View view,
                             final ResourceTypeManagerCache resourceTypeManagerCache,
                             final PlaceManager placeManager,
                             final TranslationService ts) {
        this.view = view;
        this.resourceTypeManagerCache = resourceTypeManagerCache;
        this.placeManager = placeManager;
        this.ts = ts;
    }

    @PostConstruct
    public void postConstruct() {
        view.init(this);
    }

    @PreDestroy
    public void preDestroy() {
        if (ready && open) {
            closeContent();
        }
    }

    public View getView() {
        return view;
    }

    public void setup(final ChangeRequestDiff changeRequestDiff,
                      final boolean warnConflict) {

        diff = changeRequestDiff;
        diffMode = resolveDiffMode(diff);

        final String resolveDiffFilename = resolveDiffFilename(diff.getChangeType(),
                                                               diff.getOldFilePath().getFileName(),
                                                               diff.getNewFilePath().getFileName());

        prepareDiff(diff,
                    resolveDiffFilename,
                    warnConflict);

        view.expandCollapsibleContainer(open);

        if (open && !canClose) {
            view.removeCollapseLink();
        }

        ready = true;
    }

    public void draw() {
        if (ready) {
            if (open) {
                openContent();
            }
        } else {
            throw new IllegalStateException("Item not ready - setup first.");
        }
    }

    public void toggleCollapsibleContainerState() {
        open = !open;

        handleContent();
    }

    private void prepareDiff(final ChangeRequestDiff diff,
                             final String filename,
                             final boolean warnConflict) {
        if (diffMode == DiffMode.VISUAL) {
            prepareVisualDiff(diff,
                              filename,
                              warnConflict);
        } else {
            prepareTextualDiff(diff,
                               filename,
                               warnConflict);
        }
    }

    private void handleContent() {
        if (ready) {
            if (open) {
                openContent();

                if (!canClose) {
                    view.removeCollapseLink();
                    view.expandCollapsibleContainer(true);
                }
            } else if (canClose) {
                closeContent();
            }
        }
    }

    private void closeContent() {
        if (diffMode == DiffMode.VISUAL) {
            closeVisualContent();
        } else {
            closeTextualContent();
        }
    }

    private void openContent() {
        if (diffMode == DiffMode.VISUAL) {
            drawVisualContent();
        } else {
            drawTextualContent();
        }
    }

    PlaceRequest createPlaceRequest(final Path path) {
        return new PathPlaceRequest(path,
                                    createPathPlaceRequestParameters());
    }

    private void drawTextualContent() {
        final DiffPrintState diffPrintState = resolvePrintState(diff);

        switch (diffPrintState) {
            case BINARY:
                view.drawBinaryContent();
                break;

            case UNMODIFIED:
                view.drawUnmodifiedContent();
                break;

            case REGULAR:
            default:
                final boolean isUnified = diff.getChangeType() == ChangeType.ADD
                        || diff.getChangeType() == ChangeType.DELETE;
                view.drawTextualContent(diff.getDiffText(),
                                        isUnified);
                break;
        }
    }

    private void drawVisualContent() {
        if (diff.getChangeType() != ChangeType.ADD) {
            placeRequestCustomLeft = createPlaceRequest(diff.getOldFilePath());
            placeManager.goTo(placeRequestCustomLeft,
                              view.getCustomLeftContainer());
        }

        if (diff.getChangeType() != ChangeType.DELETE) {
            placeRequestCustomRight = createPlaceRequest(diff.getNewFilePath());
            placeManager.goTo(placeRequestCustomRight,
                              view.getCustomRightContainer());
        }
    }

    private void closeVisualContent() {
        if (placeRequestCustomLeft != null) {
            view.clearCustomLeftContainer();
            placeManager.closePlace(placeRequestCustomLeft);
        }

        if (placeRequestCustomRight != null) {
            view.clearCustomRightContainer();
            placeManager.closePlace(placeRequestCustomRight);
        }
    }

    private void closeTextualContent() {
        view.removeTextualContent();
    }

    private Map<String, String> createPathPlaceRequestParameters() {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("readOnly", "true");
        parameters.put("hash", generateRandomHash());
        return parameters;
    }

    private String generateRandomHash() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 32;

        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (Random.nextInt() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    private void prepareTextualDiff(final ChangeRequestDiff diff,
                                    final String filename,
                                    final boolean warnConflict) {
        view.setupTextualContent(filename,
                                 resolveChangeTypeText(diff.getChangeType()),
                                 warnConflict && diff.isConflict());
    }

    private void prepareVisualDiff(final ChangeRequestDiff diff,
                                   final String filename,
                                   final boolean warnConflict) {
        view.setupCustomContent(filename,
                                resolveChangeTypeText(diff.getChangeType()),
                                warnConflict && diff.isConflict());

        if (diff.getChangeType() == ChangeType.ADD) {
            view.expandCustomRightContainer();
        } else if (diff.getChangeType() == ChangeType.DELETE) {
            view.expandCustomLeftContainer();
        }
    }

    private DiffMode resolveDiffMode(final ChangeRequestDiff diff) {
        final Path filePath = diff.getChangeType() == ChangeType.ADD ? diff.getNewFilePath() : diff.getOldFilePath();

        Optional<ResourceTypeDefinition> resourceTypeDefinition = resourceTypeManagerCache
                .getResourceTypeDefinitions()
                .stream()
                .filter(resource -> resource.accept(filePath))
                .findFirst();

        if (resourceTypeDefinition.isPresent()) {
            return resourceTypeDefinition.get().getDiffMode();
        }

        return DiffMode.TEXTUAL;
    }

    private String resolveDiffFilename(final ChangeType changeType,
                                       final String oldFilePath,
                                       final String newFilePath) {
        if (changeType == ChangeType.ADD) {
            return newFilePath;
        } else if (changeType == ChangeType.DELETE ||
                changeType == ChangeType.MODIFY) {
            return oldFilePath;
        } else { // COPY & RENAME
            return oldFilePath + " -> " + newFilePath;
        }
    }

    private String resolveChangeTypeText(final ChangeType changeType) {
        switch (changeType) {
            case ADD:
                return ts.getTranslation(LibraryConstants.Added);
            case DELETE:
                return ts.getTranslation(LibraryConstants.Deleted);
            case RENAME:
                return ts.getTranslation(LibraryConstants.Renamed);
            case COPY:
                return ts.getTranslation(LibraryConstants.Copied);
            case MODIFY:
            default:
                return ts.getTranslation(LibraryConstants.Updated);
        }
    }

    private DiffPrintState resolvePrintState(final ChangeRequestDiff changeRequestDiff) {
        if (changeRequestDiff.getAddedLinesCount() == 0 && changeRequestDiff.getDeletedLinesCount() == 0) {
            if (changeRequestDiff.getDiffText().contains("Binary files differ")) {
                return DiffPrintState.BINARY;
            } else {
                return DiffPrintState.UNMODIFIED;
            }
        }

        return DiffPrintState.REGULAR;
    }

    public interface View extends UberElemental<DiffItemPresenter> {

        void setupTextualContent(final String filename,
                                 final String changeType,
                                 final boolean conflict);

        void drawTextualContent(final String diffText,
                                final boolean isUnified);

        void setupCustomContent(final String filename,
                                final String changeType,
                                final boolean conflict);

        void expandCustomLeftContainer();

        void expandCustomRightContainer();

        HTMLElement getCustomLeftContainer();

        HTMLElement getCustomRightContainer();

        void clearCustomLeftContainer();

        void clearCustomRightContainer();

        void expandCollapsibleContainer(final boolean isOpened);

        void drawBinaryContent();

        void drawUnmodifiedContent();

        void removeTextualContent();

        void removeCollapseLink();
    }
}
