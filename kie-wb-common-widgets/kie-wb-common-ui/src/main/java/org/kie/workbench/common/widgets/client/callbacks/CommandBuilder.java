/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.callbacks;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.shared.source.SourceGenerationFailedException;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.source.ViewDRLSourceWidget;
import org.kie.workbench.common.widgets.client.widget.NoSuchFileWidget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Utility class to build the Commands for CommandDrivenErrorCallback.
 */
public class CommandBuilder {

    private final Map<Class<? extends Throwable>, Command> commands = new HashMap<Class<? extends Throwable>, Command>();

    public CommandBuilder add(final Class<? extends Throwable> throwable,
                              final Command command) {
        PortablePreconditions.checkNotNull("throwable",
                                           throwable);
        PortablePreconditions.checkNotNull("command",
                                           command);
        commands.put(throwable,
                     command);
        return this;
    }

    public CommandBuilder addNoSuchFileException(final HasBusyIndicator view,
                                                 final Callback<IsWidget> callback) {
        add(NoSuchFileException.class,
            new Command() {

                @Override
                public void execute() {
                    callback.callback(new NoSuchFileWidget());
                    view.hideBusyIndicator();
                }
            }
        );
        return this;
    }

    public CommandBuilder addNoSuchFileException(final HasBusyIndicator view,
                                                 final MultiPageEditor editor) {
        add(NoSuchFileException.class,
            new Command() {

                @Override
                public void execute() {
                    editor.clear();
                    editor.addWidget(new NoSuchFileWidget(),
                                     CommonConstants.INSTANCE.NoSuchFileTabTitle());
                    view.hideBusyIndicator();
                }
            }
        );
        return this;
    }

    public CommandBuilder addNoSuchFileException(final HasBusyIndicator view,
                                                 final MultiPageEditor editor,
                                                 final Menus menus) {
        add(NoSuchFileException.class,
            new Command() {

                @Override
                public void execute() {
                    editor.clear();
                    editor.addWidget(new NoSuchFileWidget(),
                                     CommonConstants.INSTANCE.NoSuchFileTabTitle());
                    disableMenuItems(menus);
                    view.hideBusyIndicator();
                }
            }
        );
        return this;
    }

    public CommandBuilder addNoSuchFileException(final HasBusyIndicator view,
                                                 final Menus menus) {
        add(NoSuchFileException.class,
            new Command() {

                @Override
                public void execute() {
                    disableMenuItems(menus);
                    view.hideBusyIndicator();
                }
            }
        );
        return this;
    }

    public CommandBuilder addFileSystemNotFoundException(final HasBusyIndicator view,
                                                         final MultiPageEditor editor,
                                                         final Menus menus) {
        add(FileSystemNotFoundException.class,
            new Command() {

                @Override
                public void execute() {
                    editor.clear();
                    editor.addWidget(new NoSuchFileWidget(),
                                     CommonConstants.INSTANCE.NoSuchFileTabTitle());
                    disableMenuItems(menus);
                    view.hideBusyIndicator();
                }
            }
        );
        return this;
    }

    public CommandBuilder addFileSystemNotFoundException(final HasBusyIndicator view,
                                                         final Menus menus) {
        add(FileSystemNotFoundException.class,
            new Command() {

                @Override
                public void execute() {
                    disableMenuItems(menus);
                    view.hideBusyIndicator();
                }
            }
        );
        return this;
    }

    public CommandBuilder addSourceCodeGenerationFailedException(final HasBusyIndicator view,
                                                                 final ViewDRLSourceWidget sourceWidget) {
        add(SourceGenerationFailedException.class,
            new Command() {

                @Override
                public void execute() {
                    sourceWidget.clearContent();
                    view.hideBusyIndicator();
                    ErrorPopup.showMessage(CommonConstants.INSTANCE.FailedToGenerateSource());
                }
            }
        );
        return this;
    }

    private void disableMenuItems(final Menus menus) {
        for (MenuItem mi : menus.getItemsMap().values()) {
            mi.setEnabled(false);
        }
    }

    public Map<Class<? extends Throwable>, Command> build() {
        return commands;
    }
}
