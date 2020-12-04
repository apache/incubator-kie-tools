package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.preferences.client.scope.PreferenceScopeResolutionStrategyIOCProvider;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;

public class Type_factory__o_u_p_c_s_PreferenceScopeResolutionStrategyIOCProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeResolutionStrategyIOCProvider> { public Type_factory__o_u_p_c_s_PreferenceScopeResolutionStrategyIOCProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferenceScopeResolutionStrategyIOCProvider.class, "Type_factory__o_u_p_c_s_PreferenceScopeResolutionStrategyIOCProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceScopeResolutionStrategyIOCProvider.class, Object.class, ContextualTypeProvider.class });
  }

  public PreferenceScopeResolutionStrategyIOCProvider createInstance(final ContextManager contextManager) {
    final PreferenceScopeResolutionStrategyIOCProvider instance = new PreferenceScopeResolutionStrategyIOCProvider();
    setIncompleteInstance(instance);
    final PreferenceScopeFactoryImpl PreferenceScopeResolutionStrategyIOCProvider_scopeFactory = (PreferenceScopeFactoryImpl) contextManager.getInstance("Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    PreferenceScopeResolutionStrategyIOCProvider_PreferenceScopeFactory_scopeFactory(instance, PreferenceScopeResolutionStrategyIOCProvider_scopeFactory);
    final ManagedInstance PreferenceScopeResolutionStrategyIOCProvider_scopeResolutionStrategyProvider = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { PreferenceScopeResolutionStrategy.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PreferenceScopeResolutionStrategyIOCProvider_scopeResolutionStrategyProvider);
    PreferenceScopeResolutionStrategyIOCProvider_ManagedInstance_scopeResolutionStrategyProvider(instance, PreferenceScopeResolutionStrategyIOCProvider_scopeResolutionStrategyProvider);
    setIncompleteInstance(null);
    return instance;
  }

  native static ManagedInstance PreferenceScopeResolutionStrategyIOCProvider_ManagedInstance_scopeResolutionStrategyProvider(PreferenceScopeResolutionStrategyIOCProvider instance) /*-{
    return instance.@org.uberfire.preferences.client.scope.PreferenceScopeResolutionStrategyIOCProvider::scopeResolutionStrategyProvider;
  }-*/;

  native static void PreferenceScopeResolutionStrategyIOCProvider_ManagedInstance_scopeResolutionStrategyProvider(PreferenceScopeResolutionStrategyIOCProvider instance, ManagedInstance<PreferenceScopeResolutionStrategy> value) /*-{
    instance.@org.uberfire.preferences.client.scope.PreferenceScopeResolutionStrategyIOCProvider::scopeResolutionStrategyProvider = value;
  }-*/;

  native static PreferenceScopeFactory PreferenceScopeResolutionStrategyIOCProvider_PreferenceScopeFactory_scopeFactory(PreferenceScopeResolutionStrategyIOCProvider instance) /*-{
    return instance.@org.uberfire.preferences.client.scope.PreferenceScopeResolutionStrategyIOCProvider::scopeFactory;
  }-*/;

  native static void PreferenceScopeResolutionStrategyIOCProvider_PreferenceScopeFactory_scopeFactory(PreferenceScopeResolutionStrategyIOCProvider instance, PreferenceScopeFactory value) /*-{
    instance.@org.uberfire.preferences.client.scope.PreferenceScopeResolutionStrategyIOCProvider::scopeFactory = value;
  }-*/;
}