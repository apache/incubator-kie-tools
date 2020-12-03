package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.AbstractLienzoShapeGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoSvgDataUriGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.client.util.SvgDataUriGenerator;

public class Type_factory__o_k_w_c_s_c_l_c_g_LienzoSvgDataUriGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoSvgDataUriGlyphRenderer> { public Type_factory__o_k_w_c_s_c_l_c_g_LienzoSvgDataUriGlyphRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoSvgDataUriGlyphRenderer.class, "Type_factory__o_k_w_c_s_c_l_c_g_LienzoSvgDataUriGlyphRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoSvgDataUriGlyphRenderer.class, AbstractLienzoShapeGlyphRenderer.class, Object.class, LienzoGlyphRenderer.class, GlyphRenderer.class });
  }

  public LienzoSvgDataUriGlyphRenderer createInstance(final ContextManager contextManager) {
    final SvgDataUriGenerator _svgDataUriUtil_0 = (SvgDataUriGenerator) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_Default");
    final LienzoSvgDataUriGlyphRenderer instance = new LienzoSvgDataUriGlyphRenderer(_svgDataUriUtil_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}