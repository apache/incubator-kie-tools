package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootScreen;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootScreen.View;
import org.uberfire.ext.preferences.client.central.screen.PreferencesRootView;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent;
import org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo;

public class Type_factory__o_u_e_p_c_c_s_PreferencesRootScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesRootScreen> { public Type_factory__o_u_e_p_c_c_s_PreferencesRootScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferencesRootScreen.class, "Type_factory__o_u_e_p_c_c_s_PreferencesRootScreen__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferencesRootScreen.class, Object.class });
  }

  public PreferencesRootScreen createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_1 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final PreferenceFormBeansInfo _preferenceFormBeansInfo_2 = (PreferenceFormBeansInfo) contextManager.getInstance("Type_factory__o_u_e_p_c_u_PreferenceFormBeansInfo__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (PreferencesRootView) contextManager.getInstance("Type_factory__o_u_e_p_c_c_s_PreferencesRootView__quals__j_e_i_Any_j_e_i_Default");
    final Event<HierarchyItemFormInitializationEvent> _hierarchyItemFormInitializationEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { HierarchyItemFormInitializationEvent.class }, new Annotation[] { });
    final PreferencesRootScreen instance = new PreferencesRootScreen(_view_0, _placeManager_1, _preferenceFormBeansInfo_2, _hierarchyItemFormInitializationEvent_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _hierarchyItemFormInitializationEvent_3);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "hierarchyItemSelectedEventSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent", new AbstractCDIEventCallback<HierarchyItemSelectedEvent>() {
      public void fireEvent(final HierarchyItemSelectedEvent event) {
        instance.hierarchyItemSelectedEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PreferencesRootScreen) instance, contextManager);
  }

  public void destroyInstanceHelper(final PreferencesRootScreen instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "hierarchyItemSelectedEventSubscription", Subscription.class)).remove();
  }
}