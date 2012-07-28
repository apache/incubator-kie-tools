/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.moduleeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.rpc.Artifact;
import org.drools.guvnor.client.widgets.MessageWidget;
import org.drools.guvnor.client.widgets.MetaDataWidget;
//import org.drools.guvnor.client.widgets.RuleDocumentWidget;
import org.uberfire.client.common.LoadingPopup;

/**
 * The generic editor for all types of artifacts.
 */
public class ArtifactEditor extends Composite {
    interface ArtifactEditorBinder
        extends
        UiBinder<Widget, ArtifactEditor> {
    }

    private static ArtifactEditorBinder uiBinder  = GWT.create( ArtifactEditorBinder.class );

    @UiField(provided = true)
    final MetaDataWidget                metaWidget;

/*    @UiField(provided = true)
    final RuleDocumentWidget            ruleDocumentWidget;*/

    @UiField
    MessageWidget                       messageWidget;

    protected Artifact                  artifact;
    private long                        lastSaved = System.currentTimeMillis();

    /**
     * @param Artifact
     *            artifact
     */
    public ArtifactEditor(Artifact artifact) {
        this(artifact, false);
    }

    /**
     * @param Artifact
     *            artifact
     * @param historicalReadOnly
     *            true if this is a read only view for historical purposes.
     */
    public ArtifactEditor(Artifact artifact,
                          boolean historicalReadOnly) {
        this.artifact = artifact;
        boolean readOnly = historicalReadOnly || artifact.isReadonly();
/*
        ruleDocumentWidget = new RuleDocumentWidget( this.artifact,
                                                     readOnly );*/

        metaWidget = new MetaDataWidget( this.artifact,
                                         readOnly,
                                         this.artifact.getUuid() );

        initWidget( uiBinder.createAndBindUi( this ) );
        setWidth( "100%" );
        LoadingPopup.close();
    }


}
