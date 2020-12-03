package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Event;
import javax.inject.Singleton;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.client.service.auth.ExperimentalActivitiesAuthorizationManagerImpl;
import org.uberfire.experimental.client.service.impl.ClientExperimentalFeaturesRegistryServiceImpl;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.experimental.service.events.NonPortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;

public class Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalActivitiesAuthorizationManagerImpl> { public Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExperimentalActivitiesAuthorizationManagerImpl.class, "Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExperimentalActivitiesAuthorizationManagerImpl.class, Object.class, ExperimentalActivitiesAuthorizationManager.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent", new AbstractCDIEventCallback<PortableExperimentalFeatureModifiedEvent>() {
      public void fireEvent(final PortableExperimentalFeatureModifiedEvent event) {
        final ExperimentalActivitiesAuthorizationManagerImpl instance = Factory.maybeUnwrapProxy((ExperimentalActivitiesAuthorizationManagerImpl) context.getInstance("Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default"));
        instance.onFeatureModified(event);
      }
      public String toString() {
        return "Observer: org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.experimental.service.events.NonPortableExperimentalFeatureModifiedEvent", new AbstractCDIEventCallback<NonPortableExperimentalFeatureModifiedEvent>() {
      public void fireEvent(final NonPortableExperimentalFeatureModifiedEvent event) {
        final ExperimentalActivitiesAuthorizationManagerImpl instance = Factory.maybeUnwrapProxy((ExperimentalActivitiesAuthorizationManagerImpl) context.getInstance("Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default"));
        instance.onFeatureModified(event);
      }
      public String toString() {
        return "Observer: org.uberfire.experimental.service.events.NonPortableExperimentalFeatureModifiedEvent []";
      }
    });
  }

  public ExperimentalActivitiesAuthorizationManagerImpl createInstance(final ContextManager contextManager) {
    final SyncBeanManager _iocManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientExperimentalFeaturesRegistryService _experimentalFeaturesRegistryService_1 = (ClientExperimentalFeaturesRegistryServiceImpl) contextManager.getInstance("Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<PerspectiveVisibiltiyChangeEvent> _perspectiveVisibleEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PerspectiveVisibiltiyChangeEvent.class }, new Annotation[] { });
    final ExperimentalActivitiesAuthorizationManagerImpl instance = new ExperimentalActivitiesAuthorizationManagerImpl(_iocManager_0, _experimentalFeaturesRegistryService_1, _perspectiveVisibleEvent_2);
    registerDependentScopedReference(instance, _iocManager_0);
    registerDependentScopedReference(instance, _perspectiveVisibleEvent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}