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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.client.api.Timed;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;

/**
 * @author Mike Brock
 */
@CodeDecorator
public class TimedExtension extends IOCDecoratorExtension<Timed> {
  public TimedExtension(Class<Timed> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public void generateDecorator(Decorable decorable, FactoryController controller) {
    try {
      final Timed timed = (Timed) decorable.getAnnotation();


      final Statement methodInvokation
          = decorable.getAccessStatement();

      final org.jboss.errai.common.client.util.TimeUnit timeUnit = timed.timeUnit();
      final int interval = timed.interval();

      final Statement timerDecl
          = Stmt.nestedCall(Stmt.newObject(Timer.class).extend()
          .publicOverridesMethod("run")
          .append(methodInvokation)
          .finish().finish());

      final String timerVarName = decorable.getAsMethod().getName() + "Timer";
      final Statement timerVar = controller.getReferenceStmt(timerVarName, Timer.class);

      final List<Statement> initStmts = new ArrayList<Statement>();
      final List<Statement> destructionStmts = new ArrayList<Statement>();

      initStmts.add(controller.setReferenceStmt(timerVarName, timerDecl));

      final Statement timerExec;
      switch (timed.type()) {
        case REPEATING:
          timerExec = Stmt.nestedCall(timerVar).invoke("scheduleRepeating", timeUnit.toMillis(interval));
          break;
        default:
        case DELAYED:
          timerExec = Stmt.nestedCall(timerVar).invoke("schedule", timeUnit.toMillis(interval));
          break;
      }

      initStmts.add(timerExec);
      destructionStmts.add(Stmt.nestedCall(timerVar).invoke("cancel"));

      controller.addInitializationStatements(initStmts);
      controller.addDestructionStatements(destructionStmts);
    }

    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
