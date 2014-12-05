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

package org.drools.workbench.screens.drltext.client.editor;

import java.util.List;
import javax.annotation.PostConstruct;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.screens.drltext.client.resources.i18n.DRLTextEditorConstants;
import org.drools.workbench.screens.drltext.client.widget.ClickEvent;
import org.drools.workbench.screens.drltext.client.widget.DSLSentenceBrowserWidget;
import org.drools.workbench.screens.drltext.client.widget.FactTypeBrowserWidget;
import org.drools.workbench.screens.drltext.client.widget.RuleContentWidget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

public class DRLEditorViewImpl
        extends KieEditorViewImpl
        implements DRLEditorView {

    private RuleContentWidget ruleContentWidget = null;
    private FactTypeBrowserWidget factTypeBrowser = null;
    private DSLSentenceBrowserWidget dslConditionsBrowser = null;
    private DSLSentenceBrowserWidget dslActionsBrowser = null;
    private VerticalPanel browsers = new VerticalPanel();

    @Override
    public void init( final DRLEditorPresenter presenter ) {
        this.factTypeBrowser.init( presenter );
    }

    @PostConstruct
    public void init() {
        this.ruleContentWidget = new RuleContentWidget();

        final ClickEvent ce = new ClickEvent() {
            public void selected( String text ) {
                ruleContentWidget.insertText( text );
            }
        };

        final Grid layout = new Grid( 1,
                                      2 );

        this.factTypeBrowser = new FactTypeBrowserWidget( ce );
        this.dslConditionsBrowser = new DSLSentenceBrowserWidget( ce,
                                                                  DRLTextEditorConstants.INSTANCE.showDSLConditions(),
                                                                  DRLTextEditorConstants.INSTANCE.dslConditions() );
        this.dslActionsBrowser = new DSLSentenceBrowserWidget( ce,
                                                               DRLTextEditorConstants.INSTANCE.showDSLActions(),
                                                               DRLTextEditorConstants.INSTANCE.dslActions() );
        browsers.add( factTypeBrowser );
        browsers.add( dslConditionsBrowser );
        browsers.add( dslActionsBrowser );

        layout.setWidget( 0,
                          0,
                          browsers );
        layout.setWidget( 0,
                          1,
                          ruleContentWidget );

        layout.getColumnFormatter().setWidth( 0,
                                              "20%" );
        layout.getColumnFormatter().setWidth( 1,
                                              "80%" );
        layout.getCellFormatter().setAlignment( 0,
                                                0,
                                                HasHorizontalAlignment.ALIGN_LEFT,
                                                HasVerticalAlignment.ALIGN_TOP );
        layout.getCellFormatter().setAlignment( 0,
                                                1,
                                                HasHorizontalAlignment.ALIGN_LEFT,
                                                HasVerticalAlignment.ALIGN_TOP );
        layout.setWidth( "95%" );

        initWidget( layout );
    }

    @Override
    public void setContent( final String drl,
                            final List<String> fullyQualifiedClassNames ) {
        dslConditionsBrowser.setVisible( false );
        dslActionsBrowser.setVisible( false );
        ruleContentWidget.setContent( drl );
        factTypeBrowser.setFullyQualifiedClassNames( fullyQualifiedClassNames );
        factTypeBrowser.setDSLR( false );
    }

    @Override
    public void setContent( final String dslr,
                            final List<String> fullyQualifiedClassNames,
                            final List<DSLSentence> dslConditions,
                            final List<DSLSentence> dslActions ) {
        dslConditionsBrowser.setVisible( true );
        dslActionsBrowser.setVisible( true );
        ruleContentWidget.setContent( dslr );
        factTypeBrowser.setFullyQualifiedClassNames( fullyQualifiedClassNames );
        factTypeBrowser.setDSLR( true );
        dslConditionsBrowser.setDSLSentences( dslConditions );
        dslActionsBrowser.setDSLSentences( dslActions );
    }

    @Override
    public String getContent() {
        return ruleContentWidget.getContent();
    }

}
