package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.JavaScriptObject;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.jsbridge.client.loading.AppFormerComponentsRegistry;

public class Type_factory__o_u_j_c_l_AppFormerComponentsRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<AppFormerComponentsRegistry> { private class Type_factory__o_u_j_c_l_AppFormerComponentsRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AppFormerComponentsRegistry implements Proxy<AppFormerComponentsRegistry> {
    private final ProxyHelper<AppFormerComponentsRegistry> proxyHelper = new ProxyHelperImpl<AppFormerComponentsRegistry>("Type_factory__o_u_j_c_l_AppFormerComponentsRegistry__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AppFormerComponentsRegistry instance) {

    }

    public AppFormerComponentsRegistry asBeanType() {
      return this;
    }

    public void setInstance(final AppFormerComponentsRegistry instance) {
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

    @Override public String[] keys() {
      if (proxyHelper != null) {
        final AppFormerComponentsRegistry proxiedInstance = proxyHelper.getInstance(this);
        final String[] retVal = proxiedInstance.keys();
        return retVal;
      } else {
        return super.keys();
      }
    }

    @Override public JavaScriptObject get(String key) {
      if (proxyHelper != null) {
        final AppFormerComponentsRegistry proxiedInstance = proxyHelper.getInstance(this);
        final JavaScriptObject retVal = proxiedInstance.get(key);
        return retVal;
      } else {
        return super.get(key);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AppFormerComponentsRegistry proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_j_c_l_AppFormerComponentsRegistry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AppFormerComponentsRegistry.class, "Type_factory__o_u_j_c_l_AppFormerComponentsRegistry__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppFormerComponentsRegistry.class, Object.class });
  }

  public AppFormerComponentsRegistry createInstance(final ContextManager contextManager) {
    final AppFormerComponentsRegistry instance = new AppFormerComponentsRegistry();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AppFormerComponentsRegistry> proxyImpl = new Type_factory__o_u_j_c_l_AppFormerComponentsRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}