package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.SvgElementGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.ImageElementRendererView;
import org.kie.workbench.common.stunner.core.client.util.SvgDataUriGenerator;

public class Type_factory__o_k_w_c_s_c_w_c_g_SvgElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<SvgElementGlyphRenderer> { public Type_factory__o_k_w_c_s_c_w_c_g_SvgElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SvgElementGlyphRenderer.class, "Type_factory__o_k_w_c_s_c_w_c_g_SvgElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SvgElementGlyphRenderer.class, Object.class, DOMGlyphRenderer.class, GlyphRenderer.class });
  }

  public SvgElementGlyphRenderer createInstance(final ContextManager contextManager) {
    final SvgDataUriGenerator _svgDataUriUtil_0 = (SvgDataUriGenerator) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_u_SvgDataUriGenerator__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ImageElementRendererView> _viewInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ImageElementRendererView.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final SvgElementGlyphRenderer instance = new SvgElementGlyphRenderer(_svgDataUriUtil_0, _viewInstances_1);
    registerDependentScopedReference(instance, _viewInstances_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SvgElementGlyphRenderer) instance, contextManager);
  }

  public void destroyInstanceHelper(final SvgElementGlyphRenderer instance, final ContextManager contextManager) {
    instance.destroy();
  }
}