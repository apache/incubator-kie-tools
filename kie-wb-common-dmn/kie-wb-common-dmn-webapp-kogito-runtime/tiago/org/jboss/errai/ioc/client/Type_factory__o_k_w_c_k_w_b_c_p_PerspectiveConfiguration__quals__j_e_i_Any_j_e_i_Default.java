package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.kogito.webapp.base.client.perspectives.PerspectiveConfiguration;

public class Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveConfiguration> { private class Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PerspectiveConfiguration implements Proxy<PerspectiveConfiguration> {
    private final ProxyHelper<PerspectiveConfiguration> proxyHelper = new ProxyHelperImpl<PerspectiveConfiguration>("Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PerspectiveConfiguration instance) {

    }

    public PerspectiveConfiguration asBeanType() {
      return this;
    }

    public void setInstance(final PerspectiveConfiguration instance) {
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

    @Override public Class getPerspectivePanelType() {
      if (proxyHelper != null) {
        final PerspectiveConfiguration proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getPerspectivePanelType();
        return retVal;
      } else {
        return super.getPerspectivePanelType();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PerspectiveConfiguration proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PerspectiveConfiguration.class, "Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PerspectiveConfiguration.class, Object.class });
  }

  public PerspectiveConfiguration createInstance(final ContextManager contextManager) {
    final PerspectiveConfiguration instance = new PerspectiveConfiguration();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PerspectiveConfiguration> proxyImpl = new Type_factory__o_k_w_c_k_w_b_c_p_PerspectiveConfiguration__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}