/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.rebind.ioc.bootstrapper;

import javax.inject.Provider;

import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.ioc.client.api.IOCProvider;

/**
 * Create factories for beans from {@link Provider providers} annotated with
 * {@link IOCProvider}.
 *
 * @see FactoryBodyGenerator
 * @see AbstractBodyGenerator
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ProviderFactoryBodyGenerator extends BaseProviderGenerator {

  @Override
  protected Class<?> getProviderRawType() {
    return Provider.class;
  }

  @Override
  protected ContextualStatementBuilder invokeProviderStmt(final ContextualStatementBuilder provider) {
    return provider.invoke("get");
  }

}
