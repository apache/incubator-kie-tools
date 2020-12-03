package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.kogito.webapp.base.shared.PreferenceScopeTypesMock;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

public class Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeTypesMock__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeTypesMock> { private class Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeTypesMock__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PreferenceScopeTypesMock implements Proxy<PreferenceScopeTypesMock> {
    private final ProxyHelper<PreferenceScopeTypesMock> proxyHelper = new ProxyHelperImpl<PreferenceScopeTypesMock>("Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeTypesMock__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PreferenceScopeTypesMock instance) {

    }

    public PreferenceScopeTypesMock asBeanType() {
      return this;
    }

    public void setInstance(final PreferenceScopeTypesMock instance) {
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

    @Override public boolean typeRequiresKey(String type) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final PreferenceScopeTypesMock proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.typeRequiresKey(type);
        return retVal;
      } else {
        return super.typeRequiresKey(type);
      }
    }

    @Override public String getDefaultKeyFor(String type) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final PreferenceScopeTypesMock proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDefaultKeyFor(type);
        return retVal;
      } else {
        return super.getDefaultKeyFor(type);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PreferenceScopeTypesMock proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeTypesMock__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferenceScopeTypesMock.class, "Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeTypesMock__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceScopeTypesMock.class, Object.class, PreferenceScopeTypes.class });
  }

  public PreferenceScopeTypesMock createInstance(final ContextManager contextManager) {
    final PreferenceScopeTypesMock instance = new PreferenceScopeTypesMock();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PreferenceScopeTypesMock> proxyImpl = new Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeTypesMock__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}