/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.quarkus.devui.runtime.forms.impl;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jbpm.devconsole.commons.forms.FormsStorage;
import org.jbpm.devconsole.commons.forms.impl.FormsStorageImpl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

public class FormsStorageProducer {

    public static final String PROJECT_FORM_STORAGE_PROP = "quarkus.kogito-runtime-tools.forms.folder";

    @Produces
    @ApplicationScoped
    public FormsStorage formsStorage() {
        return new FormsStorageImpl(ConfigProvider.getConfig()
                .getOptionalValue(PROJECT_FORM_STORAGE_PROP, String.class)
                .orElse(null));
    }
}
