package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.AbstractElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Element;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ElementBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.rule.ClientRuleManager;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexerImpl;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

public class Type_factory__o_k_w_c_s_c_c_c_c_b_i_ElementBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_b_i_Element extends Factory<ElementBuilderControlImpl> { public Type_factory__o_k_w_c_s_c_c_c_c_b_i_ElementBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_b_i_Element() {
    super(new FactoryHandleImpl(ElementBuilderControlImpl.class, "Type_factory__o_k_w_c_s_c_c_c_c_b_i_ElementBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_b_i_Element", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ElementBuilderControlImpl.class, AbstractElementBuilderControl.class, AbstractCanvasHandlerControl.class, Object.class, CanvasControl.class, ElementBuilderControl.class, BuilderControl.class, RequiresCommandManager.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, new Element() {
        public Class annotationType() {
          return Element.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Element()";
        }
    } });
  }

  public ElementBuilderControlImpl createInstance(final ContextManager contextManager) {
    final RuleManager _ruleManager_2 = (ClientRuleManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationMessages _translationMessages_4 = (ClientTranslationMessages) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_Default");
    final ClientDefinitionManager _clientDefinitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final CanvasCommandFactory<AbstractCanvasHandler> _canvasCommandFactory_3 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final GraphBoundsIndexer _graphBoundsIndexer_5 = (GraphBoundsIndexerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_i_b_GraphBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClientFactoryService _clientFactoryServices_1 = (ClientFactoryService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default");
    final ElementBuilderControlImpl instance = new ElementBuilderControlImpl(_clientDefinitionManager_0, _clientFactoryServices_1, _ruleManager_2, _canvasCommandFactory_3, _translationMessages_4, _graphBoundsIndexer_5);
    registerDependentScopedReference(instance, _graphBoundsIndexer_5);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}