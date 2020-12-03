package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class JsType_factory__o_u_c_w_t_ClientResourceType__quals__Universal extends Factory<ClientResourceType> { public JsType_factory__o_u_c_w_t_ClientResourceType__quals__Universal() {
    super(new FactoryHandleImpl(ClientResourceType.class, "JsType_factory__o_u_c_w_t_ClientResourceType__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ClientResourceType.class, ResourceTypeDefinition.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ClientResourceType createInstance(final ContextManager contextManager) {
    return (ClientResourceType) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.client.workbench.type.ClientResourceType");
  }
}