package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoShapeGlyphRenderer;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;

public class Type_factory__o_k_w_c_s_c_l_c_g_LienzoShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoShapeGlyphRenderer> { public Type_factory__o_k_w_c_s_c_l_c_g_LienzoShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoShapeGlyphRenderer.class, "Type_factory__o_k_w_c_s_c_l_c_g_LienzoShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoShapeGlyphRenderer.class, Object.class, LienzoGlyphRenderer.class, GlyphRenderer.class });
  }

  public LienzoShapeGlyphRenderer createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final LienzoShapeGlyphRenderer instance = new LienzoShapeGlyphRenderer(_factoryManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}