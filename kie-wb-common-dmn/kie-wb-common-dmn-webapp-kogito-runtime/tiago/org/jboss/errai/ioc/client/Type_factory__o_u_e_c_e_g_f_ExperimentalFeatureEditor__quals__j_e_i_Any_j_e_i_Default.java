package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditor;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorView;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorView.Presenter;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditorViewImpl;
import org.uberfire.experimental.client.service.impl.CDIClientFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;

public class Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeatureEditor> { public Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExperimentalFeatureEditor.class, "Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeatureEditor.class, Object.class, Presenter.class, IsElement.class, Comparable.class });
  }

  public ExperimentalFeatureEditor createInstance(final ContextManager contextManager) {
    final ExperimentalFeatureDefRegistry _registry_0 = (CDIClientFeatureDefRegistry) contextManager.getInstance("Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalFeatureEditorView _view_2 = (ExperimentalFeatureEditorViewImpl) contextManager.getInstance("Type_factory__o_u_e_c_e_g_f_ExperimentalFeatureEditorViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalFeatureEditor instance = new ExperimentalFeatureEditor(_registry_0, _translationService_1, _view_2);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _view_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ExperimentalFeatureEditor instance) {
    instance.init();
  }
}