package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoShapeGlyphRenderer;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.ElementShapeGlyphRenderer;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.LienzoElementGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeGlyph;

public class Type_factory__o_k_w_c_s_c_w_c_g_ElementShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ElementShapeGlyphRenderer> { public Type_factory__o_k_w_c_s_c_w_c_g_ElementShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ElementShapeGlyphRenderer.class, "Type_factory__o_k_w_c_s_c_w_c_g_ElementShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ElementShapeGlyphRenderer.class, LienzoElementGlyphRenderer.class, Object.class, DOMGlyphRenderer.class, GlyphRenderer.class });
  }

  public ElementShapeGlyphRenderer createInstance(final ContextManager contextManager) {
    final LienzoGlyphRenderer<ShapeGlyph> _lienzoShapeGlyphRenderer_0 = (LienzoShapeGlyphRenderer) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_g_LienzoShapeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<WidgetElementRendererView> _viewInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { WidgetElementRendererView.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ElementShapeGlyphRenderer instance = new ElementShapeGlyphRenderer(_lienzoShapeGlyphRenderer_0, _viewInstances_1);
    registerDependentScopedReference(instance, _lienzoShapeGlyphRenderer_0);
    registerDependentScopedReference(instance, _viewInstances_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ElementShapeGlyphRenderer) instance, contextManager);
  }

  public void destroyInstanceHelper(final ElementShapeGlyphRenderer instance, final ContextManager contextManager) {
    instance.destroy();
  }
}