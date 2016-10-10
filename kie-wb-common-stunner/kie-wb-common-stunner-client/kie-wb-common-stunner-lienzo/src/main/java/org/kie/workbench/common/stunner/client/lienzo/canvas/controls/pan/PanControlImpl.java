/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls.pan;

import com.ait.lienzo.client.core.mediator.EventFilter;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.mediator.MousePanMediator;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.AbstractMediatorControl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;

import javax.enterprise.context.Dependent;

@Dependent
public class PanControlImpl extends AbstractMediatorControl<MousePanMediator> implements PanControl<AbstractCanvas> {

    private final IEventFilter[] filters = new IEventFilter[]{ EventFilter.ALT };

    @Override
    protected MousePanMediator buildMediator() {
        return new MousePanMediator( filters );
    }

}
