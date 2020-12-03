package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.mvp.Command;

public class JsType_factory__o_u_m_Command__quals__Universal extends Factory<Command> { public JsType_factory__o_u_m_Command__quals__Universal() {
    super(new FactoryHandleImpl(Command.class, "JsType_factory__o_u_m_Command__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Command.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Command createInstance(final ContextManager contextManager) {
    return (Command) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.mvp.Command");
  }
}