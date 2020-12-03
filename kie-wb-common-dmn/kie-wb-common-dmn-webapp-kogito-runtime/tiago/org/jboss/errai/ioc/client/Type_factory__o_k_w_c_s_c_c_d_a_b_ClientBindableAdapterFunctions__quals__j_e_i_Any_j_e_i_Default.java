package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFunctions;

public class Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientBindableAdapterFunctions> { private class Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientBindableAdapterFunctions implements Proxy<ClientBindableAdapterFunctions> {
    private final ProxyHelper<ClientBindableAdapterFunctions> proxyHelper = new ProxyHelperImpl<ClientBindableAdapterFunctions>("Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientBindableAdapterFunctions instance) {

    }

    public ClientBindableAdapterFunctions asBeanType() {
      return this;
    }

    public void setInstance(final ClientBindableAdapterFunctions instance) {
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

    @Override public Object getValue(Object pojo, String fieldName) {
      if (proxyHelper != null) {
        final ClientBindableAdapterFunctions proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.getValue(pojo, fieldName);
        return retVal;
      } else {
        return super.getValue(pojo, fieldName);
      }
    }

    @Override public void setValue(Object pojo, String field, Object value) {
      if (proxyHelper != null) {
        final ClientBindableAdapterFunctions proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setValue(pojo, field, value);
      } else {
        super.setValue(pojo, field, value);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientBindableAdapterFunctions proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientBindableAdapterFunctions.class, "Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientBindableAdapterFunctions.class, Object.class, BindableAdapterFunctions.class });
  }

  public ClientBindableAdapterFunctions createInstance(final ContextManager contextManager) {
    final ClientBindableAdapterFunctions instance = new ClientBindableAdapterFunctions();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientBindableAdapterFunctions> proxyImpl = new Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}