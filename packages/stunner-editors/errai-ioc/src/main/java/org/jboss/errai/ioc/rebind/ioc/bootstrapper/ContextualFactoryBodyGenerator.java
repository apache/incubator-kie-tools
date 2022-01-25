/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

import static org.jboss.errai.codegen.util.Stmt.loadVariable;

import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;

/**
 * Generate factories for contextual bean instances provided by a {@link ContextualTypeProvider}.
 *
 * @see FactoryBodyGenerator
 * @see AbstractBodyGenerator
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ContextualFactoryBodyGenerator extends BaseProviderGenerator {

  @Override
  protected ContextualStatementBuilder invokeProviderStmt(final ContextualStatementBuilder provider) {
    return provider.invoke("provide", loadVariable("typeArgs"), loadVariable("qualifiers"));
  }

  @Override
  protected Class<?> getProviderRawType() {
    return ContextualTypeProvider.class;
  }

}
