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
import org.kie.workbench.common.stunner.core.definition.clone.DeepCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.IDeepCloneProcess;
import org.kie.workbench.common.stunner.core.util.ClassUtils;

public class Type_factory__o_k_w_c_s_c_d_c_DeepCloneProcess__quals__j_e_i_Any_j_e_i_Default extends Factory<DeepCloneProcess> { private class Type_factory__o_k_w_c_s_c_d_c_DeepCloneProcess__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DeepCloneProcess implements Proxy<DeepCloneProcess> {
    private final ProxyHelper<DeepCloneProcess> proxyHelper = new ProxyHelperImpl<DeepCloneProcess>("Type_factory__o_k_w_c_s_c_d_c_DeepCloneProcess__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DeepCloneProcess instance) {

    }

    public DeepCloneProcess asBeanType() {
      return this;
    }

    public void setInstance(final DeepCloneProcess instance) {
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
        final DeepCloneProcess proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.clone(source, target);
        return retVal;
      } else {
        return super.clone(source, target);
      }
    }

    @Override public Object clone(Object source) {
      if (proxyHelper != null) {
        final DeepCloneProcess proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.clone(source);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DeepCloneProcess proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_d_c_DeepCloneProcess__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DeepCloneProcess.class, "Type_factory__o_k_w_c_s_c_d_c_DeepCloneProcess__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DeepCloneProcess.class, AbstractCloneProcess.class, Object.class, CloneProcess.class, IDeepCloneProcess.class });
  }

  public DeepCloneProcess createInstance(final ContextManager contextManager) {
    final AdapterManager _adapterManager_1 = (AdapterManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClassUtils _classUtils_2 = (ClassUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_ClassUtils__quals__j_e_i_Any_j_e_i_Default");
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final DeepCloneProcess instance = new DeepCloneProcess(_factoryManager_0, _adapterManager_1, _classUtils_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DeepCloneProcess> proxyImpl = new Type_factory__o_k_w_c_s_c_d_c_DeepCloneProcess__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}