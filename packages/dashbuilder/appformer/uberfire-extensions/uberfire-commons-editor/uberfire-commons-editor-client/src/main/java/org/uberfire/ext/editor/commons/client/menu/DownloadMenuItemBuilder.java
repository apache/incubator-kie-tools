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

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.dom.DomGlobal;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.mvp.Command;
import org.uberfire.util.URIUtil;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@Dependent
public class DownloadMenuItemBuilder {

    private TranslationService translationService;

    private static final String DEFAULT_EDITOR = "defaulteditor/download?path=";

    @Inject
    public DownloadMenuItemBuilder(final TranslationService translationService) {
        this.translationService = translationService;
    }

    public MenuItem build(final Supplier<Path> pathSupplier) {

        final String download = translationService.format(Constants.DownloadMenuItem_Download);

        return makeMenuItem(download, makeMenuItemCommand(pathSupplier));
    }

    Command makeMenuItemCommand(final Supplier<Path> pathSupplier) {
        return () -> download(pathSupplier);
    }

    void download(final Supplier<Path> pathSupplier) {

        final String downloadURL = getFileDownloadURL(pathSupplier);

        open(downloadURL);
    }

    void open(final String downloadURL) {
        DomGlobal.window.open(downloadURL);
    }

    MenuItem makeMenuItem(final String caption,
                          final Command command) {
        return MenuFactory
                .newTopLevelMenu(caption)
                .respondsWith(command)
                .endMenu()
                .build()
                .getItems()
                .get(0);
    }

    private String getFileDownloadURL(final Supplier<Path> pathSupplier) {
        return GWT.getModuleBaseURL() + DEFAULT_EDITOR + URIUtil.encodeQueryString(URIUtil.decode(pathSupplier.get().toURI()));
    }
}
