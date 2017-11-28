/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.RendererLibrary;
import org.dashbuilder.displayer.client.RendererManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

public class RendererSelector implements IsWidget {

    public enum SelectorType {
        LIST, RADIO, TAB;
    }

    public interface View extends UberView<RendererSelector> {

        void setVisible(boolean visible);

        void setWidth(int width);

        void setHeight(int height);

        void clearRendererSelector();

        void addRendererItem(String renderer);

        void setSelectedRendererIndex(int index);

        String getRendererSelected();
    }

    public interface TabListView extends View {

    }

    public interface ListBoxView extends View {

    }

    public interface RadioListView extends View {

    }

    View tabListView;
    View listBoxView;
    View radioListView;
    View view;
    RendererManager rendererManager;
    RendererLibrary rendererLibrary;

    Command selectCommand;

    @Inject
    public RendererSelector(TabListView tabListView,
                            ListBoxView listBoxView,
                            RadioListView radioListView,
                            RendererManager rendererManager) {
        this.tabListView = tabListView;
        this.listBoxView = listBoxView;
        this.radioListView = radioListView;
        this.rendererManager = rendererManager;
        this.rendererLibrary = null;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public RendererLibrary getRendererLibrary() {
        return rendererLibrary;
    }

    public void init(DisplayerSettings displayerSettings,
                     SelectorType selectorType, int width,
                     Command onSelectCommand) {

        // Choose the target view
        switch (selectorType) {
            case LIST:
                view = listBoxView;
                break;
            case RADIO:
                view = radioListView;
                break;
            case TAB:
                view = tabListView;
                break;
        }

        this.view.init(this);
        this.selectCommand = onSelectCommand;

        RendererLibrary rendererLibrary = rendererManager.getRendererForDisplayer(displayerSettings);
        List<RendererLibrary> renderers = rendererManager.getRenderersForType(displayerSettings.getType(), displayerSettings.getSubtype());
        if (renderers != null && renderers.size() > 1) {

            view.setVisible(true);
            view.setWidth(width);
            view.clearRendererSelector();

            // Build the selector
            for (int i=0; i<renderers.size(); i++) {
                RendererLibrary rendererLib = renderers.get(i);
                view.addRendererItem(rendererLib.getName());

                if (rendererLibrary != null && rendererLib.equals(rendererLibrary)) {
                    view.setSelectedRendererIndex(i);
                }
            }
        }
        // If there is only one renderer in the list, do not show the selector.
        else {
            view.setVisible(false);
        }
    }

    // View notifications

    void onRendererSelected() {
        if (view.getRendererSelected() != null) {
            rendererLibrary = rendererManager.getRendererByName(view.getRendererSelected());
            selectCommand.execute();
        }
    }
}