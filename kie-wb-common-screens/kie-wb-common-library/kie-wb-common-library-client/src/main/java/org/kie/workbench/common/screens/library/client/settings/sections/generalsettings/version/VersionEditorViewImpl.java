/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.sections.generalsettings.version;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.SizeType;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@Templated
public class VersionEditorViewImpl implements VersionEditorView,
                                              IsElement {

    private Presenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField
    private HTMLInputElement versionInput;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement versionAddon;

    @Inject
    @DataField
    private ToggleSwitch developmentModeToggle;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement developmentModeToggleTooltip;

    @PostConstruct
    public void init() {
        this.developmentModeToggle.setSize(SizeType.MINI);
        this.developmentModeToggleTooltip.title = translationService.getTranslation(LibraryConstants.DevelopmentMode_Tooltip);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("versionInput")
    public void onVersionChanged(final ChangeEvent ignore) {
        presenter.notifyVersionChange(versionInput.value);
    }

    @EventHandler("developmentModeToggle")
    public void onSnapshotToggle(final ChangeEvent event) {
        presenter.toggleDevelopmentMode(developmentModeToggle.getValue());
    }

    @Override
    public void setDevelopmentMode(boolean developmentMode) {
        if (developmentModeToggle.getValue().booleanValue() != developmentMode) {
            developmentModeToggle.setValue(developmentMode);
        }
        versionAddon.hidden = !developmentMode;
    }

    @Override
    public boolean isDevelopmentMode() {
        return developmentModeToggle.getValue();
    }

    @Override
    public void setVersion(final String version) {
        this.versionInput.value = version;
    }

    @Override
    public String getVersion() {
        return versionInput.value;
    }
}
