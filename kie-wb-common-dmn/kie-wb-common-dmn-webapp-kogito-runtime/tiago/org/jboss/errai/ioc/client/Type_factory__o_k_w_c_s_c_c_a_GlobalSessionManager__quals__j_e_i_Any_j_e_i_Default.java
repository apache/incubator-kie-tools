package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default extends Factory<GlobalSessionManager> { private class Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends GlobalSessionManager implements Proxy<GlobalSessionManager> {
    private final ProxyHelper<GlobalSessionManager> proxyHelper = new ProxyHelperImpl<GlobalSessionManager>("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final GlobalSessionManager instance) {

    }

    public GlobalSessionManager asBeanType() {
      return this;
    }

    public void setInstance(final GlobalSessionManager instance) {
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

    @Override public void newSession(Metadata metadata, Class sessionType, Consumer sessionConsumer) {
      if (proxyHelper != null) {
        final GlobalSessionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.newSession(metadata, sessionType, sessionConsumer);
      } else {
        super.newSession(metadata, sessionType, sessionConsumer);
      }
    }

    @Override public void open(ClientSession session) {
      if (proxyHelper != null) {
        final GlobalSessionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.open(session);
      } else {
        super.open(session);
      }
    }

    @Override public void destroy(ClientSession session) {
      if (proxyHelper != null) {
        final GlobalSessionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy(session);
      } else {
        super.destroy(session);
      }
    }

    @Override public ClientSession getCurrentSession() {
      if (proxyHelper != null) {
        final GlobalSessionManager proxiedInstance = proxyHelper.getInstance(this);
        final ClientSession retVal = proxiedInstance.getCurrentSession();
        return retVal;
      } else {
        return super.getCurrentSession();
      }
    }

    @Override public void handleCommandError(CommandException ce) {
      if (proxyHelper != null) {
        final GlobalSessionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.handleCommandError(ce);
      } else {
        super.handleCommandError(ce);
      }
    }

    @Override public void handleClientError(ClientRuntimeError error) {
      if (proxyHelper != null) {
        final GlobalSessionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.handleClientError(error);
      } else {
        super.handleClientError(error);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final GlobalSessionManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GlobalSessionManager.class, "Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GlobalSessionManager.class, Object.class, SessionManager.class });
  }

  public GlobalSessionManager createInstance(final ContextManager contextManager) {
    final Event<OnSessionErrorEvent> _sessionErrorEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { OnSessionErrorEvent.class }, new Annotation[] { });
    final Event<SessionOpenedEvent> _sessionOpenedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionOpenedEvent.class }, new Annotation[] { });
    final Event<SessionDestroyedEvent> _sessionDestroyedEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionDestroyedEvent.class }, new Annotation[] { });
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ClientSession> _sessionInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ClientSession.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final GlobalSessionManager instance = new GlobalSessionManager(_definitionUtils_0, _sessionInstances_1, _sessionOpenedEvent_2, _sessionDestroyedEvent_3, _sessionErrorEvent_4);
    registerDependentScopedReference(instance, _sessionErrorEvent_4);
    registerDependentScopedReference(instance, _sessionOpenedEvent_2);
    registerDependentScopedReference(instance, _sessionDestroyedEvent_3);
    registerDependentScopedReference(instance, _sessionInstances_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<GlobalSessionManager> proxyImpl = new Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}