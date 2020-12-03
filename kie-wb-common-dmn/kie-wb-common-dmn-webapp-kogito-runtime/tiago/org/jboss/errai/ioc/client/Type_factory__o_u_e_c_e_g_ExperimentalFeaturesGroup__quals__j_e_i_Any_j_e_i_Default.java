package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroup;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupView;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupView.Presenter;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditor;

public class Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesGroup> { public Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExperimentalFeaturesGroup.class, "Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeaturesGroup.class, Object.class, IsElement.class, Presenter.class, Comparable.class });
  }

  public ExperimentalFeaturesGroup createInstance(final ContextManager contextManager) {
    final ManagedInstance<ExperimentalFeatureEditor> _editorInstance_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ExperimentalFeatureEditor.class }, new Annotation[] { });
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalFeaturesGroupView _view_0 = (ExperimentalFeaturesGroupViewImpl) contextManager.getInstance("Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalFeaturesGroup instance = new ExperimentalFeaturesGroup(_view_0, _translationService_1, _editorInstance_2);
    registerDependentScopedReference(instance, _editorInstance_2);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ExperimentalFeaturesGroup) instance, contextManager);
  }

  public void destroyInstanceHelper(final ExperimentalFeaturesGroup instance, final ContextManager contextManager) {
    instance.clear();
  }
}