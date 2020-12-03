package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.rules.SingleConnectorPerTypeGraphRule;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;

public class Type_factory__o_k_w_c_d_a_r_SingleConnectorPerTypeGraphRule__quals__j_e_i_Any_j_e_i_Default extends Factory<SingleConnectorPerTypeGraphRule> { private class Type_factory__o_k_w_c_d_a_r_SingleConnectorPerTypeGraphRule__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends SingleConnectorPerTypeGraphRule implements Proxy<SingleConnectorPerTypeGraphRule> {
    private final ProxyHelper<SingleConnectorPerTypeGraphRule> proxyHelper = new ProxyHelperImpl<SingleConnectorPerTypeGraphRule>("Type_factory__o_k_w_c_d_a_r_SingleConnectorPerTypeGraphRule__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final SingleConnectorPerTypeGraphRule instance) {

    }

    public SingleConnectorPerTypeGraphRule asBeanType() {
      return this;
    }

    public void setInstance(final SingleConnectorPerTypeGraphRule instance) {
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
        final SingleConnectorPerTypeGraphRule proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getExtensionType();
        return retVal;
      } else {
        return super.getExtensionType();
      }
    }

    @Override public Class getContextType() {
      if (proxyHelper != null) {
        final SingleConnectorPerTypeGraphRule proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getContextType();
        return retVal;
      } else {
        return super.getContextType();
      }
    }

    @Override public boolean accepts(RuleExtension rule, GraphConnectionContext context) {
      if (proxyHelper != null) {
        final SingleConnectorPerTypeGraphRule proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(rule, context);
        return retVal;
      } else {
        return super.accepts(rule, context);
      }
    }

    @Override public RuleViolations evaluate(RuleExtension rule, GraphConnectionContext context) {
      if (proxyHelper != null) {
        final SingleConnectorPerTypeGraphRule proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(rule, context);
        return retVal;
      } else {
        return super.evaluate(rule, context);
      }
    }

    @Override protected boolean isConnectionAlreadyFormed(Node source, Node target, Edge connector) {
      if (proxyHelper != null) {
        final SingleConnectorPerTypeGraphRule proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = SingleConnectorPerTypeGraphRule_isConnectionAlreadyFormed_Node_Node_Edge(proxiedInstance, source, target, connector);
        return retVal;
      } else {
        return super.isConnectionAlreadyFormed(source, target, connector);
      }
    }

    @Override public Class getRuleType() {
      if (proxyHelper != null) {
        final SingleConnectorPerTypeGraphRule proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getRuleType();
        return retVal;
      } else {
        return super.getRuleType();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final SingleConnectorPerTypeGraphRule proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_r_SingleConnectorPerTypeGraphRule__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SingleConnectorPerTypeGraphRule.class, "Type_factory__o_k_w_c_d_a_r_SingleConnectorPerTypeGraphRule__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SingleConnectorPerTypeGraphRule.class, RuleExtensionHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public SingleConnectorPerTypeGraphRule createInstance(final ContextManager contextManager) {
    final SingleConnectorPerTypeGraphRule instance = new SingleConnectorPerTypeGraphRule();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<SingleConnectorPerTypeGraphRule> proxyImpl = new Type_factory__o_k_w_c_d_a_r_SingleConnectorPerTypeGraphRule__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static boolean SingleConnectorPerTypeGraphRule_isConnectionAlreadyFormed_Node_Node_Edge(SingleConnectorPerTypeGraphRule instance, Node a0, Node a1, Edge a2) /*-{
    return instance.@org.kie.workbench.common.dmn.api.rules.SingleConnectorPerTypeGraphRule::isConnectionAlreadyFormed(Lorg/kie/workbench/common/stunner/core/graph/Node;Lorg/kie/workbench/common/stunner/core/graph/Node;Lorg/kie/workbench/common/stunner/core/graph/Edge;)(a0, a1, a2);
  }-*/;
}