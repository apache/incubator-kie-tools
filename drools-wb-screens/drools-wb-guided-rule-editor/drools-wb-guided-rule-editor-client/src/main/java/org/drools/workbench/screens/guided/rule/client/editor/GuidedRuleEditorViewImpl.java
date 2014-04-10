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

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collection;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;

public class GuidedRuleEditorViewImpl
        extends Composite
        implements GuidedRuleEditorView {

    private final EventBus localBus = new SimpleEventBus();
    private final SimplePanel panel = new SimplePanel();
    private RuleModeller modeller = null;

    public GuidedRuleEditorViewImpl() {
        panel.setWidth( "100%" );
        initWidget( panel );
    }

    @Override
    public void setContent( final Path path,
                            final RuleModel model,
                            final AsyncPackageDataModelOracle oracle,
                            final Caller<RuleNamesService> ruleNamesService,
                            final boolean isReadOnly,
                            final boolean isDSLEnabled ) {
        this.modeller = new RuleModeller( path,
                                          model,
                                          oracle,
                                          new RuleModellerWidgetFactory(),
                                          localBus,
                                          isReadOnly,
                                          isDSLEnabled );
        panel.setWidget( modeller );

        ruleNamesService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> ruleNames ) {
                modeller.setRuleNamesForPackage( ruleNames );
            }
        } ).getRuleNames(path, model.getPackageName());
    }

    @Override
    public String getTitle(String fileName) {
        return GuidedRuleEditorResources.CONSTANTS.GuidedRuleEditorTitle() + " [" + fileName + "]";
    }

    @Override
    public RuleModel getContent() {
        return modeller.getModel();
    }

    @Override
    public boolean isDirty() {
        //The Modeller widget isn't set until after the content has been loaded from an asynchronous call to
        //the server. It is therefore possible that the User attempts to close the tab before Modeller is set
        return ( modeller == null ) ? false : modeller.getRuleModeller().isDirty();
    }

    @Override
    public void setNotDirty() {
        modeller.resetDirty();
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void refresh() {
        modeller.refreshWidget();
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
