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


package org.kie.workbench.common.stunner.client.lienzo.components.views;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;

@Dependent
public class LienzoCanvasNotificationView implements LienzoCanvasNotification.View {

    private final FloatingView<IsElement> floatingView;
    private final AlertView alertView;

    @Inject
    public LienzoCanvasNotificationView(final FloatingView<IsElement> floatingView,
                                        final AlertView alertView) {
        this.floatingView = floatingView;
        this.alertView = alertView;
    }

    @PostConstruct
    public void init() {
        floatingView
                .clearTimeOut()
                .setOffsetX(0)
                .setOffsetY(0)
                .hide()
                .add(alertView);
    }

    @Override
    public void show() {
        floatingView.show();
    }

    @Override
    public void hide() {
        floatingView.hide();
    }

    @Override
    public void setText(final String text) {
        alertView.setText(text);
    }

    @Override
    public void at(final double x,
                   final double y) {
        floatingView.setX(x).setY(y);
    }

    @PreDestroy
    public void destroy() {
        floatingView.destroy();
    }

}
