package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.rpc.SessionInfo;

public class Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionInfo> { private class Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_DefaultProxyImpl implements Proxy<SessionInfo>, SessionInfo {
    private final ProxyHelper<SessionInfo> proxyHelper = new ProxyHelperImpl<SessionInfo>("Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final SessionInfo instance) {

    }

    public SessionInfo asBeanType() {
      return this;
    }

    public void setInstance(final SessionInfo instance) {
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

    @Override public String getId() {
      if (proxyHelper != null) {
        final SessionInfo proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getId();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public User getIdentity() {
      if (proxyHelper != null) {
        final SessionInfo proxiedInstance = proxyHelper.getInstance(this);
        final User retVal = proxiedInstance.getIdentity();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final SessionInfo proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }
  }
  public Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionInfo.class, "Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionInfo.class });
  }

  public SessionInfo createInstance(final ContextManager contextManager) {
    Workbench producerInstance = contextManager.getInstance("Type_factory__o_u_c_w_Workbench__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final SessionInfo instance = Workbench_currentSession(producerInstance);
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<SessionInfo> proxyImpl = new Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static SessionInfo Workbench_currentSession(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::currentSession()();
  }-*/;
}