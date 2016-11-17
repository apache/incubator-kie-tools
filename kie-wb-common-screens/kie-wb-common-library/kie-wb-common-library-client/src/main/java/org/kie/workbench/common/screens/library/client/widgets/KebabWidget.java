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
package org.kie.workbench.common.screens.library.client.widgets;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class KebabWidget implements IsElement {

    @Inject
    @DataField
    private Anchor details;

    @Inject
    @DataField
    private Anchor selectProject;

    @Inject
    @DataField
    private Div kekab;

    private Command detailsCommand;

    private Command selectCommand;

    public void init( Command detailsCommand, Command selectCommand ) {

        this.detailsCommand = detailsCommand;
        this.selectCommand = selectCommand;

    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "details" )
    public void detailsClick( Event e ) {
        e.stopPropagation();
        detailsCommand.execute();
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "selectProject" )
    public void selectCommand( Event e ) {
        e.stopPropagation();
        selectCommand.execute();
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "kekab" )
    public void kekabClick( Event e ) {
        e.preventDefault();
    }


}
