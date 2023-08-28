/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.RootPanel;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import jsinterop.base.Js;
import org.dashbuilder.client.place.PlaceManager;
import org.dashbuilder.client.screens.Router;
import org.dashbuilder.patternfly.busyindicator.BusyIndicator;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;

@EntryPoint
@ApplicationScoped
@Bundle("resources/i18n/AppConstants.properties")
public class RuntimeEntryPoint {

    @Inject
    Elemental2DomUtil domUtil;

    @Inject
    PlaceManager placeManager;

    @Inject
    Router router;

    @Inject
    BusyIndicator busyIndicator;

    @PostConstruct
    public void onLoad() {
        var root = DomGlobal.document.getElementById("app");

        RootPanel.get().getElement().appendChild(Js.cast(root));

        hideLoading();
        placeManager.setup(root);
        router.doRoute();

        DomGlobal.document.body.appendChild(busyIndicator.getElement());

    }

    private void hideLoading() {
        Element loading = DomGlobal.document.getElementById("loading");
        if (loading != null) {
            loading.remove();
        }
    }

}
