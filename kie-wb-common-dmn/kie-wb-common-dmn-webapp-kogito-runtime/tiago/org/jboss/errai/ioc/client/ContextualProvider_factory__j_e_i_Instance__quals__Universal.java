package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Provider;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ContextualProvider_factory__j_e_i_Instance__quals__Universal extends Factory<Instance> { public ContextualProvider_factory__j_e_i_Instance__quals__Universal() {
    super(new FactoryHandleImpl(Instance.class, "ContextualProvider_factory__j_e_i_Instance__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Instance.class, Iterable.class, Provider.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Instance createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<Instance> provider = (ContextualTypeProvider<Instance>) contextManager.getInstance("Type_factory__o_j_e_e_c_c_InstanceProvider__quals__j_e_i_Any_j_e_i_Default");
    final Instance instance = provider.provide(typeArgs, qualifiers);
    return instance;
  }
}