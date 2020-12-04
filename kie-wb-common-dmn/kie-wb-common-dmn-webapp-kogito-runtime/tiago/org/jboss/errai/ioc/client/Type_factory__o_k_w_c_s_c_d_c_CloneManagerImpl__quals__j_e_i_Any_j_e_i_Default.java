package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.commands.clone.DMNDeepCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManagerImpl;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;
import org.kie.workbench.common.stunner.core.definition.clone.DefaultCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.IDeepCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.NoneCloneProcess;

public class Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CloneManagerImpl> { private class Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CloneManagerImpl implements Proxy<CloneManagerImpl> {
    private final ProxyHelper<CloneManagerImpl> proxyHelper = new ProxyHelperImpl<CloneManagerImpl>("Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final CloneManagerImpl instance) {

    }

    public CloneManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final CloneManagerImpl instance) {
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

    @Override public Object clone(Object source, ClonePolicy policy) {
      if (proxyHelper != null) {
        final CloneManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.clone(source, policy);
        return retVal;
      } else {
        return super.clone(source, policy);
      }
    }

    @Override public Object clone(Object source, Object target, ClonePolicy policy) {
      if (proxyHelper != null) {
        final CloneManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.clone(source, target, policy);
        return retVal;
      } else {
        return super.clone(source, target, policy);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CloneManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CloneManagerImpl.class, "Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CloneManagerImpl.class, Object.class, CloneManager.class });
  }

  public CloneManagerImpl createInstance(final ContextManager contextManager) {
    final DefaultCloneProcess _defaultCloneProcess_1 = (DefaultCloneProcess) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_c_DefaultCloneProcess__quals__j_e_i_Any_j_e_i_Default");
    final NoneCloneProcess _noneCloneProcess_2 = (NoneCloneProcess) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_c_NoneCloneProcess__quals__j_e_i_Any_j_e_i_Default");
    final IDeepCloneProcess _deepCloneProcess_0 = (DMNDeepCloneProcess) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_c_DMNDeepCloneProcess__quals__j_e_i_Any_j_e_i_Default");
    final CloneManagerImpl instance = new CloneManagerImpl(_deepCloneProcess_0, _defaultCloneProcess_1, _noneCloneProcess_2);
    registerDependentScopedReference(instance, _noneCloneProcess_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CloneManagerImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}