package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.inject.Provider;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal extends Factory<ManagedInstance> { public ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal() {
    super(new FactoryHandleImpl(ManagedInstance.class, "ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ManagedInstance.class, Provider.class, Iterable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ManagedInstance createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<ManagedInstance> provider = (ContextualTypeProvider<ManagedInstance>) contextManager.getInstance("Type_factory__o_j_e_i_c_a_b_ManagedInstanceProvider__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance instance = provider.provide(typeArgs, qualifiers);
    setReference(instance, "disposer", provider);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ManagedInstance) instance, contextManager);
  }

  public void destroyInstanceHelper(final ManagedInstance instance, final ContextManager contextManager) {
    ((Disposer) getReferenceAs(instance, "disposer", Disposer.class)).dispose(instance);
  }
}