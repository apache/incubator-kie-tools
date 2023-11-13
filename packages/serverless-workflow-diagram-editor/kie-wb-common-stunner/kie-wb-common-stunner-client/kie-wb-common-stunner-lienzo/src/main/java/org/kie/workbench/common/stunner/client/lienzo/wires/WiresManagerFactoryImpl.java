/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.client.lienzo.wires.decorator.StunnerMagnetDecorator;

@ApplicationScoped
@Default
public class WiresManagerFactoryImpl implements WiresManagerFactory {

    private final WiresControlFactory wiresControlFactory;

    private final WiresHandlerFactory wiresHandlerFactory;

    // CDI Proxy.
    protected WiresManagerFactoryImpl() {
        this(null, null);
    }

    @Inject
    public WiresManagerFactoryImpl(final WiresControlFactory wiresControlFactory,
                                   final WiresHandlerFactory wiresHandlerFactory) {
        this.wiresControlFactory = wiresControlFactory;
        this.wiresHandlerFactory = wiresHandlerFactory;
    }

    @Override
    public WiresManager newWiresManager(final Layer layer) {
        WiresManager wiresManager = WiresManager.get(layer);
        wiresManager.setWiresHandlerFactory(wiresHandlerFactory);
        wiresManager.setWiresControlFactory(wiresControlFactory);
        //injecting a custom MagnetDecorator
        wiresManager.getMagnetManager().setMagnetDecorator(new StunnerMagnetDecorator());
        return wiresManager;
    }
}
