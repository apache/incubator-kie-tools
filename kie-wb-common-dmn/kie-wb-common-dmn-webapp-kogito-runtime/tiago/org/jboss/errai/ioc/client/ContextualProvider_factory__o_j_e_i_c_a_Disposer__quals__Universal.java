package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ContextualProvider_factory__o_j_e_i_c_a_Disposer__quals__Universal extends Factory<Disposer> { public ContextualProvider_factory__o_j_e_i_c_a_Disposer__quals__Universal() {
    super(new FactoryHandleImpl(Disposer.class, "ContextualProvider_factory__o_j_e_i_c_a_Disposer__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Disposer.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Disposer createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<Disposer> provider = (ContextualTypeProvider<Disposer>) contextManager.getInstance("Type_factory__o_j_e_i_c_a_b_DisposerProvider__quals__j_e_i_Any_j_e_i_Default");
    final Disposer instance = provider.provide(typeArgs, qualifiers);
    return instance;
  }
}