package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenPreMaximizedStateEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen.View;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

public class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorExplorerScreen> { public Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramEditorExplorerScreen.class, "Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreen__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramEditorExplorerScreen.class, Object.class });
  }

  public DiagramEditorExplorerScreen createInstance(final ContextManager contextManager) {
    final ErrorPopupPresenter _errorPopupPresenter_4 = (ErrorPopupPresenter) contextManager.getInstance("Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default");
    final Event<ScreenPreMaximizedStateEvent> _screenStateEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ScreenPreMaximizedStateEvent.class }, new Annotation[] { });
    final ManagedInstance<TreeExplorer> _treeExplorers_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { TreeExplorer.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<SessionDiagramPreview<AbstractSession>> _sessionPreviews_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SessionDiagramPreview.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new Default() {
        public Class annotationType() {
          return Default.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Default()";
        }
    } });
    final View _view_5 = (DiagramEditorExplorerScreenView) contextManager.getInstance("Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenView__quals__j_e_i_Any_j_e_i_Default");
    final Event<ChangeTitleWidgetEvent> _changeTitleNotificationEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ChangeTitleWidgetEvent.class }, new Annotation[] { });
    final SessionManager _clientSessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final DiagramEditorExplorerScreen instance = new DiagramEditorExplorerScreen(_clientSessionManager_0, _treeExplorers_1, _changeTitleNotificationEvent_2, _sessionPreviews_3, _errorPopupPresenter_4, _view_5, _screenStateEvent_6);
    registerDependentScopedReference(instance, _screenStateEvent_6);
    registerDependentScopedReference(instance, _treeExplorers_1);
    registerDependentScopedReference(instance, _sessionPreviews_3);
    registerDependentScopedReference(instance, _view_5);
    registerDependentScopedReference(instance, _changeTitleNotificationEvent_2);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasSessionOpenedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent", new AbstractCDIEventCallback<SessionOpenedEvent>() {
      public void fireEvent(final SessionOpenedEvent event) {
        DiagramEditorExplorerScreen_onCanvasSessionOpened_SessionOpenedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasSessionDestroyedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent", new AbstractCDIEventCallback<SessionDestroyedEvent>() {
      public void fireEvent(final SessionDestroyedEvent event) {
        DiagramEditorExplorerScreen_onCanvasSessionDestroyed_SessionDestroyedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onSessionDiagramOpenedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent", new AbstractCDIEventCallback<SessionDiagramOpenedEvent>() {
      public void fireEvent(final SessionDiagramOpenedEvent event) {
        DiagramEditorExplorerScreen_onSessionDiagramOpenedEvent_SessionDiagramOpenedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onPlaceMaximizedEventSubscription", CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceMaximizedEvent", new AbstractCDIEventCallback<PlaceMaximizedEvent>() {
      public void fireEvent(final PlaceMaximizedEvent event) {
        DiagramEditorExplorerScreen_onPlaceMaximizedEvent_PlaceMaximizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceMaximizedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DiagramEditorExplorerScreen) instance, contextManager);
  }

  public void destroyInstanceHelper(final DiagramEditorExplorerScreen instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSessionOpenedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSessionDestroyedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onSessionDiagramOpenedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onPlaceMaximizedEventSubscription", Subscription.class)).remove();
  }

  public native static void DiagramEditorExplorerScreen_onPlaceMaximizedEvent_PlaceMaximizedEvent(DiagramEditorExplorerScreen instance, PlaceMaximizedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen::onPlaceMaximizedEvent(Lorg/uberfire/client/workbench/events/PlaceMaximizedEvent;)(a0);
  }-*/;

  public native static void DiagramEditorExplorerScreen_onCanvasSessionDestroyed_SessionDestroyedEvent(DiagramEditorExplorerScreen instance, SessionDestroyedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen::onCanvasSessionDestroyed(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionDestroyedEvent;)(a0);
  }-*/;

  public native static void DiagramEditorExplorerScreen_onCanvasSessionOpened_SessionOpenedEvent(DiagramEditorExplorerScreen instance, SessionOpenedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen::onCanvasSessionOpened(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionOpenedEvent;)(a0);
  }-*/;

  public native static void DiagramEditorExplorerScreen_onSessionDiagramOpenedEvent_SessionDiagramOpenedEvent(DiagramEditorExplorerScreen instance, SessionDiagramOpenedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen::onSessionDiagramOpenedEvent(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionDiagramOpenedEvent;)(a0);
  }-*/;
}