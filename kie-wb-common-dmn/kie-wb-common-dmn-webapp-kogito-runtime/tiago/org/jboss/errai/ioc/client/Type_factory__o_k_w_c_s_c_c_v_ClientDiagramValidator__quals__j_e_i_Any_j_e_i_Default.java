package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.api.ManagedInstance;
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
import org.kie.workbench.common.stunner.core.client.validation.ClientDiagramValidator;
import org.kie.workbench.common.stunner.core.client.validation.ClientModelValidator;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.validation.DiagramValidator;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;
import org.kie.workbench.common.stunner.core.validation.Validator;
import org.kie.workbench.common.stunner.core.validation.impl.AbstractDiagramValidator;

public class Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDiagramValidator> { private class Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientDiagramValidator implements Proxy<ClientDiagramValidator> {
    private final ProxyHelper<ClientDiagramValidator> proxyHelper = new ProxyHelperImpl<ClientDiagramValidator>("Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientDiagramValidator instance) {

    }

    public ClientDiagramValidator asBeanType() {
      return this;
    }

    public void setInstance(final ClientDiagramValidator instance) {
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

    @Override public void validate(Diagram diagram, Consumer resultConsumer) {
      if (proxyHelper != null) {
        final ClientDiagramValidator proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.validate(diagram, resultConsumer);
      } else {
        super.validate(diagram, resultConsumer);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientDiagramValidator proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientDiagramValidator.class, "Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientDiagramValidator.class, AbstractDiagramValidator.class, Object.class, DiagramValidator.class, Validator.class });
  }

  public ClientDiagramValidator createInstance(final ContextManager contextManager) {
    final ModelValidator _modelValidator_3 = (ClientModelValidator) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_v_ClientModelValidator__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DomainValidator> _validators_4 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DomainValidator.class }, new Annotation[] { });
    final TreeWalkTraverseProcessor _treeWalkTraverseProcessor_2 = (TreeWalkTraverseProcessorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_t_t_TreeWalkTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final RuleManager _ruleManager_1 = (ClientRuleManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientDiagramValidator instance = new ClientDiagramValidator(_definitionManager_0, _ruleManager_1, _treeWalkTraverseProcessor_2, _modelValidator_3, _validators_4);
    registerDependentScopedReference(instance, _validators_4);
    registerDependentScopedReference(instance, _treeWalkTraverseProcessor_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientDiagramValidator> proxyImpl = new Type_factory__o_k_w_c_s_c_c_v_ClientDiagramValidator__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}