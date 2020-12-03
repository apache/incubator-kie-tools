package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.validation.DiagramElementNameProviderImpl;
import org.kie.workbench.common.stunner.core.validation.DiagramElementNameProvider;

public class Type_factory__o_k_w_c_d_c_v_DiagramElementNameProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramElementNameProviderImpl> { private class Type_factory__o_k_w_c_d_c_v_DiagramElementNameProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DiagramElementNameProviderImpl implements Proxy<DiagramElementNameProviderImpl> {
    private final ProxyHelper<DiagramElementNameProviderImpl> proxyHelper = new ProxyHelperImpl<DiagramElementNameProviderImpl>("Type_factory__o_k_w_c_d_c_v_DiagramElementNameProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DiagramElementNameProviderImpl instance) {

    }

    public DiagramElementNameProviderImpl asBeanType() {
      return this;
    }

    public void setInstance(final DiagramElementNameProviderImpl instance) {
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

    @Override public String getDefinitionSetId() {
      if (proxyHelper != null) {
        final DiagramElementNameProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDefinitionSetId();
        return retVal;
      } else {
        return super.getDefinitionSetId();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DiagramElementNameProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_v_DiagramElementNameProviderImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramElementNameProviderImpl.class, "Type_factory__o_k_w_c_d_c_v_DiagramElementNameProviderImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramElementNameProviderImpl.class, Object.class, DiagramElementNameProvider.class });
  }

  public DiagramElementNameProviderImpl createInstance(final ContextManager contextManager) {
    final DiagramElementNameProviderImpl instance = new DiagramElementNameProviderImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DiagramElementNameProviderImpl> proxyImpl = new Type_factory__o_k_w_c_d_c_v_DiagramElementNameProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}