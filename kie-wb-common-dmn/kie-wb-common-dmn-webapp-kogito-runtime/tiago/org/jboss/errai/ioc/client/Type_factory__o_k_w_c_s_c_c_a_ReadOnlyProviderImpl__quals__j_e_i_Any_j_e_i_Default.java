package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.ReadOnlyProviderImpl;

public class Type_factory__o_k_w_c_s_c_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ReadOnlyProviderImpl> { private class Type_factory__o_k_w_c_s_c_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ReadOnlyProviderImpl implements Proxy<ReadOnlyProviderImpl> {
    private final ProxyHelper<ReadOnlyProviderImpl> proxyHelper = new ProxyHelperImpl<ReadOnlyProviderImpl>("Type_factory__o_k_w_c_s_c_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ReadOnlyProviderImpl instance) {

    }

    public ReadOnlyProviderImpl asBeanType() {
      return this;
    }

    public void setInstance(final ReadOnlyProviderImpl instance) {
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

    @Override public boolean isReadOnlyDiagram() {
      if (proxyHelper != null) {
        final ReadOnlyProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isReadOnlyDiagram();
        return retVal;
      } else {
        return super.isReadOnlyDiagram();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ReadOnlyProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ReadOnlyProviderImpl.class, "Type_factory__o_k_w_c_s_c_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ReadOnlyProviderImpl.class, Object.class, ReadOnlyProvider.class });
  }

  public ReadOnlyProviderImpl createInstance(final ContextManager contextManager) {
    final ReadOnlyProviderImpl instance = new ReadOnlyProviderImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ReadOnlyProviderImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}