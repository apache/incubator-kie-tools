/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.uberfire.mvp.Command;

/**
 * The coordinator class holds a list of Displayer instances and it makes sure that the data shared among
 * all of them is properly synced. This means every time a data display modification request comes from any
 * of the displayer components the rest are updated to reflect those changes.
 */
@Dependent
public class DisplayerCoordinator {

    protected List<Displayer> displayerList = new ArrayList<>();
    protected Set<DisplayerListener> listenerSet = new HashSet<>();
    protected Map<RendererLibrary,List<Displayer>> rendererMap = new HashMap<>();
    protected CoordinatorListener coordinatorListener = new CoordinatorListener();
    protected Map<Displayer,List<Displayer>> notificationVetoMap = new HashMap<>();
    protected RendererManager rendererManager;

    @Inject
    public DisplayerCoordinator(RendererManager rendererManager) {
        this.rendererManager = rendererManager;
    }

    public void addListener(DisplayerListener... listeners) {
        if (listeners != null) {
            for (DisplayerListener listener : listeners) {
                listenerSet.add(listener);
            }
            displayerList.stream().forEach(displayer -> displayer.addListener(listeners));
        }
    }

    public void addDisplayers(Collection<Displayer> displayers) {
        if (displayers != null) {
            displayers.stream().forEach(this::addDisplayer);
        }
    }
    
    public void addDisplayers(Displayer... displayers) {
        if (displayers != null) {
            for (Displayer displayer : displayers) {
                addDisplayer(displayer);
            }
        }
    }

    public void addDisplayer(Displayer displayer) {
        if (displayer != null && !displayerList.contains(displayer)) {
            displayerList.add(displayer);

            displayer.addListener(coordinatorListener);
            listenerSet.stream().forEach(displayer::addListener);

            RendererLibrary renderer = rendererManager.getRendererForDisplayer(displayer.getDisplayerSettings());
            List<Displayer> rendererGroup = rendererMap.get(renderer);
            if (rendererGroup == null) {
                rendererGroup = new ArrayList<>();
                rendererMap.put(renderer, rendererGroup);
            }
            rendererGroup.add(displayer);
        }
    }

    public List<Displayer> getDisplayerList() {
        return displayerList;
    }

    public boolean removeDisplayer(Displayer displayer) {
        if (displayer == null) {
            return false;
        }
        RendererLibrary renderer = rendererManager.getRendererForDisplayer(displayer.getDisplayerSettings());
        List<Displayer> rendererGroup = rendererMap.get(renderer);
        if (rendererGroup != null) rendererGroup.remove(displayer);

        return displayerList.remove(displayer);
    }

    public void drawAll() {
        drawAll(null, null);
    }

    public void redrawAll() {
        redrawAll(null, null);
    }

    public void drawAll(Command onSuccess, Command onFailure) {
        coordinatorListener.init(onSuccess, onFailure, displayerList.size(), true);
        for (RendererLibrary renderer : rendererMap.keySet()) {
            List<Displayer> rendererGroup = rendererMap.get(renderer);
            renderer.draw(rendererGroup);
        }
    }

    public void redrawAll(Command onSuccess, Command onFailure) {
        coordinatorListener.init(onSuccess, onFailure, displayerList.size(), false);
        for (RendererLibrary renderer : rendererMap.keySet()) {
            List<Displayer> rendererGroup = rendererMap.get(renderer);
            renderer.redraw(rendererGroup);
        }
    }

    public void closeAll() {
        displayerList.stream().forEach(Displayer::close);
    }

    public void clear() {
        closeAll();
        displayerList.clear();
        listenerSet.clear();
        rendererMap.clear();
        notificationVetoMap.clear();
    }

    public void addNotificationVeto(Displayer target, List<Displayer> vetoedDisplayers) {
        notificationVetoMap.put(target, vetoedDisplayers);
    }

    public void addNotificationVeto(List<Displayer> vetoedDisplayers) {
        for (Displayer target: vetoedDisplayers) {
            notificationVetoMap.put(target, vetoedDisplayers);
        }
    }

    public boolean isNotificationVetoed(Displayer from, Displayer to) {
        List<Displayer> vetoList = notificationVetoMap.get(to);
        return vetoList != null && vetoList.contains(from);
    }

    /**
     * Internal class that listens to events raised by any of the Displayer instances handled by this coordinator.
     */
    private class CoordinatorListener implements DisplayerListener {

        int count = 0;
        int total = 0;
        Command onSuccess;
        Command onFailure;
        boolean draw;

        protected void init(Command onSuccess, Command onFailure, int total, boolean draw) {
            count = 0;
            this.onSuccess = onSuccess;
            this.onFailure = onFailure;
            this.draw = draw;
            this.total = total;
        }

        protected void count() {
            count++;
            if (count == total && onSuccess != null) {
                onSuccess.execute();
            }
        }

        protected void error() {
            count++;
            if (count == total && onFailure != null) {
                onFailure.execute();
            }
        }

        @Override
        public void onDataLookup(Displayer displayer) {
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onDataLookup(displayer));
        }

        @Override
        public void onDataLoaded(Displayer displayer) {
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onDataLoaded(displayer));
        }

        @Override
        public void onDraw(Displayer displayer) {
            if (draw) {
                count();
            }
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onDraw(displayer));
        }

        @Override
        public void onRedraw(Displayer displayer) {
            if (!draw) {
                count();
            }
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onRedraw(displayer));
        }

        public void onClose(Displayer displayer) {
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onClose(displayer));
        }

        @Override
        public void onFilterEnabled(Displayer displayer, DataSetGroup groupOp) {
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onFilterEnabled(displayer, groupOp));
        }

        @Override
        public void onFilterEnabled(Displayer displayer, DataSetFilter filter) {
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onFilterEnabled(displayer, filter));
        }

        @Override
        public void onFilterUpdate(Displayer displayer, DataSetFilter oldFilter, DataSetFilter newFilter) {
            for (Displayer other : displayerList) {
                if (other != displayer && !isNotificationVetoed(displayer, other)) {
                    other.onFilterUpdate(displayer, oldFilter, newFilter);
                }
            }
        }

        @Override
        public void onFilterReset(Displayer displayer, List<DataSetGroup> groupOps) {
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onFilterReset(displayer, groupOps));
        }

        @Override
        public void onFilterReset(Displayer displayer, DataSetFilter filter) {
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onFilterReset(displayer, filter));
        }

        @Override
        public void onError(final Displayer displayer, ClientRuntimeError error) {
            error();
            displayerList.stream()
                    .filter(other -> other != displayer && !isNotificationVetoed(displayer, other))
                    .forEach(other -> other.onError(displayer, error));
        }
    }
}