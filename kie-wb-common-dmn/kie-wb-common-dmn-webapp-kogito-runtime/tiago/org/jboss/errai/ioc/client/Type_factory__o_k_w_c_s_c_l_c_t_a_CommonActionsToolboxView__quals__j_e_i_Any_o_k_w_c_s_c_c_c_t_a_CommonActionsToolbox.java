package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.AbstractActionsToolboxView;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.CommonActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;

public class Type_factory__o_k_w_c_s_c_l_c_t_a_CommonActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox extends Factory<CommonActionsToolboxView> { public Type_factory__o_k_w_c_s_c_l_c_t_a_CommonActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox() {
    super(new FactoryHandleImpl(CommonActionsToolboxView.class, "Type_factory__o_k_w_c_s_c_l_c_t_a_CommonActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_CommonActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CommonActionsToolboxView.class, AbstractActionsToolboxView.class, Object.class, ActionsToolboxView.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new CommonActionsToolbox() {
        public Class annotationType() {
          return CommonActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox()";
        }
    } });
  }

  public CommonActionsToolboxView createInstance(final ContextManager contextManager) {
    final LienzoGlyphRenderers _glyphRenderers_0 = (LienzoGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    final CommonActionsToolboxView instance = new CommonActionsToolboxView(_glyphRenderers_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}