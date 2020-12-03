package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManagerImpl;
import org.kie.workbench.common.stunner.core.definition.clone.AbstractCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.CloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.DefaultCloneProcess;

public class Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultCloneProcess> { private class Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefaultCloneProcess implements Proxy<DefaultCloneProcess> {
    private final ProxyHelper<DefaultCloneProcess> proxyHelper = new ProxyHelperImpl<DefaultCloneProcess>("Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefaultCloneProcess instance) {

    }

    public DefaultCloneProcess asBeanType() {
      return this;
    }

    public void setInstance(final DefaultCloneProcess instance) {
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

    @Override public Object clone(Object source, Object target) {
      if (proxyHelper != null) {
        final DefaultCloneProcess proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.clone(source, target);
        return retVal;
      } else {
        return super.clone(source, target);
      }
    }

    @Override public Object clone(Object source) {
      if (proxyHelper != null) {
        final DefaultCloneProcess proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.clone(source);
        return retVal;
      } else {
        return super.clone(source);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultCloneProcess proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultCloneProcess.class, "Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultCloneProcess.class, AbstractCloneProcess.class, Object.class, CloneProcess.class });
  }

  public DefaultCloneProcess createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final AdapterManager _adapterManager_1 = (AdapterManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefaultCloneProcess instance = new DefaultCloneProcess(_factoryManager_0, _adapterManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultCloneProcess> proxyImpl = new Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}