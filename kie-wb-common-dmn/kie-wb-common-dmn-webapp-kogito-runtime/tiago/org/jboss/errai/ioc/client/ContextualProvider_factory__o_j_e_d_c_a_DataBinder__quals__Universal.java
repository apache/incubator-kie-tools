package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.databinding.client.HasPropertyChangeHandlers;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ContextualProvider_factory__o_j_e_d_c_a_DataBinder__quals__Universal extends Factory<DataBinder> { public ContextualProvider_factory__o_j_e_d_c_a_DataBinder__quals__Universal() {
    super(new FactoryHandleImpl(DataBinder.class, "ContextualProvider_factory__o_j_e_d_c_a_DataBinder__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { DataBinder.class, Object.class, HasPropertyChangeHandlers.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public DataBinder createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<DataBinder> provider = (ContextualTypeProvider<DataBinder>) contextManager.getInstance("Type_factory__o_j_e_d_c_DataBinderProvider__quals__j_e_i_Any_j_e_i_Default");
    final DataBinder instance = provider.provide(typeArgs, qualifiers);
    return instance;
  }
}