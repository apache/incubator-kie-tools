/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.widgets.metadata.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import org.kie.workbench.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.kie.workbench.widgets.common.client.widget.DecoratedTextArea;
import org.kie.workbench.services.shared.metadata.model.Metadata;
import org.uberfire.client.common.DecoratedDisclosurePanel;
import org.uberfire.client.common.DirtyableComposite;

public class CommentWidget
        extends DirtyableComposite {

    private final DecoratedTextArea text;

    public CommentWidget( final Metadata metadata,
                          boolean readOnly ) {

        text = getTextArea();
        text.setEnabled( !readOnly );

        DecoratedDisclosurePanel disclosurePanel = getDisclosurePanel();

        disclosurePanel.setContent( text );

        disclosurePanel.addOpenHandler( new OpenHandler<DisclosurePanel>() {
            public void onOpen( OpenEvent<DisclosurePanel> event ) {
                loadData( metadata );
            }
        } );

        disclosurePanel.setOpen( false );

        initWidget( disclosurePanel );
    }

    private DecoratedDisclosurePanel getDisclosurePanel() {
        final DecoratedDisclosurePanel disclosurePanel = new DecoratedDisclosurePanel( MetadataConstants.INSTANCE.Description() );
        disclosurePanel.setWidth( "100%" );
        return disclosurePanel;
    }

    private DecoratedTextArea getTextArea() {
        final DecoratedTextArea text = new DecoratedTextArea();
        text.setWidth( "95%" );
        text.setVisibleLines( 5 );
        text.setTitle( MetadataConstants.INSTANCE.RuleDocHint() );
        return text;
    }

    private void loadData( final Metadata metadata ) {
        text.setText( metadata.getDescription() );
        text.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                metadata.setDescription( text.getText() );
                makeDirty();
            }
        } );
        if ( metadata.getDescription() == null || "".equals( metadata.getDescription() ) ) {
            text.setText( MetadataConstants.INSTANCE.documentationDefault() );
        }
    }
}
