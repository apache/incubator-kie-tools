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
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsScreen;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsScreen.View;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView;
import org.uberfire.ext.preferences.client.event.PreferencesCentralActionsConfigurationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralPreSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesCentralActionsScreen> { public Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferencesCentralActionsScreen.class, "Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsScreen__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferencesCentralActionsScreen.class, Object.class });
  }

  public PreferencesCentralActionsScreen createInstance(final ContextManager contextManager) {
    final Event<PreferencesCentralPreSaveEvent> _preSaveEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PreferencesCentralPreSaveEvent.class }, new Annotation[] { });
    final PlaceManager _placeManager_1 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<PreferencesCentralSaveEvent> _saveEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PreferencesCentralSaveEvent.class }, new Annotation[] { });
    final View _view_0 = (PreferencesCentralActionsView) contextManager.getInstance("Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsView__quals__j_e_i_Any_j_e_i_Default");
    final Event<PreferencesCentralUndoChangesEvent> _undoChangesEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PreferencesCentralUndoChangesEvent.class }, new Annotation[] { });
    final Event<NotificationEvent> _notification_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final PreferencesCentralActionsScreen instance = new PreferencesCentralActionsScreen(_view_0, _placeManager_1, _preSaveEvent_2, _saveEvent_3, _undoChangesEvent_4, _notification_5);
    registerDependentScopedReference(instance, _preSaveEvent_2);
    registerDependentScopedReference(instance, _saveEvent_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _undoChangesEvent_4);
    registerDependentScopedReference(instance, _notification_5);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "initEventSubscription", CDI.subscribeLocal("org.uberfire.ext.preferences.client.event.PreferencesCentralActionsConfigurationEvent", new AbstractCDIEventCallback<PreferencesCentralActionsConfigurationEvent>() {
      public void fireEvent(final PreferencesCentralActionsConfigurationEvent event) {
        instance.initEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.preferences.client.event.PreferencesCentralActionsConfigurationEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PreferencesCentralActionsScreen) instance, contextManager);
  }

  public void destroyInstanceHelper(final PreferencesCentralActionsScreen instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "initEventSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final PreferencesCentralActionsScreen instance) {
    instance.init();
  }
}