/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.dependencies;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.commons.uuid.UUID;

@Templated("#root")
public class DependenciesItemView implements DependenciesItemPresenter.View,
                                             IsElement {

    private DependenciesItemPresenter presenter;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("white-listed-packages-indicator")
    private HTMLDivElement whiteListedPackagesIndicator;

    @Inject
    @DataField("package-white-list-all")
    private HTMLInputElement packageWhiteListAll;

    @Inject
    @DataField("package-white-list-none")
    private HTMLInputElement packageWhiteListNone;

    @Inject
    @Named("span")
    @DataField("group-id")
    private HTMLElement groupId;

    @Inject
    @Named("span")
    @DataField("artifact-id")
    private HTMLElement artifactId;

    @Inject
    @Named("span")
    @DataField("version")
    private HTMLElement version;

    @Inject
    @DataField("remove-button")
    private HTMLAnchorElement removeButton;

    private final String packageWhiteListRadioGroupName = UUID.uuid();
    private DependenciesItemPresenter.WhiteListedPackagesState whiteListedPackagesState;

    @Override
    public void init(final DependenciesItemPresenter presenter) {
        this.presenter = presenter;
        this.packageWhiteListAll.name = packageWhiteListRadioGroupName;
        this.packageWhiteListNone.name = packageWhiteListRadioGroupName;
    }

    @Override
    public void setGroupId(final String groupId) {
        this.groupId.textContent = groupId;
    }

    @Override
    public void setArtifactId(final String artifactId) {
        this.artifactId.textContent = artifactId;
    }

    @Override
    public void setVersion(final String version) {
        this.version.textContent = version;
    }

    @Override
    public void setPackagesWhiteListedState(final DependenciesItemPresenter.WhiteListedPackagesState state) {
        this.whiteListedPackagesState = state;

        packageWhiteListAll.checked = false;
        packageWhiteListNone.checked = false;

        switch (state) {
            case ALL:
                packageWhiteListAll.checked = true;
                break;
            case NONE:
                packageWhiteListNone.checked = true;
                break;
        }
    }

    @Override
    public void setTransitiveDependency(final boolean disabled) {
        if (disabled) {
            removeButton.remove();
            elemental2DomUtil.removeAllElementChildren(whiteListedPackagesIndicator);
            whiteListedPackagesIndicator.textContent = translationService.format(whiteListedPackagesState.name());
            getElement().classList.add("transitive");
        }
    }

    @EventHandler("package-white-list-all")
    public void addAllPackagesToWhiteList(final ClickEvent event) {
        presenter.addAllPackagesToWhiteList();
    }

    @EventHandler("package-white-list-none")
    public void removeAllPackagesFromWhiteList(final ClickEvent event) {
        presenter.removeAllPackagesFromWhiteList();
    }

    @EventHandler("remove-button")
    public void delete(final ClickEvent event) {
        presenter.remove();
    }
}
