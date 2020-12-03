package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TemplateProvider;
import org.jboss.errai.ui.client.local.spi.TemplateRenderingCallback;
import org.jboss.errai.ui.shared.ServerTemplateProvider;

public class Type_factory__o_j_e_u_s_ServerTemplateProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ServerTemplateProvider> { private class Type_factory__o_j_e_u_s_ServerTemplateProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ServerTemplateProvider implements Proxy<ServerTemplateProvider> {
    private final ProxyHelper<ServerTemplateProvider> proxyHelper = new ProxyHelperImpl<ServerTemplateProvider>("Type_factory__o_j_e_u_s_ServerTemplateProvider__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ServerTemplateProvider instance) {

    }

    public ServerTemplateProvider asBeanType() {
      return this;
    }

    public void setInstance(final ServerTemplateProvider instance) {
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

    @Override public void provideTemplate(String url, TemplateRenderingCallback renderingCallback) {
      if (proxyHelper != null) {
        final ServerTemplateProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.provideTemplate(url, renderingCallback);
      } else {
        super.provideTemplate(url, renderingCallback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ServerTemplateProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_j_e_u_s_ServerTemplateProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ServerTemplateProvider.class, "Type_factory__o_j_e_u_s_ServerTemplateProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ServerTemplateProvider.class, Object.class, TemplateProvider.class });
  }

  public ServerTemplateProvider createInstance(final ContextManager contextManager) {
    final ServerTemplateProvider instance = new ServerTemplateProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ServerTemplateProvider> proxyImpl = new Type_factory__o_j_e_u_s_ServerTemplateProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}