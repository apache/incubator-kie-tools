package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.canvas.controls.builder.ObserverBuilderControl;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.AbstractElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.rule.ClientRuleManager;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexerImpl;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

public class Type_factory__o_k_w_c_d_c_c_c_b_ObserverBuilderControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer extends Factory<ObserverBuilderControl> { public Type_factory__o_k_w_c_d_c_c_c_b_ObserverBuilderControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer() {
    super(new FactoryHandleImpl(ObserverBuilderControl.class, "Type_factory__o_k_w_c_d_c_c_c_b_ObserverBuilderControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ObserverBuilderControl.class, org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ObserverBuilderControl.class, AbstractElementBuilderControl.class, AbstractCanvasHandlerControl.class, Object.class, CanvasControl.class, ElementBuilderControl.class, BuilderControl.class, RequiresCommandManager.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
      }, new Observer() {
        public Class annotationType() {
          return Observer.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer()";
        }
    } });
  }

  public ObserverBuilderControl createInstance(final ContextManager contextManager) {
    final GraphBoundsIndexer _graphBoundsIndexer_5 = (GraphBoundsIndexerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_i_b_GraphBoundsIndexerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClientDefinitionManager _clientDefinitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final DefaultCanvasCommandFactory _canvasCommandFactory_3 = (DefaultCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final RuleManager _ruleManager_2 = (ClientRuleManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationMessages _translationMessages_4 = (ClientTranslationMessages) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationMessages__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasSelectionEvent> _canvasSelectionEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final DMNDiagramsSession _dmnDiagramsSession_7 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final ClientFactoryService _clientFactoryServices_1 = (ClientFactoryService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_ClientFactoryService__quals__j_e_i_Any_j_e_i_Default");
    final ObserverBuilderControl instance = new ObserverBuilderControl(_clientDefinitionManager_0, _clientFactoryServices_1, _ruleManager_2, _canvasCommandFactory_3, _translationMessages_4, _graphBoundsIndexer_5, _canvasSelectionEvent_6, _dmnDiagramsSession_7);
    registerDependentScopedReference(instance, _graphBoundsIndexer_5);
    registerDependentScopedReference(instance, _canvasSelectionEvent_6);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onBuildCanvasShapeSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent", new AbstractCDIEventCallback<BuildCanvasShapeEvent>() {
      public void fireEvent(final BuildCanvasShapeEvent event) {
        ObserverBuilderControl_onBuildCanvasShape_BuildCanvasShapeEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ObserverBuilderControl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ObserverBuilderControl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onBuildCanvasShapeSubscription", Subscription.class)).remove();
  }

  public native static void ObserverBuilderControl_onBuildCanvasShape_BuildCanvasShapeEvent(org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ObserverBuilderControl instance, BuildCanvasShapeEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ObserverBuilderControl::onBuildCanvasShape(Lorg/kie/workbench/common/stunner/core/client/canvas/controls/event/BuildCanvasShapeEvent;)(a0);
  }-*/;
}