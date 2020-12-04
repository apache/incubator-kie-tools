package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitionsProducer;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorModelEnricher;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGridSupplementaryEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.BaseSupplementaryFunctionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLFunctionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView.Presenter;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

public class Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLFunctionEditorDefinition__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor extends Factory<PMMLFunctionEditorDefinition> { public Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLFunctionEditorDefinition__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor() {
    super(new FactoryHandleImpl(PMMLFunctionEditorDefinition.class, "Type_factory__o_k_w_c_d_c_e_e_t_f_s_p_PMMLFunctionEditorDefinition__quals__j_e_i_Any_o_k_w_c_d_c_e_e_t_f_FunctionGridSupplementaryEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PMMLFunctionEditorDefinition.class, BaseSupplementaryFunctionEditorDefinition.class, BaseEditorDefinition.class, Object.class, ExpressionEditorDefinition.class, ExpressionEditorModelEnricher.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new FunctionGridSupplementaryEditor() {
        public Class annotationType() {
          return FunctionGridSupplementaryEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGridSupplementaryEditor()";
        }
    } });
  }

  public PMMLFunctionEditorDefinition createInstance(final ContextManager contextManager) {
    final Event<RefreshFormPropertiesEvent> _refreshFormPropertiesEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshFormPropertiesEvent.class }, new Annotation[] { });
    final Event<DomainObjectSelectionEvent> _domainObjectSelectionEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DomainObjectSelectionEvent.class }, new Annotation[] { });
    final Event<ExpressionEditorChanged> _editorSelectedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ExpressionEditorChanged.class }, new Annotation[] { });
    final ReadOnlyProvider _readOnlyProvider_10 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final TranslationService _translationService_8 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DefaultCanvasCommandFactory _canvasCommandFactory_3 = (DefaultCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final Supplier<ExpressionEditorDefinitions> _expressionEditorDefinitionsSupplier_9 = (ExpressionEditorDefinitionsProducer) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_e_t_ExpressionEditorDefinitionsProducer__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_2 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final Presenter _listSelector_7 = (ListSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final PMMLFunctionEditorDefinition instance = new PMMLFunctionEditorDefinition(_definitionUtils_0, _sessionManager_1, _sessionCommandManager_2, _canvasCommandFactory_3, _editorSelectedEvent_4, _refreshFormPropertiesEvent_5, _domainObjectSelectionEvent_6, _listSelector_7, _translationService_8, _expressionEditorDefinitionsSupplier_9, _readOnlyProvider_10);
    registerDependentScopedReference(instance, _refreshFormPropertiesEvent_5);
    registerDependentScopedReference(instance, _domainObjectSelectionEvent_6);
    registerDependentScopedReference(instance, _editorSelectedEvent_4);
    registerDependentScopedReference(instance, _translationService_8);
    registerDependentScopedReference(instance, _listSelector_7);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}