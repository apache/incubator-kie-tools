package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ConnectorCardinalityEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.EdgeCardinalityEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;

public class Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorCardinalityEvaluationHandler> { private class Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ConnectorCardinalityEvaluationHandler implements Proxy<ConnectorCardinalityEvaluationHandler> {
    private final ProxyHelper<ConnectorCardinalityEvaluationHandler> proxyHelper = new ProxyHelperImpl<ConnectorCardinalityEvaluationHandler>("Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final ConnectorCardinalityEvaluationHandler instance) {

    }

    public ConnectorCardinalityEvaluationHandler asBeanType() {
      return this;
    }

    public void setInstance(final ConnectorCardinalityEvaluationHandler instance) {
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
        final ConnectorCardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getRuleType();
        return retVal;
      } else {
        return super.getRuleType();
      }
    }

    @Override public Class getContextType() {
      if (proxyHelper != null) {
        final ConnectorCardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getContextType();
        return retVal;
      } else {
        return super.getContextType();
      }
    }

    @Override public boolean accepts(EdgeOccurrences rule, ConnectorCardinalityContext context) {
      if (proxyHelper != null) {
        final ConnectorCardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(rule, context);
        return retVal;
      } else {
        return super.accepts(rule, context);
      }
    }

    @Override public RuleViolations evaluate(EdgeOccurrences rule, ConnectorCardinalityContext context) {
      if (proxyHelper != null) {
        final ConnectorCardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(rule, context);
        return retVal;
      } else {
        return super.evaluate(rule, context);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ConnectorCardinalityEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConnectorCardinalityEvaluationHandler.class, "Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConnectorCardinalityEvaluationHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public ConnectorCardinalityEvaluationHandler createInstance(final ContextManager contextManager) {
    final EdgeCardinalityEvaluationHandler _edgeCardinalityEvaluationHandler_1 = (EdgeCardinalityEvaluationHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_r_h_i_EdgeCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final ConnectorCardinalityEvaluationHandler instance = new ConnectorCardinalityEvaluationHandler(_definitionManager_0, _edgeCardinalityEvaluationHandler_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_r_h_i_ConnectorCardinalityEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.rule.handler.impl.ConnectorCardinalityEvaluationHandler an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.rule.handler.impl.ConnectorCardinalityEvaluationHandler ([org.kie.workbench.common.stunner.core.api.DefinitionManager, org.kie.workbench.common.stunner.core.rule.handler.impl.EdgeCardinalityEvaluationHandler])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ConnectorCardinalityEvaluationHandler> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}