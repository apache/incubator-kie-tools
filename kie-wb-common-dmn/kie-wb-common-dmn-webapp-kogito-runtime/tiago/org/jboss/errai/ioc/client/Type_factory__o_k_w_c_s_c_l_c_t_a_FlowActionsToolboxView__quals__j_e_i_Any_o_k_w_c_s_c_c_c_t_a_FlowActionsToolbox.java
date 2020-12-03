package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.AbstractActionsToolboxView;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions.FlowActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;

public class Type_factory__o_k_w_c_s_c_l_c_t_a_FlowActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox extends Factory<FlowActionsToolboxView> { public Type_factory__o_k_w_c_s_c_l_c_t_a_FlowActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox() {
    super(new FactoryHandleImpl(FlowActionsToolboxView.class, "Type_factory__o_k_w_c_s_c_l_c_t_a_FlowActionsToolboxView__quals__j_e_i_Any_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FlowActionsToolboxView.class, AbstractActionsToolboxView.class, Object.class, ActionsToolboxView.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new FlowActionsToolbox() {
        public Class annotationType() {
          return FlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox()";
        }
    } });
  }

  public FlowActionsToolboxView createInstance(final ContextManager contextManager) {
    final LienzoGlyphRenderers _glyphRenderers_0 = (LienzoGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_g_LienzoGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    final FlowActionsToolboxView instance = new FlowActionsToolboxView(_glyphRenderers_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}