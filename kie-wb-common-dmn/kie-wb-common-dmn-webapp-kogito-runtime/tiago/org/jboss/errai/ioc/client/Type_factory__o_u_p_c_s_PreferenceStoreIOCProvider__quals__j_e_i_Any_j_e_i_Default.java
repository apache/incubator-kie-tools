package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.preferences.client.store.PreferenceStoreIOCProvider;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;

public class Type_factory__o_u_p_c_s_PreferenceStoreIOCProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceStoreIOCProvider> { public Type_factory__o_u_p_c_s_PreferenceStoreIOCProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferenceStoreIOCProvider.class, "Type_factory__o_u_p_c_s_PreferenceStoreIOCProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceStoreIOCProvider.class, Object.class, ContextualTypeProvider.class });
  }

  public PreferenceStoreIOCProvider createInstance(final ContextManager contextManager) {
    final PreferenceStoreIOCProvider instance = new PreferenceStoreIOCProvider();
    setIncompleteInstance(instance);
    final Caller PreferenceStoreIOCProvider_preferenceStoreCaller = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { PreferenceStore.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PreferenceStoreIOCProvider_preferenceStoreCaller);
    PreferenceStoreIOCProvider_Caller_preferenceStoreCaller(instance, PreferenceStoreIOCProvider_preferenceStoreCaller);
    final ManagedInstance PreferenceStoreIOCProvider_scopeResolutionStrategyProvider = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { PreferenceScopeResolutionStrategy.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PreferenceStoreIOCProvider_scopeResolutionStrategyProvider);
    PreferenceStoreIOCProvider_ManagedInstance_scopeResolutionStrategyProvider(instance, PreferenceStoreIOCProvider_scopeResolutionStrategyProvider);
    final PreferenceScopeFactoryImpl PreferenceStoreIOCProvider_scopeFactory = (PreferenceScopeFactoryImpl) contextManager.getInstance("Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    PreferenceStoreIOCProvider_PreferenceScopeFactory_scopeFactory(instance, PreferenceStoreIOCProvider_scopeFactory);
    setIncompleteInstance(null);
    return instance;
  }

  native static ManagedInstance PreferenceStoreIOCProvider_ManagedInstance_scopeResolutionStrategyProvider(PreferenceStoreIOCProvider instance) /*-{
    return instance.@org.uberfire.preferences.client.store.PreferenceStoreIOCProvider::scopeResolutionStrategyProvider;
  }-*/;

  native static void PreferenceStoreIOCProvider_ManagedInstance_scopeResolutionStrategyProvider(PreferenceStoreIOCProvider instance, ManagedInstance<PreferenceScopeResolutionStrategy> value) /*-{
    instance.@org.uberfire.preferences.client.store.PreferenceStoreIOCProvider::scopeResolutionStrategyProvider = value;
  }-*/;

  native static PreferenceScopeFactory PreferenceStoreIOCProvider_PreferenceScopeFactory_scopeFactory(PreferenceStoreIOCProvider instance) /*-{
    return instance.@org.uberfire.preferences.client.store.PreferenceStoreIOCProvider::scopeFactory;
  }-*/;

  native static void PreferenceStoreIOCProvider_PreferenceScopeFactory_scopeFactory(PreferenceStoreIOCProvider instance, PreferenceScopeFactory value) /*-{
    instance.@org.uberfire.preferences.client.store.PreferenceStoreIOCProvider::scopeFactory = value;
  }-*/;

  native static Caller PreferenceStoreIOCProvider_Caller_preferenceStoreCaller(PreferenceStoreIOCProvider instance) /*-{
    return instance.@org.uberfire.preferences.client.store.PreferenceStoreIOCProvider::preferenceStoreCaller;
  }-*/;

  native static void PreferenceStoreIOCProvider_Caller_preferenceStoreCaller(PreferenceStoreIOCProvider instance, Caller<PreferenceStore> value) /*-{
    instance.@org.uberfire.preferences.client.store.PreferenceStoreIOCProvider::preferenceStoreCaller = value;
  }-*/;
}