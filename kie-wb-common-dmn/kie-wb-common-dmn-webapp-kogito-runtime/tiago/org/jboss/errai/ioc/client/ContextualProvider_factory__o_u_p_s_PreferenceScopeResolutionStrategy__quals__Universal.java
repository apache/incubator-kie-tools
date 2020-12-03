package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;

public class ContextualProvider_factory__o_u_p_s_PreferenceScopeResolutionStrategy__quals__Universal extends Factory<PreferenceScopeResolutionStrategy> { public ContextualProvider_factory__o_u_p_s_PreferenceScopeResolutionStrategy__quals__Universal() {
    super(new FactoryHandleImpl(PreferenceScopeResolutionStrategy.class, "ContextualProvider_factory__o_u_p_s_PreferenceScopeResolutionStrategy__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PreferenceScopeResolutionStrategy.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PreferenceScopeResolutionStrategy createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<PreferenceScopeResolutionStrategy> provider = (ContextualTypeProvider<PreferenceScopeResolutionStrategy>) contextManager.getInstance("Type_factory__o_u_p_c_s_PreferenceScopeResolutionStrategyIOCProvider__quals__j_e_i_Any_j_e_i_Default");
    final PreferenceScopeResolutionStrategy instance = provider.provide(typeArgs, qualifiers);
    return instance;
  }
}