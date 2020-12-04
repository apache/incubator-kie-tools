package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.ImageStripDOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripRegistry;

public class Type_factory__o_k_w_c_s_c_w_c_g_ImageStripDOMGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageStripDOMGlyphRenderer> { public Type_factory__o_k_w_c_s_c_w_c_g_ImageStripDOMGlyphRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImageStripDOMGlyphRenderer.class, "Type_factory__o_k_w_c_s_c_w_c_g_ImageStripDOMGlyphRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImageStripDOMGlyphRenderer.class, Object.class, DOMGlyphRenderer.class, GlyphRenderer.class });
  }

  public ImageStripDOMGlyphRenderer createInstance(final ContextManager contextManager) {
    final ImageStripRegistry _stripRegistry_0 = (ImageStripRegistry) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_ImageStripRegistry__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<WidgetElementRendererView> _views_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { WidgetElementRendererView.class }, new Annotation[] { });
    final ImageStripDOMGlyphRenderer instance = new ImageStripDOMGlyphRenderer(_stripRegistry_0, _views_1);
    registerDependentScopedReference(instance, _views_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ImageStripDOMGlyphRenderer) instance, contextManager);
  }

  public void destroyInstanceHelper(final ImageStripDOMGlyphRenderer instance, final ContextManager contextManager) {
    instance.destroy();
  }
}