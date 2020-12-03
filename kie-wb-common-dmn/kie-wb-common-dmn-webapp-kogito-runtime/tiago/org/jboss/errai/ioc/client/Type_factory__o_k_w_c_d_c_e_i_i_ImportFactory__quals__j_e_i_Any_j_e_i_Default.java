package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.ImportFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;

public class Type_factory__o_k_w_c_d_c_e_i_i_ImportFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<ImportFactory> { public Type_factory__o_k_w_c_d_c_e_i_i_ImportFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImportFactory.class, "Type_factory__o_k_w_c_d_c_e_i_i_ImportFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImportFactory.class, Object.class });
  }

  public ImportFactory createInstance(final ContextManager contextManager) {
    final IncludedModelsIndex _modelsIndex_0 = (IncludedModelsIndex) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default");
    final ImportFactory instance = new ImportFactory(_modelsIndex_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}