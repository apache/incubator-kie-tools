package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.event.SessionFocusedEvent;
import org.kie.workbench.common.stunner.client.widgets.event.SessionLostFocusEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter.View;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.AbstractSessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbar;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionViewerPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionViewerPresenter> { public Type_factory__o_k_w_c_s_c_w_p_s_i_SessionViewerPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionViewerPresenter.class, "Type_factory__o_k_w_c_s_c_w_p_s_i_SessionViewerPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionViewerPresenter.class, AbstractSessionPresenter.class, Object.class, SessionPresenter.class, Viewer.class, SessionDiagramPresenter.class, SessionPresenter.class, Viewer.class });
  }

  public SessionViewerPresenter createInstance(final ContextManager contextManager) {
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final NotificationsObserver _notificationsObserver_5 = (NotificationsObserver) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_n_NotificationsObserver__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasLostFocusEvent> _canvasLostFocusEventEvent_8 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasLostFocusEvent.class }, new Annotation[] { });
    final View _view_9 = (SessionPresenterView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPresenterView__quals__j_e_i_Any_j_e_i_Default");
    final Event<SessionDiagramOpenedEvent> _sessionDiagramOpenedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionDiagramOpenedEvent.class }, new Annotation[] { });
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final SessionViewerImpl _viewer_2 = (SessionViewerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_s_i_SessionViewerImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<SessionFocusedEvent> _sessionFocusedEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionFocusedEvent.class }, new Annotation[] { });
    final Event<SessionLostFocusEvent> _sessionLostFocusEvent_7 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionLostFocusEvent.class }, new Annotation[] { });
    final ManagedInstance<ViewerToolbar> _toolbars_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ViewerToolbar.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final SessionViewerPresenter instance = new SessionViewerPresenter(_definitionUtils_0, _sessionManager_1, _viewer_2, _toolbars_3, _sessionDiagramOpenedEvent_4, _notificationsObserver_5, _sessionFocusedEvent_6, _sessionLostFocusEvent_7, _canvasLostFocusEventEvent_8, _view_9);
    registerDependentScopedReference(instance, _notificationsObserver_5);
    registerDependentScopedReference(instance, _canvasLostFocusEventEvent_8);
    registerDependentScopedReference(instance, _view_9);
    registerDependentScopedReference(instance, _sessionDiagramOpenedEvent_4);
    registerDependentScopedReference(instance, _viewer_2);
    registerDependentScopedReference(instance, _sessionFocusedEvent_6);
    registerDependentScopedReference(instance, _sessionLostFocusEvent_7);
    registerDependentScopedReference(instance, _toolbars_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final SessionViewerPresenter instance) {
    instance.init();
  }
}