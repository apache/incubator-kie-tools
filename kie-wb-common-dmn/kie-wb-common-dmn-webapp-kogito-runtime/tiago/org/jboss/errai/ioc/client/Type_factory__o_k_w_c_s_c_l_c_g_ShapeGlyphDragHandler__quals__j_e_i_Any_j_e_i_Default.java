package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;

public class Type_factory__o_k_w_c_s_c_l_c_g_ShapeGlyphDragHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeGlyphDragHandler> { public Type_factory__o_k_w_c_s_c_l_c_g_ShapeGlyphDragHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ShapeGlyphDragHandler.class, "Type_factory__o_k_w_c_s_c_l_c_g_ShapeGlyphDragHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ShapeGlyphDragHandler.class, Object.class });
  }

  public ShapeGlyphDragHandler createInstance(final ContextManager contextManager) {
    final LienzoGlyphRenderers _glyphLienzoGlyphRenderer_0 = (LienzoGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    final ShapeGlyphDragHandler instance = new ShapeGlyphDragHandler(_glyphLienzoGlyphRenderer_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}