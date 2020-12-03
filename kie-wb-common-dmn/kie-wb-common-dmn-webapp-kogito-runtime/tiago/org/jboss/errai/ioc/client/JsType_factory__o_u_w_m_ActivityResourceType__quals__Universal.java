package org.jboss.errai.ioc.client;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;

public class JsType_factory__o_u_w_m_ActivityResourceType__quals__Universal extends Factory<ActivityResourceType> { public JsType_factory__o_u_w_m_ActivityResourceType__quals__Universal() {
    super(new FactoryHandleImpl(ActivityResourceType.class, "JsType_factory__o_u_w_m_ActivityResourceType__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ActivityResourceType.class, Enum.class, Object.class, Comparable.class, Serializable.class, ResourceType.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ActivityResourceType createInstance(final ContextManager contextManager) {
    return (ActivityResourceType) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.ActivityResourceType");
  }
}