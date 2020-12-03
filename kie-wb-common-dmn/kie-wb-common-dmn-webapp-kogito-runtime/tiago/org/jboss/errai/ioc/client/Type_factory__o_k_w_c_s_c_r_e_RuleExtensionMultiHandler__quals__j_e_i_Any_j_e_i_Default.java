package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionMultiHandler;

public class Type_factory__o_k_w_c_s_c_r_e_RuleExtensionMultiHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<RuleExtensionMultiHandler> { public Type_factory__o_k_w_c_s_c_r_e_RuleExtensionMultiHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RuleExtensionMultiHandler.class, "Type_factory__o_k_w_c_s_c_r_e_RuleExtensionMultiHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RuleExtensionMultiHandler.class, RuleExtensionHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public RuleExtensionMultiHandler createInstance(final ContextManager contextManager) {
    final RuleExtensionMultiHandler instance = new RuleExtensionMultiHandler();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}