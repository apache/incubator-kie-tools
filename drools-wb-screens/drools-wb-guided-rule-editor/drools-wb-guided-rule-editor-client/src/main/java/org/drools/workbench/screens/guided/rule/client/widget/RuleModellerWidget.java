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

package org.drools.workbench.screens.guided.rule.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;

/**
 * A superclass for the widgets present in RuleModeller.
 */
public abstract class RuleModellerWidget
        extends Composite {

    protected RuleModeller modeller;

    private EventBus eventBus;

    private boolean modified;

    private List<Command> onModifiedCommands = new ArrayList<Command>();

    public RuleModellerWidget( RuleModeller modeller,
                               EventBus eventBus ) {
        this.modeller = modeller;
        this.eventBus = eventBus;
    }

    /**
     * Dictates if the widget's state is RO or not. Sometimes RuleModeller will
     * force this state (i.e. when lockLHS() or lockRHS()), but some other
     * times, the widget itself is responsible to autodetect its state.
     * @return
     */
    public abstract boolean isReadOnly();

    /**
     * Does the Fact Type the Widget represents known to the
     * SuggestionCompletionEngine. If the Fact Type is known the Widget can be
     * edited or deleted (unless read-only). If the Fact Type is not known the
     * Widget can be deleted but cannot be edited (i.e. it is always read-only).
     * @return
     */
    public abstract boolean isFactTypeKnown();

    public HandlerRegistration addFactTypeKnownValueChangeHandler(FactTypeKnownValueChangeHandler changeHandler){
        return addHandler( changeHandler,
                FactTypeKnownValueChangeEvent.getType() );
    }

    public RuleModeller getModeller() {
        return modeller;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setModified( boolean modified ) {
        if ( modified ) {
            executeOnModifiedCommands();
        }
        this.modified = modified;
    }

    protected boolean isModified() {
        return modified;
    }

    public void addOnModifiedCommand( Command command ) {
        this.onModifiedCommands.add( command );
    }

    private void executeOnModifiedCommands() {
        for ( Command command : onModifiedCommands ) {
            command.execute();
        }
    }

}
