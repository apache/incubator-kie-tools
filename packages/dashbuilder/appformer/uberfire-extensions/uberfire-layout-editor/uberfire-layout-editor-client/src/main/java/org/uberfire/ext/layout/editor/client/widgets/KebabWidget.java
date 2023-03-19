/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class KebabWidget implements IsElement {

    @Inject
    @DataField
    private Anchor remove;

    @Inject
    @DataField
    private Anchor edit;

    @Inject
    @DataField("le-kebab")
    private Div leKebab;

    private Command editCommand;
    private Command removeCommand;

    public void init(Command remove,
                     Command edit) {

        this.removeCommand = remove;
        this.editCommand = edit;
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("remove")
    public void removeClick(Event e) {
        removeCommand.execute();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("edit")
    public void editClick(Event e) {
        editCommand.execute();
    }
}
