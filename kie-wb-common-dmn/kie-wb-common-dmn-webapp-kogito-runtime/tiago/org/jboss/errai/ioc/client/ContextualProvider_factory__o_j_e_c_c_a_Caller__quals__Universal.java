package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal extends Factory<Caller> { public ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal() {
    super(new FactoryHandleImpl(Caller.class, "ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Caller.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Caller createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<Caller> provider = (ContextualTypeProvider<Caller>) contextManager.getInstance("Type_factory__o_j_e_i_c_a_b_CallerProvider__quals__j_e_i_Any_j_e_i_Default");
    final Caller instance = provider.provide(typeArgs, qualifiers);
    setReference(instance, "disposer", provider);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((Caller) instance, contextManager);
  }

  public void destroyInstanceHelper(final Caller instance, final ContextManager contextManager) {
    ((Disposer) getReferenceAs(instance, "disposer", Disposer.class)).dispose(instance);
  }
}