/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.mvp;

import java.util.function.Consumer;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

/**
 * Implementation of behaviour common to all workbench editors and screens.
 * <p>
 * AbstractWorkbenchActivity is not typically subclassed directly, even by generated code. See the more purpose-based
 * subclasses.
 */
public abstract class AbstractWorkbenchActivity extends AbstractActivity implements WorkbenchActivity {

    public AbstractWorkbenchActivity(final PlaceManager placeManager) {
        super(placeManager);
    }

    @Override
    public Position getDefaultPosition() {
        return CompassPosition.ROOT;
    }

    /**
     * This default implementation returns null, meaning this activity can be displayed in any perspective.
     */
    @Override
    public PlaceRequest getOwningPlace() {
        return null;
    }

    @Override
    public boolean onMayClose() {
        return true;
    }

    @Override
    public abstract String getTitle();

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public abstract IsWidget getWidget();

    @Override
    public void onLostFocus() {
        //Do nothing.
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(null);
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    @Override
    public String contextId() {
        return null;
    }
}
