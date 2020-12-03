package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.BS3IconTypeGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.glyph.GlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;

public class Type_factory__o_k_w_c_s_c_w_c_g_BS3IconTypeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<BS3IconTypeGlyphRenderer> { public Type_factory__o_k_w_c_s_c_w_c_g_BS3IconTypeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BS3IconTypeGlyphRenderer.class, "Type_factory__o_k_w_c_s_c_w_c_g_BS3IconTypeGlyphRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BS3IconTypeGlyphRenderer.class, Object.class, DOMGlyphRenderer.class, GlyphRenderer.class });
  }

  public BS3IconTypeGlyphRenderer createInstance(final ContextManager contextManager) {
    final ManagedInstance<WidgetElementRendererView> _viewInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { WidgetElementRendererView.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final BS3IconTypeGlyphRenderer instance = new BS3IconTypeGlyphRenderer(_viewInstances_0);
    registerDependentScopedReference(instance, _viewInstances_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((BS3IconTypeGlyphRenderer) instance, contextManager);
  }

  public void destroyInstanceHelper(final BS3IconTypeGlyphRenderer instance, final ContextManager contextManager) {
    instance.destroy();
  }
}