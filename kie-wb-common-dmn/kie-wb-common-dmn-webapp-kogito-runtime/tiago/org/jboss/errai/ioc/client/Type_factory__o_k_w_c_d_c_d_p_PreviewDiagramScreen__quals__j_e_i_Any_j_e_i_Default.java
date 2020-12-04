package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen.View;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;

public class Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<PreviewDiagramScreen> { public Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreviewDiagramScreen.class, "Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreen__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreviewDiagramScreen.class, Object.class });
  }

  public PreviewDiagramScreen createInstance(final ContextManager contextManager) {
    final SessionManager _clientSessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _session_3 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<SessionDiagramPreview<AbstractSession>> _sessionPreviews_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SessionDiagramPreview.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
    final View _view_2 = (PreviewDiagramScreenView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenView__quals__j_e_i_Any_j_e_i_Default");
    final PreviewDiagramScreen instance = new PreviewDiagramScreen(_clientSessionManager_0, _sessionPreviews_1, _view_2, _session_3);
    registerDependentScopedReference(instance, _sessionPreviews_1);
    registerDependentScopedReference(instance, _view_2);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasSessionOpenedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent", new AbstractCDIEventCallback<SessionOpenedEvent>() {
      public void fireEvent(final SessionOpenedEvent event) {
        PreviewDiagramScreen_onCanvasSessionOpened_SessionOpenedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasSessionDestroyedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent", new AbstractCDIEventCallback<SessionDestroyedEvent>() {
      public void fireEvent(final SessionDestroyedEvent event) {
        PreviewDiagramScreen_onCanvasSessionDestroyed_SessionDestroyedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onSessionDiagramOpenedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent", new AbstractCDIEventCallback<SessionDiagramOpenedEvent>() {
      public void fireEvent(final SessionDiagramOpenedEvent event) {
        PreviewDiagramScreen_onSessionDiagramOpenedEvent_SessionDiagramOpenedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PreviewDiagramScreen) instance, contextManager);
  }

  public void destroyInstanceHelper(final PreviewDiagramScreen instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSessionOpenedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSessionDestroyedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onSessionDiagramOpenedEventSubscription", Subscription.class)).remove();
  }

  public native static void PreviewDiagramScreen_onCanvasSessionDestroyed_SessionDestroyedEvent(PreviewDiagramScreen instance, SessionDestroyedEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen::onCanvasSessionDestroyed(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionDestroyedEvent;)(a0);
  }-*/;

  public native static void PreviewDiagramScreen_onSessionDiagramOpenedEvent_SessionDiagramOpenedEvent(PreviewDiagramScreen instance, SessionDiagramOpenedEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen::onSessionDiagramOpenedEvent(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionDiagramOpenedEvent;)(a0);
  }-*/;

  public native static void PreviewDiagramScreen_onCanvasSessionOpened_SessionOpenedEvent(PreviewDiagramScreen instance, SessionOpenedEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen::onCanvasSessionOpened(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionOpenedEvent;)(a0);
  }-*/;
}