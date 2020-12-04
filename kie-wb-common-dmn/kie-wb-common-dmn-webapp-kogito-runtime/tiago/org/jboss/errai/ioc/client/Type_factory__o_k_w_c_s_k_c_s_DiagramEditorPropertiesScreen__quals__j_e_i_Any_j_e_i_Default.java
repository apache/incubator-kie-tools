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
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenPreMaximizedStateEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidget;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.stunner.kogito.client.view.DiagramEditorScreenView;
import org.kie.workbench.common.stunner.kogito.client.view.DiagramEditorScreenViewImpl;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;

public class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorPropertiesScreen> { public Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramEditorPropertiesScreen.class, "Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramEditorPropertiesScreen.class, Object.class });
  }

  public DiagramEditorPropertiesScreen createInstance(final ContextManager contextManager) {
    final SessionManager _clientSessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<ScreenPreMaximizedStateEvent> _screenStateEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ScreenPreMaximizedStateEvent.class }, new Annotation[] { });
    final FormPropertiesWidget _formPropertiesWidget_0 = (FormPropertiesWidget) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidget__quals__j_e_i_Any_j_e_i_Default");
    final Event<ChangeTitleWidgetEvent> _changeTitleNotification_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ChangeTitleWidgetEvent.class }, new Annotation[] { });
    final DiagramEditorScreenView _view_3 = (DiagramEditorScreenViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_k_c_v_DiagramEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DiagramEditorPropertiesScreen instance = new DiagramEditorPropertiesScreen(_formPropertiesWidget_0, _clientSessionManager_1, _changeTitleNotification_2, _view_3, _screenStateEvent_4);
    registerDependentScopedReference(instance, _screenStateEvent_4);
    registerDependentScopedReference(instance, _formPropertiesWidget_0);
    registerDependentScopedReference(instance, _changeTitleNotification_2);
    registerDependentScopedReference(instance, _view_3);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onPlaceMaximizedEventSubscription", CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceMaximizedEvent", new AbstractCDIEventCallback<PlaceMaximizedEvent>() {
      public void fireEvent(final PlaceMaximizedEvent event) {
        DiagramEditorPropertiesScreen_onPlaceMaximizedEvent_PlaceMaximizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceMaximizedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onFormPropertiesOpenedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened", new AbstractCDIEventCallback<FormPropertiesOpened>() {
      public void fireEvent(final FormPropertiesOpened event) {
        DiagramEditorPropertiesScreen_onFormPropertiesOpened_FormPropertiesOpened(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened []";
      }
    }));
    thisInstance.setReference(instance, "onSessionOpenedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent", new AbstractCDIEventCallback<SessionDiagramOpenedEvent>() {
      public void fireEvent(final SessionDiagramOpenedEvent event) {
        DiagramEditorPropertiesScreen_onSessionOpened_SessionDiagramOpenedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DiagramEditorPropertiesScreen) instance, contextManager);
  }

  public void destroyInstanceHelper(final DiagramEditorPropertiesScreen instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onPlaceMaximizedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onFormPropertiesOpenedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onSessionOpenedSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final DiagramEditorPropertiesScreen instance) {
    instance.init();
  }

  public native static void DiagramEditorPropertiesScreen_onFormPropertiesOpened_FormPropertiesOpened(DiagramEditorPropertiesScreen instance, FormPropertiesOpened a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen::onFormPropertiesOpened(Lorg/kie/workbench/common/stunner/forms/client/event/FormPropertiesOpened;)(a0);
  }-*/;

  public native static void DiagramEditorPropertiesScreen_onPlaceMaximizedEvent_PlaceMaximizedEvent(DiagramEditorPropertiesScreen instance, PlaceMaximizedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen::onPlaceMaximizedEvent(Lorg/uberfire/client/workbench/events/PlaceMaximizedEvent;)(a0);
  }-*/;

  public native static void DiagramEditorPropertiesScreen_onSessionOpened_SessionDiagramOpenedEvent(DiagramEditorPropertiesScreen instance, SessionDiagramOpenedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen::onSessionOpened(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionDiagramOpenedEvent;)(a0);
  }-*/;
}