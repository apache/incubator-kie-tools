package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.AbstractActionsToolboxView;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.MorphActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox;

public class Type_factory__o_k_w_c_s_c_l_c_t_a_MorphActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox extends Factory<MorphActionsToolboxView> { public Type_factory__o_k_w_c_s_c_l_c_t_a_MorphActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox() {
    super(new FactoryHandleImpl(MorphActionsToolboxView.class, "Type_factory__o_k_w_c_s_c_l_c_t_a_MorphActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_MorphActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MorphActionsToolboxView.class, AbstractActionsToolboxView.class, Object.class, ActionsToolboxView.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new MorphActionsToolbox() {
        public Class annotationType() {
          return MorphActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox()";
        }
    } });
  }

  public MorphActionsToolboxView createInstance(final ContextManager contextManager) {
    final LienzoGlyphRenderers _glyphRenderers_0 = (LienzoGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    final MorphActionsToolboxView instance = new MorphActionsToolboxView(_glyphRenderers_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}