package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactoryImpl;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_c_GuidedTourUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourUtils> { public Type_factory__o_k_w_c_d_w_k_c_c_t_c_GuidedTourUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GuidedTourUtils.class, "Type_factory__o_k_w_c_d_w_k_c_c_t_c_GuidedTourUtils__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GuidedTourUtils.class, Object.class });
  }

  public GuidedTourUtils createInstance(final ContextManager contextManager) {
    final TextPropertyProviderFactory _textPropertyProviderFactory_0 = (TextPropertyProviderFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final GuidedTourUtils instance = new GuidedTourUtils(_textPropertyProviderFactory_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}