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

package org.uberfire.ext.editor.commons.client.file.popups;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.SaveInProgressEvent;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.ParameterizedCommand;

import static org.uberfire.backend.vfs.PathSupport.isVersioned;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class SavePopUpPresenter {

    private ParameterizedCommand<String> command;
    private View view;
    private Event<SaveInProgressEvent> saveInProgressEvent;

    @Inject
    public SavePopUpPresenter(View view,
                              Event<SaveInProgressEvent> saveInProgressEvent) {

        this.saveInProgressEvent = saveInProgressEvent;
        this.view = view;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show(final ParameterizedCommand<String> command) {
        this.command = command;
        view.show();
    }

    public void show(final Path path,
                     final ParameterizedCommand<String> command) {
        final ParameterizedCommand<String> wrappedCommand = wrapWithSaveInProgressEvent(path,
                                                                                        command);

        if (isVersioned(path)) {
            show(wrappedCommand);
        } else {
            wrappedCommand.execute("");
        }
    }

    public ParameterizedCommand<String> getCommand() {
        return command;
    }

    public void save() {
        checkNotNull("command",
                     command);
        command.execute(view.getComment());
        view.hide();
    }

    public void cancel() {
        view.hide();
    }

    private ParameterizedCommand<String> wrapWithSaveInProgressEvent(
            final Path path,
            final ParameterizedCommand<String> command) {

        return parameter -> {
            command.execute(parameter);
            saveInProgressEvent.fire(new SaveInProgressEvent(path));
        };
    }

    public interface View extends UberElement<SavePopUpPresenter> {
        String getComment();
        void show();
        void hide();
    }
}
