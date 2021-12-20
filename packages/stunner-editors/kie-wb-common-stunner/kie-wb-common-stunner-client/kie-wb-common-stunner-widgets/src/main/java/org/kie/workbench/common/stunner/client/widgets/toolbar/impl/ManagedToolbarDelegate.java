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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

public abstract class ManagedToolbarDelegate<S extends ClientSession> implements Toolbar<S> {

    protected abstract ManagedToolbar<S> getDelegate();

    @Override
    public void load(final S session) {
        getDelegate().load(session);
    }

    @Override
    public void enable(final ToolbarCommand<S> command) {
        getDelegate().enable(command);
    }

    @Override
    public void disable(final ToolbarCommand<S> command) {
        getDelegate().disable(command);
    }

    @Override
    public boolean isEnabled(final ToolbarCommand<S> command) {
        return getDelegate().isEnabled(command);
    }

    @Override
    public void destroy() {
        getDelegate().destroy();
    }

    @Override
    public ToolbarView<? extends Toolbar> getView() {
        return getDelegate().getView();
    }
}
