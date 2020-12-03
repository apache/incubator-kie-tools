package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.common.HTMLDownloadHelper;

public class Type_factory__o_k_w_c_d_c_e_d_c_HTMLDownloadHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<HTMLDownloadHelper> { public Type_factory__o_k_w_c_d_c_e_d_c_HTMLDownloadHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HTMLDownloadHelper.class, "Type_factory__o_k_w_c_d_c_e_d_c_HTMLDownloadHelper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HTMLDownloadHelper.class, Object.class });
  }

  public HTMLDownloadHelper createInstance(final ContextManager contextManager) {
    final HTMLDownloadHelper instance = new HTMLDownloadHelper();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}