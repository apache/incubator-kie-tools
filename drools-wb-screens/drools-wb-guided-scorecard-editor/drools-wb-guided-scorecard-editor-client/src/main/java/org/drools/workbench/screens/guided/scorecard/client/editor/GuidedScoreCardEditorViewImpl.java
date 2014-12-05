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
package org.drools.workbench.screens.guided.scorecard.client.editor;

import com.google.gwt.user.client.Window;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.client.widget.GuidedScoreCardEditor;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

public class GuidedScoreCardEditorViewImpl
        extends KieEditorViewImpl
        implements GuidedScoreCardEditorView {

    private GuidedScoreCardEditor editor = new GuidedScoreCardEditor();

    public GuidedScoreCardEditorViewImpl() {
        initWidget( editor );
    }

    @Override
    public void setContent( final ScoreCardModel model,
                            final AsyncPackageDataModelOracle oracle ) {
        this.editor.setContent( model,
                                oracle );
    }

    @Override
    public ScoreCardModel getModel() {
        return this.editor.getModel();
    }

    @Override
    public void refreshFactTypes() {
        editor.refreshFactTypes();
    }

}
