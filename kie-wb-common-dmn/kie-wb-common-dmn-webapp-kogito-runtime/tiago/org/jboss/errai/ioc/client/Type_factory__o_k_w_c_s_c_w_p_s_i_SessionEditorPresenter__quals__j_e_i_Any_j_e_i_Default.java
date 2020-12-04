package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.event.SessionLostFocusEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.RequestSessionRefreshEvent;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter.View;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.AbstractSessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionCardinalityStateHandler;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbar;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionEditorPresenter> { public Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionEditorPresenter.class, "Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionEditorPresenter.class, AbstractSessionPresenter.class, Object.class, SessionPresenter.class, Viewer.class, SessionDiagramPresenter.class, SessionPresenter.class, Viewer.class });
  }

  public SessionEditorPresenter createInstance(final ContextManager contextManager) {
    final NotificationsObserver _notificationsObserver_7 = (NotificationsObserver) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_n_NotificationsObserver__quals__j_e_i_Any_j_e_i_Default");
    final View _view_11 = (SessionPresenterView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPresenterView__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final Event<SessionFocusedEvent> _sessionFocusedEvent_8 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionFocusedEvent.class }, new Annotation[] { });
    final Event<SessionDiagramOpenedEvent> _sessionDiagramOpenedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionDiagramOpenedEvent.class }, new Annotation[] { });
    final ManagedInstance<EditorToolbar> _toolbars_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { EditorToolbar.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final SessionCardinalityStateHandler _cardinalityStateHandler_3 = (SessionCardinalityStateHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_s_i_SessionCardinalityStateHandler__quals__j_e_i_Any_j_e_i_Default");
    final DefaultPaletteFactory<AbstractCanvasHandler> _paletteWidgetFactory_6 = (DefaultPaletteFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_DefaultPaletteFactory__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final SessionEditorImpl _editor_2 = (SessionEditorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_s_i_SessionEditorImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasLostFocusEvent> _canvasLostFocusEventEvent_10 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasLostFocusEvent.class }, new Annotation[] { });
    final Event<SessionLostFocusEvent> _sessionLostFocusEvent_9 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionLostFocusEvent.class }, new Annotation[] { });
    final SessionEditorPresenter instance = new SessionEditorPresenter(_definitionUtils_0, _sessionManager_1, _editor_2, _cardinalityStateHandler_3, _sessionDiagramOpenedEvent_4, _toolbars_5, _paletteWidgetFactory_6, _notificationsObserver_7, _sessionFocusedEvent_8, _sessionLostFocusEvent_9, _canvasLostFocusEventEvent_10, _view_11);
    registerDependentScopedReference(instance, _notificationsObserver_7);
    registerDependentScopedReference(instance, _view_11);
    registerDependentScopedReference(instance, _sessionFocusedEvent_8);
    registerDependentScopedReference(instance, _sessionDiagramOpenedEvent_4);
    registerDependentScopedReference(instance, _toolbars_5);
    registerDependentScopedReference(instance, _cardinalityStateHandler_3);
    registerDependentScopedReference(instance, _paletteWidgetFactory_6);
    registerDependentScopedReference(instance, _editor_2);
    registerDependentScopedReference(instance, _canvasLostFocusEventEvent_10);
    registerDependentScopedReference(instance, _sessionLostFocusEvent_9);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onScreenMaximizedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent", new AbstractCDIEventCallback<ScreenMaximizedEvent>() {
      public void fireEvent(final ScreenMaximizedEvent event) {
        SessionEditorPresenter_onScreenMaximizedEvent_ScreenMaximizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onScreenMinimizedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent", new AbstractCDIEventCallback<ScreenMinimizedEvent>() {
      public void fireEvent(final ScreenMinimizedEvent event) {
        SessionEditorPresenter_onScreenMinimizedEvent_ScreenMinimizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent []";
      }
    }));
    thisInstance.setReference(instance, "commandExecutedFiredSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent", new AbstractCDIEventCallback<CanvasCommandExecutedEvent>() {
      public void fireEvent(final CanvasCommandExecutedEvent event) {
        SessionEditorPresenter_commandExecutedFired_CanvasCommandExecutedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent []";
      }
    }));
    thisInstance.setReference(instance, "commandUndoExecutedFiredSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent", new AbstractCDIEventCallback<CanvasCommandUndoneEvent>() {
      public void fireEvent(final CanvasCommandUndoneEvent event) {
        SessionEditorPresenter_commandUndoExecutedFired_CanvasCommandUndoneEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent []";
      }
    }));
    thisInstance.setReference(instance, "onRequestSessionRefreshEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.client.widgets.presenters.session.RequestSessionRefreshEvent", new AbstractCDIEventCallback<RequestSessionRefreshEvent>() {
      public void fireEvent(final RequestSessionRefreshEvent event) {
        SessionEditorPresenter_onRequestSessionRefreshEvent_RequestSessionRefreshEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.client.widgets.presenters.session.RequestSessionRefreshEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SessionEditorPresenter) instance, contextManager);
  }

  public void destroyInstanceHelper(final SessionEditorPresenter instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onScreenMaximizedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onScreenMinimizedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "commandExecutedFiredSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "commandUndoExecutedFiredSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onRequestSessionRefreshEventSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final SessionEditorPresenter instance) {
    instance.init();
  }

  public native static void SessionEditorPresenter_onRequestSessionRefreshEvent_RequestSessionRefreshEvent(SessionEditorPresenter instance, RequestSessionRefreshEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter::onRequestSessionRefreshEvent(Lorg/kie/workbench/common/stunner/client/widgets/presenters/session/RequestSessionRefreshEvent;)(a0);
  }-*/;

  public native static void SessionEditorPresenter_onScreenMaximizedEvent_ScreenMaximizedEvent(SessionEditorPresenter instance, ScreenMaximizedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter::onScreenMaximizedEvent(Lorg/kie/workbench/common/stunner/core/client/event/screen/ScreenMaximizedEvent;)(a0);
  }-*/;

  public native static void SessionEditorPresenter_onScreenMinimizedEvent_ScreenMinimizedEvent(SessionEditorPresenter instance, ScreenMinimizedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter::onScreenMinimizedEvent(Lorg/kie/workbench/common/stunner/core/client/event/screen/ScreenMinimizedEvent;)(a0);
  }-*/;

  public native static void SessionEditorPresenter_commandExecutedFired_CanvasCommandExecutedEvent(SessionEditorPresenter instance, CanvasCommandExecutedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter::commandExecutedFired(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandExecutedEvent;)(a0);
  }-*/;

  public native static void SessionEditorPresenter_commandUndoExecutedFired_CanvasCommandUndoneEvent(SessionEditorPresenter instance, CanvasCommandUndoneEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter::commandUndoExecutedFired(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandUndoneEvent;)(a0);
  }-*/;
}