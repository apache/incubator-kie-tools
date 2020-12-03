package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;

public class Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default extends Factory<ToolboxDomainLookups> { private class Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ToolboxDomainLookups implements Proxy<ToolboxDomainLookups> {
    private final ProxyHelper<ToolboxDomainLookups> proxyHelper = new ProxyHelperImpl<ToolboxDomainLookups>("Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ToolboxDomainLookups instance) {

    }

    public ToolboxDomainLookups asBeanType() {
      return this;
    }

    public void setInstance(final ToolboxDomainLookups instance) {
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

    @Override public CommonDomainLookups get(String definitionSetId) {
      if (proxyHelper != null) {
        final ToolboxDomainLookups proxiedInstance = proxyHelper.getInstance(this);
        final CommonDomainLookups retVal = proxiedInstance.get(definitionSetId);
        return retVal;
      } else {
        return super.get(definitionSetId);
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final ToolboxDomainLookups proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ToolboxDomainLookups proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ToolboxDomainLookups.class, "Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ToolboxDomainLookups.class, Object.class });
  }

  public ToolboxDomainLookups createInstance(final ContextManager contextManager) {
    final ManagedInstance<CommonDomainLookups> _domainLookupInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CommonDomainLookups.class }, new Annotation[] { });
    final ToolboxDomainLookups instance = new ToolboxDomainLookups(_domainLookupInstances_0);
    registerDependentScopedReference(instance, _domainLookupInstances_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ToolboxDomainLookups) instance, contextManager);
  }

  public void destroyInstanceHelper(final ToolboxDomainLookups instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ToolboxDomainLookups> proxyImpl = new Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}