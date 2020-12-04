package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.rule.ClientRuleManager;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;
import org.kie.workbench.common.stunner.core.rule.CachedRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

public class Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientRuleManager> { private class Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientRuleManager implements Proxy<ClientRuleManager> {
    private final ProxyHelper<ClientRuleManager> proxyHelper = new ProxyHelperImpl<ClientRuleManager>("Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientRuleManager instance) {

    }

    public ClientRuleManager asBeanType() {
      return this;
    }

    public void setInstance(final ClientRuleManager instance) {
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
        final ClientRuleManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public RuleHandlerRegistry registry() {
      if (proxyHelper != null) {
        final ClientRuleManager proxiedInstance = proxyHelper.getInstance(this);
        final RuleHandlerRegistry retVal = proxiedInstance.registry();
        return retVal;
      } else {
        return super.registry();
      }
    }

    @Override public RuleViolations evaluate(RuleSet ruleSet, RuleEvaluationContext context) {
      if (proxyHelper != null) {
        final ClientRuleManager proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(ruleSet, context);
        return retVal;
      } else {
        return super.evaluate(ruleSet, context);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientRuleManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientRuleManager.class, "Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientRuleManager.class, Object.class, RuleManager.class });
  }

  public ClientRuleManager createInstance(final ContextManager contextManager) {
    final CachedRuleManager _ruleManager_0 = (CachedRuleManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_r_CachedRuleManager__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<RuleEvaluationHandler> _ruleEvaluationHandlerInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { RuleEvaluationHandler.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ClientRuleManager instance = new ClientRuleManager(_ruleManager_0, _ruleEvaluationHandlerInstances_1);
    registerDependentScopedReference(instance, _ruleManager_0);
    registerDependentScopedReference(instance, _ruleEvaluationHandlerInstances_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientRuleManager instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientRuleManager> proxyImpl = new Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}