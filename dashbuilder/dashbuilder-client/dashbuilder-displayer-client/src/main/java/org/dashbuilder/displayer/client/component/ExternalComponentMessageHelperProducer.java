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

package org.dashbuilder.displayer.client.component;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.dashbuilder.displayer.external.ExternalComponentMessageHelper;

/**
 * Produce the helper for messages.
 *
 */
@ApplicationScoped
public class ExternalComponentMessageHelperProducer {

    @Produces
    public ExternalComponentMessageHelper produce() {
        return new ExternalComponentMessageHelper();
    }

}