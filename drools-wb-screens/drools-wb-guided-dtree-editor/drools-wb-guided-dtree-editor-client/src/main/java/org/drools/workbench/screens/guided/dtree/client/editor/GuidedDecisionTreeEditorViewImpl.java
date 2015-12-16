/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.guided.dtree.client.editor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.screens.guided.dtree.client.widget.GuidedDecisionTreeWidget;
import org.drools.workbench.screens.guided.dtree.client.widget.palette.GuidedDecisionTreePalette;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

/**
 * The Guided Decision Tree Editor View implementation
 */
public class GuidedDecisionTreeEditorViewImpl
        extends KieEditorViewImpl
        implements GuidedDecisionTreeEditorView {

    private static GuidedDecisionTreeEditorViewBinder uiBinder = GWT.create( GuidedDecisionTreeEditorViewBinder.class );

    interface GuidedDecisionTreeEditorViewBinder
            extends
            UiBinder<Widget, GuidedDecisionTreeEditorViewImpl> {

    }

    @UiField
    SimplePanel holderCanvas;

    @UiField
    SimplePanel holderPalette;

    @Inject
    private GuidedDecisionTreeWidget canvas;

    @Inject
    private GuidedDecisionTreePalette palette;

    private boolean isReadOnly = false;
    private GuidedDecisionTree model;

    public GuidedDecisionTreeEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    public void setup() {
        holderCanvas.setWidget( canvas );
        holderPalette.setWidget( palette );
    }

    @Override
    public void init( final GuidedDecisionTreeEditorPresenter presenter ) {
        canvas.init( presenter );
    }

    @Override
    public void setModel( final GuidedDecisionTree model,
                          final boolean isReadOnly ) {
        this.model = model;
        this.isReadOnly = isReadOnly;
        canvas.setModel( model,
                         isReadOnly );
    }

    @Override
    public void setDataModel( final AsyncPackageDataModelOracle oracle,
                              final boolean isReadOnly ) {
        canvas.clearSelection();
        palette.setDataModelOracle( oracle,
                                    isReadOnly );
    }

}
