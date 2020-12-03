package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoImageStripGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;

public class Type_factory__o_k_w_c_s_c_l_c_g_LienzoImageStripGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoImageStripGlyphRenderer> { public Type_factory__o_k_w_c_s_c_l_c_g_LienzoImageStripGlyphRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoImageStripGlyphRenderer.class, "Type_factory__o_k_w_c_s_c_l_c_g_LienzoImageStripGlyphRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoImageStripGlyphRenderer.class, Object.class, LienzoGlyphRenderer.class, GlyphRenderer.class });
  }

  public LienzoImageStripGlyphRenderer createInstance(final ContextManager contextManager) {
    final LienzoImageStripGlyphRenderer instance = new LienzoImageStripGlyphRenderer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}