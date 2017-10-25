/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.wizard.providertype.item;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.handler.ClientProviderHandler;
import org.guvnor.ala.ui.client.handler.ClientProviderHandlerRegistry;
import org.guvnor.ala.ui.client.resources.images.GuvnorAlaUIImageResources;
import org.guvnor.ala.ui.client.util.AbstractHasContentChangeHandlers;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.mvp.UberElement;

import static org.guvnor.ala.ui.client.util.UIUtil.getDisplayableProviderTypeName;

@Dependent
public class ProviderTypeItemPresenter
        extends AbstractHasContentChangeHandlers {

    public interface View
            extends UberElement<ProviderTypeItemPresenter> {

        void disable();

        boolean isSelected();

        void setSelected(boolean selected);

        void setProviderTypeName(String name);

        void setImage(String imageURL);
    }

    private final View view;
    private final ClientProviderHandlerRegistry handlerRegistry;

    private ProviderType type;

    @Inject
    public ProviderTypeItemPresenter(final View view,
                                     final ClientProviderHandlerRegistry handlerRegistry) {
        this.view = view;
        this.handlerRegistry = handlerRegistry;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final ProviderType type,
                      final ProviderTypeStatus status) {
        this.type = type;
        view.setProviderTypeName(getDisplayableProviderTypeName(type));
        view.setImage(getImageURL(type.getKey()));
        if (status.equals(ProviderTypeStatus.ENABLED)) {
            view.disable();
        }
    }

    public ProviderType getProviderType() {
        return type;
    }

    public boolean isSelected() {
        return view.isSelected();
    }

    public IsElement getView() {
        return view;
    }

    protected void onItemClick() {
        view.setSelected(!isSelected());
        fireChangeHandlers();
    }

    private String getImageURL(ProviderTypeKey providerTypeKey) {
        final ClientProviderHandler handler = handlerRegistry.getProviderHandler(providerTypeKey);
        String imageURL = null;
        if (handler != null) {
            imageURL = handler.getProviderTypeImageURL();
        }
        return imageURL != null ? imageURL : GuvnorAlaUIImageResources.INSTANCE.defaultProviderIcon().getSafeUri().asString();
    }
}
