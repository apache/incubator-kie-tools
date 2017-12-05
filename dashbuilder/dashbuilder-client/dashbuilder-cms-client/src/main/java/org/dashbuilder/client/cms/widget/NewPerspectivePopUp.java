/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.dashbuilder.client.cms.widget;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.client.validation.PluginNameValidator;
import org.uberfire.ext.plugin.exception.PluginAlreadyExists;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@ApplicationScoped
public class NewPerspectivePopUp implements IsElement {

    public interface View extends UberElement<NewPerspectivePopUp> {

        void show();

        void hide();

        String getName();

        String getStyle();

        void errorEmptyName();

        void errorInvalidName();

        void errorDuplicatedName();
    }

    private Caller<PerspectiveServices> perspectiveServices;
    private PlaceManager placeManager;
    private PluginNameValidator pluginNameValidator;
    private NewPerspectivePopUpView view;

    // For proxying
    protected NewPerspectivePopUp() {
    }

    @Inject
    public NewPerspectivePopUp(NewPerspectivePopUpView view,
                               Caller<PerspectiveServices> perspectiveServices,
                               PluginNameValidator pluginNameValidator,
                               PlaceManager placeManager) {
        this.view = view;
        this.perspectiveServices = perspectiveServices;
        this.pluginNameValidator = pluginNameValidator;
        this.placeManager = placeManager;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    protected PlaceRequest getPathPlaceRequest(Plugin response) {
        return new PathPlaceRequest(response.getPath())
                .addParameter("name", response.getName());
    }

    public void show() {
        view.show();
    }

    public void hide() {
        view.hide();
    }

    // View actions

    public void onOK() {
        String layoutName = view.getName();
        LayoutTemplate.Style layoutStyle = LayoutTemplate.Style.valueOf(view.getStyle());

        pluginNameValidator.validate(layoutName + ".plugin",
                new ValidatorWithReasonCallback() {
                    @Override
                    public void onFailure(final String reason) {
                        if (ValidationErrorReason.EMPTY_NAME.name().equals(reason)) {
                            view.errorEmptyName();
                        } else if (ValidationErrorReason.DUPLICATED_NAME.name().equals(reason)) {
                            view.errorDuplicatedName();
                        } else {
                            view.errorInvalidName();
                        }
                    }

                    @Override
                    public void onSuccess() {
                        perspectiveServices.call(
                            (Plugin response) -> {
                                placeManager.goTo(getPathPlaceRequest(response));
                                hide();
                            },
                            (message, throwable) -> {
                                if (throwable instanceof PluginAlreadyExists) {
                                    view.errorDuplicatedName();
                                } else {
                                    view.errorInvalidName();
                                }
                                return false;
                            }
                        ).createNewPerspective(layoutName, layoutStyle);
                    }

                    @Override
                    public void onFailure() {
                        view.errorInvalidName();
                    }
                });
    }

    public void onCancel() {
        hide();
    }
}