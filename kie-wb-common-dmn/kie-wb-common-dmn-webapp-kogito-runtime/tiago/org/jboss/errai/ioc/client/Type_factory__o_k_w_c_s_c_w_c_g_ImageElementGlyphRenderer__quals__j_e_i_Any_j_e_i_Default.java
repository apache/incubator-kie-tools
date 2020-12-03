package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.ImageElementGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.ImageElementRendererView;

public class Type_factory__o_k_w_c_s_c_w_c_g_ImageElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageElementGlyphRenderer> { public Type_factory__o_k_w_c_s_c_w_c_g_ImageElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImageElementGlyphRenderer.class, "Type_factory__o_k_w_c_s_c_w_c_g_ImageElementGlyphRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImageElementGlyphRenderer.class, Object.class, DOMGlyphRenderer.class, GlyphRenderer.class });
  }

  public ImageElementGlyphRenderer createInstance(final ContextManager contextManager) {
    final ManagedInstance<ImageElementRendererView> _viewInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ImageElementRendererView.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ImageElementGlyphRenderer instance = new ImageElementGlyphRenderer(_viewInstances_0);
    registerDependentScopedReference(instance, _viewInstances_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ImageElementGlyphRenderer) instance, contextManager);
  }

  public void destroyInstanceHelper(final ImageElementGlyphRenderer instance, final ContextManager contextManager) {
    instance.destroy();
  }
}