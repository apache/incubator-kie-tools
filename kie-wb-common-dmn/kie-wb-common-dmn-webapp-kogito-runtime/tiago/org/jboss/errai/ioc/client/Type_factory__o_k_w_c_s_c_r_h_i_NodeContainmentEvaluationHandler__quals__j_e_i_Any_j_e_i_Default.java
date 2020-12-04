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
import org.kie.workbench.common.stunner.core.rule.context.NodeContainmentContext;
import org.kie.workbench.common.stunner.core.rule.handler.impl.ContainmentEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.handler.impl.NodeContainmentEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;

public class Type_factory__o_k_w_c_s_c_r_h_i_NodeContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeContainmentEvaluationHandler> { private class Type_factory__o_k_w_c_s_c_r_h_i_NodeContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NodeContainmentEvaluationHandler implements Proxy<NodeContainmentEvaluationHandler> {
    private final ProxyHelper<NodeContainmentEvaluationHandler> proxyHelper = new ProxyHelperImpl<NodeContainmentEvaluationHandler>("Type_factory__o_k_w_c_s_c_r_h_i_NodeContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final NodeContainmentEvaluationHandler instance) {

    }

    public NodeContainmentEvaluationHandler asBeanType() {
      return this;
    }

    public void setInstance(final NodeContainmentEvaluationHandler instance) {
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
        final NodeContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getRuleType();
        return retVal;
      } else {
        return super.getRuleType();
      }
    }

    @Override public Class getContextType() {
      if (proxyHelper != null) {
        final NodeContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getContextType();
        return retVal;
      } else {
        return super.getContextType();
      }
    }

    @Override public boolean accepts(CanContain rule, NodeContainmentContext context) {
      if (proxyHelper != null) {
        final NodeContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(rule, context);
        return retVal;
      } else {
        return super.accepts(rule, context);
      }
    }

    @Override public RuleViolations evaluate(CanContain rule, NodeContainmentContext context) {
      if (proxyHelper != null) {
        final NodeContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(rule, context);
        return retVal;
      } else {
        return super.evaluate(rule, context);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NodeContainmentEvaluationHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_r_h_i_NodeContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NodeContainmentEvaluationHandler.class, "Type_factory__o_k_w_c_s_c_r_h_i_NodeContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NodeContainmentEvaluationHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public NodeContainmentEvaluationHandler createInstance(final ContextManager contextManager) {
    final ContainmentEvaluationHandler _containmentHandler_1 = (ContainmentEvaluationHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_r_h_i_ContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final NodeContainmentEvaluationHandler instance = new NodeContainmentEvaluationHandler(_definitionManager_0, _containmentHandler_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NodeContainmentEvaluationHandler> proxyImpl = new Type_factory__o_k_w_c_s_c_r_h_i_NodeContainmentEvaluationHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}