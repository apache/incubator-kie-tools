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

package org.guvnor.ala.ui.client.provider.status.runtime.actions;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class RuntimeActionItemView
        implements RuntimeActionItemPresenter.View,
                   IsElement {

    @Inject
    @DataField("action-item")
    private ListItem actionItem;

    @Inject
    @DataField("action-item-anchor")
    private Anchor actionAnchor;

    private RuntimeActionItemPresenter presenter;

    private boolean enabled = false;

    @Override
    public void init(final RuntimeActionItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLabel(final String label) {
        actionAnchor.setTextContent(label);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            actionItem.getClassList().remove("disabled");
        } else {
            actionItem.getClassList().add("disabled");
        }
    }

    @EventHandler("action-item-anchor")
    protected void onActionAnchorClick(@ForEvent("click") final Event event) {
        if (enabled) {
            presenter.onActionClick();
        }
    }
}