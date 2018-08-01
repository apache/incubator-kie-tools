/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.widget.card.body.notification;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.gwtbootstrap3.client.ui.constants.Trigger;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Templated
@Dependent
public class NotificationView extends Composite
        implements NotificationPresenter.View {

    private TranslationService translationService;

    @Inject
    @DataField
    Anchor size;

    @Inject
    @DataField
    Span icon;

    @Inject
    public NotificationView(final TranslationService translationService) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void setupOk() {
        icon.addStyleName(NotificationType.OK.getStyleName());
        size.setVisible(false);
    }

    @Override
    public void setup(final NotificationType type,
                      final String size) {
        icon.addStyleName(checkNotNull("type",
                                       type).getStyleName());
        this.size.setVisible(true);
        this.size.setText(size);
    }

    @Override
    public void setup(final NotificationType type,
                      final String size,
                      final String popOverMessage) {
        setup(type,
              size);
        final Widget parent = this.size.getParent();
        final Popover popover = new Popover(this.size);
        popover.setTrigger(Trigger.CLICK);
        popover.setPlacement(Placement.RIGHT);
        popover.setTitle(getTitleText());
        popover.setContent(popOverMessage);
        parent.getElement().insertAfter(popover.asWidget().getElement(),
                                        icon.getElement());
        popover.init();
    }

    private String getTitleText() {
        return translationService.format(Constants.NotificationView_TitleText);
    }
}
