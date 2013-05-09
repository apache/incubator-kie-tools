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

package org.kie.guvnor.guided.rule.client.widget;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.guvnor.models.commons.shared.rule.ActionRetractFact;
import org.kie.guvnor.commons.ui.client.resources.HumanReadable;
import org.kie.guvnor.guided.rule.client.editor.RuleModeller;
import org.uberfire.client.common.SmallLabel;

/**
 * This is used when you want to retract a fact. It will provide a list of bound
 * facts for you to retract.
 */
public class ActionRetractFactWidget extends RuleModellerWidget {

    private boolean readOnly;

    private boolean isFactTypeKnown;

    public ActionRetractFactWidget( RuleModeller modeller,
                                    EventBus eventBus,
                                    ActionRetractFact model,
                                    Boolean readOnly ) {
        super( modeller,
               eventBus );
        HorizontalPanel layout = new HorizontalPanel();
        layout.setWidth( "100%" );
        layout.setStyleName( "model-builderInner-Background" );

        this.isFactTypeKnown = modeller.getSuggestionCompletions().isFactTypeRecognized( modeller.getModel().getLHSBindingType( model.getVariableName() ) );
        if ( readOnly == null ) {
            this.readOnly = !this.isFactTypeKnown;
        } else {
            this.readOnly = readOnly;
        }

        if ( this.readOnly ) {
            layout.addStyleName( "editor-disabled-widget" );
        }

        String desc = modeller.getModel().getLHSBindingType( model.getVariableName() ) + " [" + model.getVariableName() + "]";
        layout.add( new SmallLabel( HumanReadable.getActionDisplayName("retract") + "&nbsp;<b>" + desc + "</b>" ) );

        //This widget couldn't be modified.
        this.setModified( false );

        initWidget( layout );
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return this.isFactTypeKnown;
    }

}
