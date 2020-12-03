package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class JsType_factory__o_u_c_m_WorkbenchActivity__quals__Universal extends Factory<WorkbenchActivity> { public JsType_factory__o_u_c_m_WorkbenchActivity__quals__Universal() {
    super(new FactoryHandleImpl(WorkbenchActivity.class, "JsType_factory__o_u_c_m_WorkbenchActivity__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { WorkbenchActivity.class, ContextSensitiveActivity.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public WorkbenchActivity createInstance(final ContextManager contextManager) {
    return (WorkbenchActivity) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.client.mvp.WorkbenchActivity");
  }
}