/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.wildfly.client.provider;

import org.guvnor.ala.ui.client.handler.BaseFormResolverTest;
import org.guvnor.ala.ui.client.handler.FormResolver;
import org.guvnor.ala.ui.wildfly.client.handler.WildflyFormResolver;
import org.jboss.errai.ioc.client.api.ManagedInstance;

public class WildflyFormResolverTest
        extends BaseFormResolverTest<WF10ProviderConfigPresenter> {

    @Override
    protected FormResolver<WF10ProviderConfigPresenter> createFormResolver(ManagedInstance<WF10ProviderConfigPresenter> managedInstance) {
        return new WildflyFormResolver(managedInstance);
    }
}
