package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.LienzoImageStripLoader;
import org.kie.workbench.common.stunner.client.lienzo.components.LienzoImageStrips;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionInitializer;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_l_c_LienzoImageStripLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoImageStripLoader> { public Type_factory__o_k_w_c_s_c_l_c_LienzoImageStripLoader__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoImageStripLoader.class, "Type_factory__o_k_w_c_s_c_l_c_LienzoImageStripLoader__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoImageStripLoader.class, Object.class, SessionInitializer.class });
  }

  public LienzoImageStripLoader createInstance(final ContextManager contextManager) {
    final ImageStripRegistry _stripRegistry_1 = (ImageStripRegistry) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final LienzoImageStrips _lienzoImageStrips_2 = (LienzoImageStrips) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_Default");
    final LienzoImageStripLoader instance = new LienzoImageStripLoader(_definitionUtils_0, _stripRegistry_1, _lienzoImageStrips_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}