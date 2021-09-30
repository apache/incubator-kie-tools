/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.services.dummy;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.dashbuilder.renderer.RendererSettings;
import org.dashbuilder.renderer.c3.client.C3Renderer;
import org.dashbuilder.renderer.service.RendererSettingsService;
import org.jboss.errai.bus.server.annotations.ShadowService;


/**
 * Renderer settings for Runtime
 *
 */
@Alternative
@ShadowService
@ApplicationScoped
public class RuntimeRendererSettingsService implements RendererSettingsService {

    @Override
    public RendererSettings getSettings() {
        return new RendererSettings(C3Renderer.UUID, false);
    }

}