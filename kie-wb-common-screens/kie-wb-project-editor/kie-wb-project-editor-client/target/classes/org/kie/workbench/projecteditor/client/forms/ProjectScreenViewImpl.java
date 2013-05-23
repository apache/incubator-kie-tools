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

package org.kie.workbench.projecteditor.client.forms;

import javax.inject.Inject;

import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.kie.workbench.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.MultiPageEditorView;
import org.uberfire.client.common.Page;

public class ProjectScreenViewImpl
        extends MultiPageEditorView
        implements ProjectScreenView {

    private Presenter presenter;

    @Inject
    private MetadataWidget pomMetaDataPanel;

    @Inject
    private MetadataWidget kModuleMetaDataPanel;

    @Inject
    private MetadataWidget importsPageMetadata;

    @Override
    public void selectMainTab() {
        selectPage(0);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPOMEditorPanel(POMEditorPanel gavPanel) {
        addPage(new Page(gavPanel, ProjectEditorConstants.INSTANCE.PomDotXml()) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        });
        addPage(new Page(this.pomMetaDataPanel, ProjectEditorConstants.INSTANCE.PomDotXmlMetadata()) {
            @Override
            public void onFocus() {
                presenter.onPOMMetadataTabSelected();
            }

            @Override
            public void onLostFocus() {
            }
        });
    }

    @Override
    public void setKModuleEditorPanel(KModuleEditorPanel kModuleEditorPanel) {
        addPage(new Page(kModuleEditorPanel, ProjectEditorConstants.INSTANCE.KModuleDotXml()) {
            @Override
            public void onFocus() {
                presenter.onKModuleTabSelected();
            }

            @Override
            public void onLostFocus() {
            }
        });
        addPage(new Page(this.kModuleMetaDataPanel, ProjectEditorConstants.INSTANCE.KModuleDotXmlMetadata()) {
            @Override
            public void onFocus() {
                presenter.onKModuleMetadataTabSelected();
            }

            @Override
            public void onLostFocus() {
            }
        });
    }

    @Override
    public void setImportsPage(ImportsWidgetPresenter importsWidgetPresenter) {
        addPage(new Page(importsWidgetPresenter, ProjectEditorConstants.INSTANCE.ImportSuggestions()) {
            @Override
            public void onFocus() {
                presenter.onImportsPageSelected();
            }

            @Override
            public void onLostFocus() {
            }
        });
        addPage(new Page(this.importsPageMetadata, ProjectEditorConstants.INSTANCE.ImportSuggestionsMetadata()) {
            @Override
            public void onFocus() {
                presenter.onImportsMetadataTabSelected();
            }

            @Override
            public void onLostFocus() {
            }
        });
    }

    @Override
    public String getEnableKieProjectMenuItemText() {
        return ProjectEditorConstants.INSTANCE.EnableKieProject();
    }

    @Override
    public String getSaveMenuItemText() {
        return ProjectEditorConstants.INSTANCE.Save();
    }

    @Override
    public String getBuildMenuItemText() {
        return ProjectEditorConstants.INSTANCE.Build();
    }

    @Override
    public void setPOMMetadata(Metadata metadata) {
        pomMetaDataPanel.setContent(metadata, false);
    }

    @Override
    public void setKModuleMetadata(Metadata metadata) {
        kModuleMetaDataPanel.setContent(metadata, false);
    }

    @Override
    public void setProjectImportsMetadata(Metadata metadata) {
        importsPageMetadata.setContent(metadata, false);
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
