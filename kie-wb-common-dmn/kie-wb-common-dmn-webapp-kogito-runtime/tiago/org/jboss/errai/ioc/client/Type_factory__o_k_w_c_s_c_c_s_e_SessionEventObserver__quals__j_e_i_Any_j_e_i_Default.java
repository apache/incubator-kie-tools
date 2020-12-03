package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedHandler;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedHandler;
import org.kie.workbench.common.stunner.core.client.session.event.SessionEventObserver;

public class Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionEventObserver> { private class Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends SessionEventObserver implements Proxy<SessionEventObserver> {
    private final ProxyHelper<SessionEventObserver> proxyHelper = new ProxyHelperImpl<SessionEventObserver>("Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final SessionEventObserver instance) {

    }

    public SessionEventObserver asBeanType() {
      return this;
    }

    public void setInstance(final SessionEventObserver instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final SessionEventObserver proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionEventObserver.class, "Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionEventObserver.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent", new AbstractCDIEventCallback<SessionDiagramOpenedEvent>() {
      public void fireEvent(final SessionDiagramOpenedEvent event) {
        final SessionEventObserver instance = Factory.maybeUnwrapProxy((SessionEventObserver) context.getInstance("Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_Default"));
        SessionEventObserver_onSessionDiagramOpenedEvent_SessionDiagramOpenedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent", new AbstractCDIEventCallback<SessionDiagramSavedEvent>() {
      public void fireEvent(final SessionDiagramSavedEvent event) {
        final SessionEventObserver instance = Factory.maybeUnwrapProxy((SessionEventObserver) context.getInstance("Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_Default"));
        SessionEventObserver_onSessionDiagramSavedEvent_SessionDiagramSavedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent []";
      }
    });
  }

  public SessionEventObserver createInstance(final ContextManager contextManager) {
    final Instance<SessionDiagramOpenedHandler> _sessionDiagramOpenedHandlersInstance_0 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { SessionDiagramOpenedHandler.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final Instance<SessionDiagramSavedHandler> _sessionDiagramSavedHandlersInstance_1 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { SessionDiagramSavedHandler.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final SessionEventObserver instance = new SessionEventObserver(_sessionDiagramOpenedHandlersInstance_0, _sessionDiagramSavedHandlersInstance_1);
    registerDependentScopedReference(instance, _sessionDiagramOpenedHandlersInstance_0);
    registerDependentScopedReference(instance, _sessionDiagramSavedHandlersInstance_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<SessionEventObserver> proxyImpl = new Type_factory__o_k_w_c_s_c_c_s_e_SessionEventObserver__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void SessionEventObserver_onSessionDiagramOpenedEvent_SessionDiagramOpenedEvent(SessionEventObserver instance, SessionDiagramOpenedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.event.SessionEventObserver::onSessionDiagramOpenedEvent(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionDiagramOpenedEvent;)(a0);
  }-*/;

  public native static void SessionEventObserver_onSessionDiagramSavedEvent_SessionDiagramSavedEvent(SessionEventObserver instance, SessionDiagramSavedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.event.SessionEventObserver::onSessionDiagramSavedEvent(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionDiagramSavedEvent;)(a0);
  }-*/;
}