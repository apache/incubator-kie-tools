package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.rules.NoInputNodesInImportedDecisionRule;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;

public class Type_factory__o_k_w_c_d_a_r_NoInputNodesInImportedDecisionRule__quals__j_e_i_Any_j_e_i_Default extends Factory<NoInputNodesInImportedDecisionRule> { private class Type_factory__o_k_w_c_d_a_r_NoInputNodesInImportedDecisionRule__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NoInputNodesInImportedDecisionRule implements Proxy<NoInputNodesInImportedDecisionRule> {
    private final ProxyHelper<NoInputNodesInImportedDecisionRule> proxyHelper = new ProxyHelperImpl<NoInputNodesInImportedDecisionRule>("Type_factory__o_k_w_c_d_a_r_NoInputNodesInImportedDecisionRule__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final NoInputNodesInImportedDecisionRule instance) {

    }

    public NoInputNodesInImportedDecisionRule asBeanType() {
      return this;
    }

    public void setInstance(final NoInputNodesInImportedDecisionRule instance) {
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

    @Override public Class getExtensionType() {
      if (proxyHelper != null) {
        final NoInputNodesInImportedDecisionRule proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getExtensionType();
        return retVal;
      } else {
        return super.getExtensionType();
      }
    }

    @Override public Class getContextType() {
      if (proxyHelper != null) {
        final NoInputNodesInImportedDecisionRule proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getContextType();
        return retVal;
      } else {
        return super.getContextType();
      }
    }

    @Override public boolean accepts(RuleExtension rule, GraphConnectionContext context) {
      if (proxyHelper != null) {
        final NoInputNodesInImportedDecisionRule proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(rule, context);
        return retVal;
      } else {
        return super.accepts(rule, context);
      }
    }

    @Override public RuleViolations evaluate(RuleExtension rule, GraphConnectionContext context) {
      if (proxyHelper != null) {
        final NoInputNodesInImportedDecisionRule proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(rule, context);
        return retVal;
      } else {
        return super.evaluate(rule, context);
      }
    }

    @Override public Class getRuleType() {
      if (proxyHelper != null) {
        final NoInputNodesInImportedDecisionRule proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getRuleType();
        return retVal;
      } else {
        return super.getRuleType();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NoInputNodesInImportedDecisionRule proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_r_NoInputNodesInImportedDecisionRule__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NoInputNodesInImportedDecisionRule.class, "Type_factory__o_k_w_c_d_a_r_NoInputNodesInImportedDecisionRule__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NoInputNodesInImportedDecisionRule.class, RuleExtensionHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public NoInputNodesInImportedDecisionRule createInstance(final ContextManager contextManager) {
    final NoInputNodesInImportedDecisionRule instance = new NoInputNodesInImportedDecisionRule();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NoInputNodesInImportedDecisionRule> proxyImpl = new Type_factory__o_k_w_c_d_a_r_NoInputNodesInImportedDecisionRule__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}