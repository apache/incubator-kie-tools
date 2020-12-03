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
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetRuleAdapterImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.AbstractDefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.Adapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PriorityAdapter;
import org.kie.workbench.common.stunner.core.rule.RuleSet;

public class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetRuleAdapterImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetRuleAdapterImpl> { private class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetRuleAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDefinitionSetRuleAdapterImpl implements Proxy<DMNDefinitionSetRuleAdapterImpl> {
    private final ProxyHelper<DMNDefinitionSetRuleAdapterImpl> proxyHelper = new ProxyHelperImpl<DMNDefinitionSetRuleAdapterImpl>("Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetRuleAdapterImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNDefinitionSetRuleAdapterImpl instance) {

    }

    public DMNDefinitionSetRuleAdapterImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNDefinitionSetRuleAdapterImpl instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final DMNDefinitionSetRuleAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public RuleSet getRuleSet(DMNDefinitionSet pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetRuleAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final RuleSet retVal = proxiedInstance.getRuleSet(pojo);
        return retVal;
      } else {
        return super.getRuleSet(pojo);
      }
    }

    @Override public boolean accepts(Class type) {
      if (proxyHelper != null) {
        final DMNDefinitionSetRuleAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(type);
        return retVal;
      } else {
        return super.accepts(type);
      }
    }

    @Override public int getPriority() {
      if (proxyHelper != null) {
        final DMNDefinitionSetRuleAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getPriority();
        return retVal;
      } else {
        return super.getPriority();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDefinitionSetRuleAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetRuleAdapterImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDefinitionSetRuleAdapterImpl.class, "Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetRuleAdapterImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDefinitionSetRuleAdapterImpl.class, AbstractDefinitionSetRuleAdapter.class, Object.class, DefinitionSetRuleAdapter.class, PriorityAdapter.class, Adapter.class });
  }

  public DMNDefinitionSetRuleAdapterImpl createInstance(final ContextManager contextManager) {
    final DMNDefinitionSetRuleAdapterImpl instance = new DMNDefinitionSetRuleAdapterImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNDefinitionSetRuleAdapterImpl instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDefinitionSetRuleAdapterImpl> proxyImpl = new Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetRuleAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}