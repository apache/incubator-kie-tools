package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionProvided_factory__o_s_Logger__quals__Universal_2 extends Factory<Logger> { public ExtensionProvided_factory__o_s_Logger__quals__Universal_2() {
    super(new FactoryHandleImpl(Logger.class, "ExtensionProvided_factory__o_s_Logger__quals__Universal_2", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Logger.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Logger createInstance(final ContextManager contextManager) {
    return LoggerFactory.getLogger("org.uberfire.client.mvp.ActivityLifecycleErrorHandler");
  }
}