package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.shapes.client.ConnectorGlyphLienzoRenderer;

public class Type_factory__o_k_w_c_s_s_c_ConnectorGlyphLienzoRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorGlyphLienzoRenderer> { public Type_factory__o_k_w_c_s_s_c_ConnectorGlyphLienzoRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConnectorGlyphLienzoRenderer.class, "Type_factory__o_k_w_c_s_s_c_ConnectorGlyphLienzoRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConnectorGlyphLienzoRenderer.class, Object.class, LienzoGlyphRenderer.class, GlyphRenderer.class });
  }

  public ConnectorGlyphLienzoRenderer createInstance(final ContextManager contextManager) {
    final ConnectorGlyphLienzoRenderer instance = new ConnectorGlyphLienzoRenderer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}