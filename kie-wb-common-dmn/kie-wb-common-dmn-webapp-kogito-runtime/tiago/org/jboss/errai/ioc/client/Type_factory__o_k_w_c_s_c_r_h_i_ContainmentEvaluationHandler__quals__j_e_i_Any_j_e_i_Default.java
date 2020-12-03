package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.ContainmentContext;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ContainmentEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;

public class Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ContainmentEvaluationHandler> { private class Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ContainmentEvaluationHandler implements Proxy<ContainmentEvaluationHandler> {
    private final ProxyHelper<ContainmentEvaluationHandler> proxyHelper = new ProxyHelperImpl<ContainmentEvaluationHandler>("Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ContainmentEvaluationHandler instance) {

    }

    public ContainmentEvaluationHandler asBeanType() {
      return this;
    }

    public void setInstance(final ContainmentEvaluationHandler instance) {
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

    @Override public Class getRuleType() {
      if (proxyHelper != null) {
        final ContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getRuleType();
        return retVal;
      } else {
        return super.getRuleType();
      }
    }

    @Override public Class getContextType() {
      if (proxyHelper != null) {
        final ContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getContextType();
        return retVal;
      } else {
        return super.getContextType();
      }
    }

    @Override public boolean accepts(CanContain rule, ContainmentContext context) {
      if (proxyHelper != null) {
        final ContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(rule, context);
        return retVal;
      } else {
        return super.accepts(rule, context);
      }
    }

    @Override public RuleViolations evaluate(CanContain rule, ContainmentContext context) {
      if (proxyHelper != null) {
        final ContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(rule, context);
        return retVal;
      } else {
        return super.evaluate(rule, context);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ContainmentEvaluationHandler.class, "Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ContainmentEvaluationHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public ContainmentEvaluationHandler createInstance(final ContextManager contextManager) {
    final ContainmentEvaluationHandler instance = new ContainmentEvaluationHandler();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ContainmentEvaluationHandler> proxyImpl = new Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}