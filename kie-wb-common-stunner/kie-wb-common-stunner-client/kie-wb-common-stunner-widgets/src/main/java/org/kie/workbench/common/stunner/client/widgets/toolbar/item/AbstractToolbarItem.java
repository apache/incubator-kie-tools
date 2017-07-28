/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.toolbar.item;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.IconRotate;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.AbstractToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.AbstractToolbar;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

public abstract class AbstractToolbarItem<S extends ClientSession> implements IsWidget {

    private static Logger LOGGER = Logger.getLogger(AbstractToolbarItem.class.getName());

    public interface View extends UberView<AbstractToolbarItem> {

        View setIcon(final IconType icon);

        View setIconRotate(final IconRotate rotate);

        View setIconSize(final IconSize size);

        View setCaption(final String caption);

        View setTooltip(final String tooltip);

        View setClickHandler(final Command command);

        View setEnabled(final boolean enabled);

        boolean isEnabled();

        void destroy();
    }

    View view;

    private String uuid;

    @Inject
    public AbstractToolbarItem(final View view) {
        this.view = view;
    }

    public void doInit() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setUUID(final String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }

    public void show(final AbstractToolbar<S> toolbar,
                     final S session,
                     final AbstractToolbarCommand<S, ?> command,
                     final Command clickHandler) {
        // Initialize the command with the current session.
        command.initialize(toolbar,
                           session);
        final IconType icon = command.getIcon();
        final String caption = command.getCaption();
        if (icon != null) {
            view.setIcon(command.getIcon());
            view.setIconRotate(command.getIconRotate());
        } else {
            view.setCaption(caption);
        }
        view.setTooltip(command.getTooltip());
        view.setClickHandler(clickHandler);
    }

    public void setIconSize(final IconSize size) {
        view.setIconSize(size);
    }

    public void enable() {
        view.setEnabled(true);
    }

    public void disable() {
        view.setEnabled(false);
    }

    public boolean isEnabled() {
        return view.isEnabled();
    }

    public void destroy() {
        view.destroy();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractToolbarItem)) {
            return false;
        }
        final AbstractToolbarItem that = (AbstractToolbarItem) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid == null ? 0 : ~~uuid.hashCode();
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }
}
