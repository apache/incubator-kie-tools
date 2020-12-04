package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.experimental.client.UberfireExperimentalEntryPoint;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesDefRegistry;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.client.service.auth.ExperimentalActivitiesAuthorizationManagerImpl;
import org.uberfire.experimental.client.service.impl.CDIClientFeatureDefRegistry;
import org.uberfire.experimental.client.service.impl.ClientExperimentalFeaturesRegistryServiceImpl;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;

public class Type_factory__o_u_e_c_UberfireExperimentalEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireExperimentalEntryPoint> { public Type_factory__o_u_e_c_UberfireExperimentalEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UberfireExperimentalEntryPoint.class, "Type_factory__o_u_e_c_UberfireExperimentalEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { UberfireExperimentalEntryPoint.class, Object.class });
  }

  public UberfireExperimentalEntryPoint createInstance(final ContextManager contextManager) {
    final ExperimentalActivitiesAuthorizationManager _activitiesAuthorizationManager_2 = (ExperimentalActivitiesAuthorizationManagerImpl) contextManager.getInstance("Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClientExperimentalFeaturesRegistryService _registryService_0 = (ClientExperimentalFeaturesRegistryServiceImpl) contextManager.getInstance("Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClientExperimentalFeaturesDefRegistry _defRegistry_1 = (CDIClientFeatureDefRegistry) contextManager.getInstance("Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default");
    final UberfireExperimentalEntryPoint instance = new UberfireExperimentalEntryPoint(_registryService_0, _defRegistry_1, _activitiesAuthorizationManager_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UberfireExperimentalEntryPoint instance) {
    instance.init();
  }
}