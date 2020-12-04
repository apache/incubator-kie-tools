package org.jboss.errai.ioc.client;

import java.util.function.Consumer;
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
import org.kie.workbench.common.stunner.core.client.rule.ClientRuleManager;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.validation.GraphValidator;
import org.kie.workbench.common.stunner.core.validation.Validator;
import org.kie.workbench.common.stunner.core.validation.impl.GraphValidatorImpl;

public class Type_factory__o_k_w_c_s_c_v_i_GraphValidatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphValidatorImpl> { private class Type_factory__o_k_w_c_s_c_v_i_GraphValidatorImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends GraphValidatorImpl implements Proxy<GraphValidatorImpl> {
    private final ProxyHelper<GraphValidatorImpl> proxyHelper = new ProxyHelperImpl<GraphValidatorImpl>("Type_factory__o_k_w_c_s_c_v_i_GraphValidatorImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final GraphValidatorImpl instance) {

    }

    public GraphValidatorImpl asBeanType() {
      return this;
    }

    public void setInstance(final GraphValidatorImpl instance) {
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

    @Override public void validate(Graph graph, Consumer callback) {
      if (proxyHelper != null) {
        final GraphValidatorImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.validate(graph, callback);
      } else {
        super.validate(graph, callback);
      }
    }

    @Override public void validate(Graph graph, RuleSet ruleSet, Consumer callback) {
      if (proxyHelper != null) {
        final GraphValidatorImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.validate(graph, ruleSet, callback);
      } else {
        super.validate(graph, ruleSet, callback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final GraphValidatorImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_v_i_GraphValidatorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GraphValidatorImpl.class, "Type_factory__o_k_w_c_s_c_v_i_GraphValidatorImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GraphValidatorImpl.class, Object.class, GraphValidator.class, Validator.class });
  }

  public GraphValidatorImpl createInstance(final ContextManager contextManager) {
    final RuleManager _ruleManager_1 = (ClientRuleManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final TreeWalkTraverseProcessor _treeWalkTraverseProcessor_2 = (TreeWalkTraverseProcessorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default");
    final GraphValidatorImpl instance = new GraphValidatorImpl(_definitionManager_0, _ruleManager_1, _treeWalkTraverseProcessor_2);
    registerDependentScopedReference(instance, _treeWalkTraverseProcessor_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<GraphValidatorImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_v_i_GraphValidatorImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}