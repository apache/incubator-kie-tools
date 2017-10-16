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

package org.kie.workbench.common.screens.server.management.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.mvp.ParameterizedCommand;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

public class ContainerListGroupItem extends LinkedGroupItem {

    public ContainerListGroupItem(final String containerName,
                                  final ParameterizedCommand<String> command) {
        super();
        insert(new Icon(IconType.FOLDER_O),
               0);
        setText(checkNotEmpty("containerName",
                              containerName));
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                command.execute(containerName);
            }
        });
    }
}
