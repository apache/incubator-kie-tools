/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend.remote.services.dummy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.plugin.RuntimePlugin;
import org.uberfire.backend.plugin.RuntimePluginService;

@Service
@ApplicationScoped
public class DummyRuntimePluginService implements RuntimePluginService {

    @Override
    public Collection<String> listFrameworksContent() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> listPluginsContent() {
        return Collections.emptyList();

    }

    @Override
    public String getTemplateContent(String url) {
        return "";
    }

    @Override
    public String getRuntimePluginTemplateContent(String url) {
        return "";
    }

    @Override
    public List<RuntimePlugin> getRuntimePlugins() {
        return Collections.emptyList();
    }

}