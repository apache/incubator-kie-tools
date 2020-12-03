package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.preferences.client.store.PreferenceStore;

public class ContextualProvider_factory__o_u_p_c_s_PreferenceStore__quals__Universal extends Factory<PreferenceStore> { public ContextualProvider_factory__o_u_p_c_s_PreferenceStore__quals__Universal() {
    super(new FactoryHandleImpl(PreferenceStore.class, "ContextualProvider_factory__o_u_p_c_s_PreferenceStore__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PreferenceStore.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PreferenceStore createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<PreferenceStore> provider = (ContextualTypeProvider<PreferenceStore>) contextManager.getInstance("Type_factory__o_u_p_c_s_PreferenceStoreIOCProvider__quals__j_e_i_Any_j_e_i_Default");
    final PreferenceStore instance = provider.provide(typeArgs, qualifiers);
    return instance;
  }
}