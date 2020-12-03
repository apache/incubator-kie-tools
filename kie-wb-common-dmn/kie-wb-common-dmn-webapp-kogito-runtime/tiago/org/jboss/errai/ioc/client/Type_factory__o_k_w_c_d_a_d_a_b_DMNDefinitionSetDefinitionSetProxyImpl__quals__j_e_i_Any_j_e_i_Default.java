package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetDefinitionSetProxyImpl;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetProxy;

public class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetProxyImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetDefinitionSetProxyImpl> { private class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetProxyImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDefinitionSetDefinitionSetProxyImpl implements Proxy<DMNDefinitionSetDefinitionSetProxyImpl> {
    private final ProxyHelper<DMNDefinitionSetDefinitionSetProxyImpl> proxyHelper = new ProxyHelperImpl<DMNDefinitionSetDefinitionSetProxyImpl>("Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetProxyImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNDefinitionSetDefinitionSetProxyImpl instance) {

    }

    public DMNDefinitionSetDefinitionSetProxyImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNDefinitionSetDefinitionSetProxyImpl instance) {
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

    @Override public DMNDefinitionSet getDefinitionSet() {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetProxyImpl proxiedInstance = proxyHelper.getInstance(this);
        final DMNDefinitionSet retVal = proxiedInstance.getDefinitionSet();
        return retVal;
      } else {
        return super.getDefinitionSet();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetProxyImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetProxyImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDefinitionSetDefinitionSetProxyImpl.class, "Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetProxyImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDefinitionSetDefinitionSetProxyImpl.class, Object.class, DefinitionSetProxy.class });
  }

  public DMNDefinitionSetDefinitionSetProxyImpl createInstance(final ContextManager contextManager) {
    final DMNDefinitionSetDefinitionSetProxyImpl instance = new DMNDefinitionSetDefinitionSetProxyImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDefinitionSetDefinitionSetProxyImpl> proxyImpl = new Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetProxyImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}