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

package org.drools.workbench.screens.guided.template.client.editor;

import java.util.Collection;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.Path;

/**
 * Guided Rule Template Editor View implementation
 */
public class GuidedRuleTemplateEditorViewImpl
        extends KieEditorViewImpl
        implements GuidedRuleTemplateEditorView {

    private final SimplePanel panel = new SimplePanel();
    private RuleModeller modeller = null;

    public GuidedRuleTemplateEditorViewImpl() {
        panel.setWidth( "100%" );
        initWidget( panel );
    }

    @Override
    public void setContent( final Path path,
                            final TemplateModel model,
                            final AsyncPackageDataModelOracle oracle,
                            final Caller<RuleNamesService> ruleNamesService,
                            final EventBus eventBus,
                            final boolean isReadOnly ) {
        this.modeller = new RuleModeller( path,
                                          model,
                                          oracle,
                                          new TemplateModellerWidgetFactory(),
                                          eventBus,
                                          isReadOnly );
        panel.setWidget( modeller );

        ruleNamesService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> ruleNames ) {
                modeller.setRuleNamesForPackage( ruleNames );
            }
        } ).getRuleNames(path, model.getPackageName());
    }

    @Override
    public TemplateModel getContent() {
        return (TemplateModel) modeller.getRuleModeller().getModel();
    }

    @Override
    public void refresh() {
        modeller.refreshWidget();
    }
}
