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
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.handler.impl.CardinalityEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;

public class Type_factory__o_k_w_c_s_c_r_h_i_CardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<CardinalityEvaluationHandler> { private class Type_factory__o_k_w_c_s_c_r_h_i_CardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CardinalityEvaluationHandler implements Proxy<CardinalityEvaluationHandler> {
    private final ProxyHelper<CardinalityEvaluationHandler> proxyHelper = new ProxyHelperImpl<CardinalityEvaluationHandler>("Type_factory__o_k_w_c_s_c_r_h_i_CardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final CardinalityEvaluationHandler instance) {

    }

    public CardinalityEvaluationHandler asBeanType() {
      return this;
    }

    public void setInstance(final CardinalityEvaluationHandler instance) {
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
        final CardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getRuleType();
        return retVal;
      } else {
        return super.getRuleType();
      }
    }

    @Override public Class getContextType() {
      if (proxyHelper != null) {
        final CardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getContextType();
        return retVal;
      } else {
        return super.getContextType();
      }
    }

    @Override public boolean accepts(Occurrences rule, CardinalityContext context) {
      if (proxyHelper != null) {
        final CardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(rule, context);
        return retVal;
      } else {
        return super.accepts(rule, context);
      }
    }

    @Override public RuleViolations evaluate(Occurrences rule, CardinalityContext context) {
      if (proxyHelper != null) {
        final CardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(rule, context);
        return retVal;
      } else {
        return super.evaluate(rule, context);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_r_h_i_CardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CardinalityEvaluationHandler.class, "Type_factory__o_k_w_c_s_c_r_h_i_CardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CardinalityEvaluationHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public CardinalityEvaluationHandler createInstance(final ContextManager contextManager) {
    final CardinalityEvaluationHandler instance = new CardinalityEvaluationHandler();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CardinalityEvaluationHandler> proxyImpl = new Type_factory__o_k_w_c_s_c_r_h_i_CardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}