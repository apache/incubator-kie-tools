package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.preferences.DefaultPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryHolder;

public class Type_factory__o_k_w_c_s_c_c_p_DefaultPreferencesRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPreferencesRegistry> { private class Type_factory__o_k_w_c_s_c_c_p_DefaultPreferencesRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultPreferencesRegistry implements Proxy<DefaultPreferencesRegistry> {
    private final ProxyHelper<DefaultPreferencesRegistry> proxyHelper = new ProxyHelperImpl<DefaultPreferencesRegistry>("Type_factory__o_k_w_c_s_c_c_p_DefaultPreferencesRegistry__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultPreferencesRegistry instance) {

    }

    public DefaultPreferencesRegistry asBeanType() {
      return this;
    }

    public void setInstance(final DefaultPreferencesRegistry instance) {
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

    @Override public Object get(Class preferenceType) {
      if (proxyHelper != null) {
        final DefaultPreferencesRegistry proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.get(preferenceType);
        return retVal;
      } else {
        return super.get(preferenceType);
      }
    }

    @Override public void set(Object preferences, Class preferenceType) {
      if (proxyHelper != null) {
        final DefaultPreferencesRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.set(preferences, preferenceType);
      } else {
        super.set(preferences, preferenceType);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultPreferencesRegistry proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_p_DefaultPreferencesRegistry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultPreferencesRegistry.class, "Type_factory__o_k_w_c_s_c_c_p_DefaultPreferencesRegistry__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultPreferencesRegistry.class, StunnerPreferencesRegistryHolder.class, Object.class, StunnerPreferencesRegistry.class });
  }

  public DefaultPreferencesRegistry createInstance(final ContextManager contextManager) {
    final DefaultPreferencesRegistry instance = new DefaultPreferencesRegistry();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultPreferencesRegistry> proxyImpl = new Type_factory__o_k_w_c_s_c_c_p_DefaultPreferencesRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}