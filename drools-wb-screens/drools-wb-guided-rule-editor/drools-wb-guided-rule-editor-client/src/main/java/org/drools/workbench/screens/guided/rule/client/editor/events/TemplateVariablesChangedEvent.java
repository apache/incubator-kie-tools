/*
 * Copyright 2012 JBoss Inc
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
package org.drools.workbench.screens.guided.rule.client.editor.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.models.datamodel.rule.RuleModel;

/**
 * An event representing a change in Template variables
 */
public class TemplateVariablesChangedEvent
        extends GwtEvent<TemplateVariablesChangedEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onTemplateVariablesChanged( TemplateVariablesChangedEvent event );
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private RuleModel model;

    public TemplateVariablesChangedEvent( RuleModel model ) {
        this.model = model;
    }

    public RuleModel getModel() {
        return model;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( Handler handler ) {
        handler.onTemplateVariablesChanged( this );
    }

}
