package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.guvnor.messageconsole.whitelist.MessageConsoleWhiteList;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.services.shared.messageconsole.MessageConsoleWhiteListImpl;

public class Type_factory__o_k_w_c_s_s_m_MessageConsoleWhiteListImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MessageConsoleWhiteListImpl> { private class Type_factory__o_k_w_c_s_s_m_MessageConsoleWhiteListImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends MessageConsoleWhiteListImpl implements Proxy<MessageConsoleWhiteListImpl> {
    private final ProxyHelper<MessageConsoleWhiteListImpl> proxyHelper = new ProxyHelperImpl<MessageConsoleWhiteListImpl>("Type_factory__o_k_w_c_s_s_m_MessageConsoleWhiteListImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final MessageConsoleWhiteListImpl instance) {

    }

    public MessageConsoleWhiteListImpl asBeanType() {
      return this;
    }

    public void setInstance(final MessageConsoleWhiteListImpl instance) {
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

    @Override public boolean contains(String perspective) {
      if (proxyHelper != null) {
        final MessageConsoleWhiteListImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.contains(perspective);
        return retVal;
      } else {
        return super.contains(perspective);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final MessageConsoleWhiteListImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_s_m_MessageConsoleWhiteListImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MessageConsoleWhiteListImpl.class, "Type_factory__o_k_w_c_s_s_m_MessageConsoleWhiteListImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MessageConsoleWhiteListImpl.class, Object.class, MessageConsoleWhiteList.class });
  }

  public MessageConsoleWhiteListImpl createInstance(final ContextManager contextManager) {
    final MessageConsoleWhiteListImpl instance = new MessageConsoleWhiteListImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<MessageConsoleWhiteListImpl> proxyImpl = new Type_factory__o_k_w_c_s_s_m_MessageConsoleWhiteListImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}