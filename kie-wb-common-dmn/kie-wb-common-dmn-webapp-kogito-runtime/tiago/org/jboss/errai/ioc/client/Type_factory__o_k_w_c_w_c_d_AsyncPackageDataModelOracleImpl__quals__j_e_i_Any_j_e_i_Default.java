package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;

public class Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AsyncPackageDataModelOracleImpl> { public Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AsyncPackageDataModelOracleImpl.class, "Type_factory__o_k_w_c_w_c_d_AsyncPackageDataModelOracleImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AsyncPackageDataModelOracleImpl.class, Object.class, AsyncPackageDataModelOracle.class });
  }

  public AsyncPackageDataModelOracleImpl createInstance(final ContextManager contextManager) {
    final Caller<IncrementalDataModelService> _service_0 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { IncrementalDataModelService.class }, new Annotation[] { });
    final Instance<DynamicValidator> _validatorInstance_1 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { DynamicValidator.class }, new Annotation[] { });
    final AsyncPackageDataModelOracleImpl instance = new AsyncPackageDataModelOracleImpl(_service_0, _validatorInstance_1);
    registerDependentScopedReference(instance, _service_0);
    registerDependentScopedReference(instance, _validatorInstance_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}