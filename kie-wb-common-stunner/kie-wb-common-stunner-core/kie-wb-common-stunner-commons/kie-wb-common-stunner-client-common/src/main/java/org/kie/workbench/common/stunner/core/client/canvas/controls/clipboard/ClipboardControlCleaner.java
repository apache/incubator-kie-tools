/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard;

import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.events.AbstractPlaceEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;

/**
 * Class responsible to clear the clipboard based on the triggers:
 * - Diagram Editor looses its focus
 */
@ApplicationScoped
public class ClipboardControlCleaner {

    private final ActivityBeansCache activityBeansCache;
    private final ManagedInstance<ClipboardControl> clipboardControls;

    @Inject
    public ClipboardControlCleaner(final ManagedInstance<ClipboardControl> clipboardControls, final ActivityBeansCache activityBeansCache) {
        this.activityBeansCache = activityBeansCache;
        this.clipboardControls = clipboardControls;
    }

    public void onPlaceGainFocusEvent(final @Observes PlaceLostFocusEvent event) {
        if (verifyIsDiagramEditor(event)) {
            StreamSupport.stream(clipboardControls.spliterator(), false).forEach(ClipboardControl::clear);
        }
    }

    private boolean verifyIsDiagramEditor(AbstractPlaceEvent event) {
        return activityBeansCache.getActivity(event.getPlace().getIdentifier()).getQualifiers().stream()
                .anyMatch(a -> a instanceof DiagramEditor);
    }
}