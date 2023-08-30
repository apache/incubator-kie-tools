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


package org.jboss.errai.ioc.rebind.ioc.builtin;

import java.util.Collections;

import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;

/**
 * @author Mike Brock
 */
@CodeDecorator
public class AfterInitializationExtension extends IOCDecoratorExtension<AfterInitialization> {
  public AfterInitializationExtension(Class<AfterInitialization> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public void generateDecorator(final Decorable decorable, final FactoryController controller) {
    final Statement callbackStmt = Stmt.newObject(Runnable.class).extend()
            .publicOverridesMethod("run")
            .append(decorable.getAccessStatement())
            .finish()
            .finish();

    controller.addInitializationStatements(Collections.<Statement>singletonList(Stmt.invokeStatic(InitVotes.class, "registerOneTimeInitCallback", callbackStmt)));
  }
}
