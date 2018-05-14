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

package org.kie.workbench.common.stunner.core.client.session.impl;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.logging.client.LogConfiguration;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils.lookup;

@Dependent
public class ManagedSession
        extends AbstractSession<AbstractCanvas, AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(ManagedSession.class.getName());

    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<AbstractCanvas> canvasInstances;
    private final ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;
    private final ManagedInstance<CanvasControl<AbstractCanvas>> canvasControlInstances;
    private final ManagedInstance<CanvasControl<AbstractCanvasHandler>> canvasHandlerControlInstances;
    private final StunnerPreferencesRegistryLoader preferencesRegistryLoader;

    // Session's state.
    private AbstractCanvas canvas;
    private AbstractCanvasHandler canvasHandler;
    private final List<ControlRegistrationEntry<AbstractCanvas>> canvasControlRegistrationEntries;
    private final List<ControlRegistrationEntry<AbstractCanvasHandler>> canvasHandlerControlRegistrationEntries;
    private final List<CanvasControl<AbstractCanvas>> canvasControls;
    private final List<CanvasControl<AbstractCanvasHandler>> canvasHandlerControls;
    private CanvasShapeListener shapeListener;
    private CanvasElementListener elementListener;
    private Consumer<CanvasControl<AbstractCanvas>> canvasControlRegistered;
    private Consumer<CanvasControl<AbstractCanvas>> canvasControlDestroyed;
    private Consumer<CanvasControl<AbstractCanvasHandler>> canvasHandlerControlRegistered;
    private Consumer<CanvasControl<AbstractCanvasHandler>> canvasHandlerControlDestroyed;

    @Inject
    public ManagedSession(final DefinitionUtils definitionUtils,
                          final @Any ManagedInstance<AbstractCanvas> canvasInstances,
                          final @Any ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances,
                          final @Any ManagedInstance<CanvasControl<AbstractCanvas>> canvasControlInstances,
                          final @Any ManagedInstance<CanvasControl<AbstractCanvasHandler>> canvasHandlerControlInstances,
                          final StunnerPreferencesRegistryLoader preferencesRegistryLoader) {
        super();
        this.definitionUtils = definitionUtils;
        this.canvasInstances = canvasInstances;
        this.canvasHandlerInstances = canvasHandlerInstances;
        this.canvasControlInstances = canvasControlInstances;
        this.canvasHandlerControlInstances = canvasHandlerControlInstances;
        this.preferencesRegistryLoader = preferencesRegistryLoader;
        this.canvasControlRegistrationEntries = new LinkedList<>();
        this.canvasHandlerControlRegistrationEntries = new LinkedList<>();
        this.canvasControls = new LinkedList<>();
        this.canvasHandlerControls = new LinkedList<>();
        this.canvasControlRegistered = c -> {
        };
        this.canvasHandlerControlRegistered = c -> {
        };
        this.canvasControlDestroyed = c -> {
        };
        this.canvasHandlerControlDestroyed = c -> {
        };
    }

    @SuppressWarnings("unchecked")
    public ManagedSession registerCanvasControl(final Class<? extends CanvasControl> type) {
        return registerCanvasControl(type,
                                     null);
    }

    @SuppressWarnings("unchecked")
    public ManagedSession registerCanvasControl(final Class<? extends CanvasControl> type,
                                                final Class<? extends Annotation> qualifier) {
        canvasControlRegistrationEntries.add(new ControlRegistrationEntry<>((Class<? extends CanvasControl<AbstractCanvas>>) type,
                                                                            null != qualifier ? buildQualifier(qualifier) : null));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ManagedSession registerCanvasHandlerControl(final Class<? extends CanvasControl> type) {
        return registerCanvasHandlerControl(type,
                                            null);
    }

    @SuppressWarnings("unchecked")
    public ManagedSession registerCanvasHandlerControl(final Class<? extends CanvasControl> type,
                                                       final Class<? extends Annotation> qualifier) {
        canvasHandlerControlRegistrationEntries.add(new ControlRegistrationEntry<>((Class<? extends CanvasControl<AbstractCanvasHandler>>) type,
                                                                                   null != qualifier ? buildQualifier(qualifier) : null));
        return this;
    }

    public ManagedSession onCanvasControlRegistered(final Consumer<CanvasControl<AbstractCanvas>> c) {
        this.canvasControlRegistered = c;
        return this;
    }

    public ManagedSession onCanvasControlDestroyed(final Consumer<CanvasControl<AbstractCanvas>> c) {
        this.canvasControlDestroyed = c;
        return this;
    }

    public ManagedSession onCanvasHandlerControlRegistered(final Consumer<CanvasControl<AbstractCanvasHandler>> c) {
        this.canvasHandlerControlRegistered = c;
        return this;
    }

    public ManagedSession onCanvasHandlerControlDestroyed(final Consumer<CanvasControl<AbstractCanvasHandler>> c) {
        this.canvasHandlerControlDestroyed = c;
        return this;
    }

    @Override
    public void init(final Metadata metadata,
                     final Command callback) {
        if (null != canvas) {
            throw new IllegalStateException("Session is already loaded!");
        }
        // Obtain the right qualified types.
        final Annotation qualifier = definitionUtils.getQualifier(metadata.getDefinitionSetId());
        canvas = lookup(canvasInstances, qualifier);
        canvasHandler = lookup(canvasHandlerInstances, qualifier);
        canvasControlRegistrationEntries
                .forEach(entry -> registerCanvasControl(lookupCanvasControl(canvasControlInstances,
                                                                            entry,
                                                                            qualifier)));
        canvasHandlerControlRegistrationEntries
                .forEach(entry -> registerCanvasHandlerControl(lookupCanvasHandlerControl(canvasHandlerControlInstances,
                                                                                          entry,
                                                                                          qualifier)));
        // Load the preferences.
        preferencesRegistryLoader.load(metadata.getDefinitionSetId(),
                                       prefs -> callback.execute(),
                                       throwable -> {
                                           if (LogConfiguration.loggingIsEnabled()) {
                                               LOGGER.log(Level.SEVERE,
                                                          "An error was produced during StunnerPreferences initialization.",
                                                          throwable);
                                           }
                                           throw new RuntimeException(throwable);
                                       });
    }

    @Override
    public void open() {
        if (null == shapeListener) {
            shapeListener = new DefaultCanvasShapeListener(canvasControls);
            elementListener = new DefaultCanvasElementListener(canvasHandlerControls);
            registerListeners();
            enableControls();
        }
    }

    @Override
    public void destroy() {
        // Destroy listeners.
        removeListeners();
        // Destroy controls.
        canvasControls.forEach(this::destroyCanvasControl);
        canvasControls.clear();
        canvasHandlerControls.forEach(this::destroyCanvasHandlerControl);
        canvasHandlerControls.clear();
        canvasControlInstances.destroyAll();
        canvasHandlerControlInstances.destroyAll();
        // Destroy canvas.
        canvasHandler.destroy();
        canvasInstances.destroyAll();
        canvasHandlerInstances.destroyAll();
        canvas = null;
        canvasHandler = null;
        shapeListener = null;
        elementListener = null;
        canvasControlRegistered = null;
        canvasControlDestroyed = null;
        canvasHandlerControlRegistered = null;
        canvasHandlerControlDestroyed = null;
    }

    @Override
    public AbstractCanvas getCanvas() {
        return canvas;
    }

    @Override
    public AbstractCanvasHandler getCanvasHandler() {
        return canvasHandler;
    }

    public List<CanvasControl<AbstractCanvas>> getCanvasControls() {
        return canvasControls;
    }

    public List<CanvasControl<AbstractCanvasHandler>> getCanvasHandlerControls() {
        return canvasHandlerControls;
    }

    public static Annotation buildQualifier(final Class<? extends Annotation> type) {
        return new Default() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return type;
            }
        };
    }

    private void registerListeners() {
        canvas.addRegistrationListener(shapeListener);
        canvasHandler.addRegistrationListener(elementListener);
    }

    private void removeListeners() {
        canvas.removeRegistrationListener(shapeListener);
        canvasHandler.removeRegistrationListener(elementListener);
    }

    private void enableControls() {
        canvasControls.forEach(c -> c.init(canvas));
        canvasHandlerControls.forEach(c -> c.init(canvasHandler));
    }

    private void registerCanvasControl(final CanvasControl<AbstractCanvas> control) {
        canvasControls.add(control);
        canvasControlRegistered.accept(control);
    }

    private void registerCanvasHandlerControl(final CanvasControl<AbstractCanvasHandler> control) {
        canvasHandlerControls.add(control);
        canvasHandlerControlRegistered.accept(control);
    }

    private void destroyCanvasControl(final CanvasControl<AbstractCanvas> control) {
        control.destroy();
        canvasControlDestroyed.accept(control);
    }

    private void destroyCanvasHandlerControl(final CanvasControl<AbstractCanvasHandler> control) {
        control.destroy();
        canvasHandlerControlDestroyed.accept(control);
    }

    @SuppressWarnings("unchecked")
    private static CanvasControl<AbstractCanvasHandler> lookupCanvasHandlerControl(final ManagedInstance instance,
                                                                                   final ControlRegistrationEntry<AbstractCanvasHandler> entry,
                                                                                   final Annotation qualifier) {
        return (CanvasControl<AbstractCanvasHandler>) doLookup(instance,
                                                               entry,
                                                               qualifier);
    }

    @SuppressWarnings("unchecked")
    private static CanvasControl<AbstractCanvas> lookupCanvasControl(final ManagedInstance instance,
                                                                     final ControlRegistrationEntry<AbstractCanvas> entry,
                                                                     final Annotation qualifier) {
        return (CanvasControl<AbstractCanvas>) doLookup(instance,
                                                        entry,
                                                        qualifier);
    }

    @SuppressWarnings("unchecked")
    private static Object doLookup(final ManagedInstance instance,
                                   final ControlRegistrationEntry entry,
                                   final Annotation qualifier) {
        final ManagedInstance i = null != entry.qualifier ?
                instance.select(entry.type,
                                entry.qualifier,
                                qualifier) :
                instance.select(entry.type,
                                qualifier);
        return i.isUnsatisfied() ?
                (null != entry.qualifier ?
                        instance.select(entry.type,
                                        entry.qualifier,
                                        DefinitionManager.DEFAULT_QUALIFIER).get() :
                        instance.select(entry.type,
                                        DefinitionManager.DEFAULT_QUALIFIER).get()) :
                i.get();
    }

    private static class ControlRegistrationEntry<T> {

        private final Class<? extends CanvasControl<T>> type;
        private final Annotation qualifier;

        private ControlRegistrationEntry(final Class<? extends CanvasControl<T>> type,
                                         final Annotation qualifier) {
            this.type = type;
            this.qualifier = qualifier;
        }
    }
}
