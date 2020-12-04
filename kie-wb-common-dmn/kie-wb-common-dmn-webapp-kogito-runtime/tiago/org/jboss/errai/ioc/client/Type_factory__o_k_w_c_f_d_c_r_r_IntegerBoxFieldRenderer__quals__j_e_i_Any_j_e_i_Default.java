package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBox;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.IntegerBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;

public class Type_factory__o_k_w_c_f_d_c_r_r_IntegerBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerBoxFieldRenderer> { public Type_factory__o_k_w_c_f_d_c_r_r_IntegerBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IntegerBoxFieldRenderer.class, "Type_factory__o_k_w_c_f_d_c_r_r_IntegerBoxFieldRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IntegerBoxFieldRenderer.class, FieldRenderer.class, Object.class, RequiresValueConverter.class });
  }

  public IntegerBoxFieldRenderer createInstance(final ContextManager contextManager) {
    final IntegerBox _integerBox_0 = (IntegerBox) contextManager.getInstance("Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBox__quals__j_e_i_Any_j_e_i_Default");
    final IntegerBoxFieldRenderer instance = new IntegerBoxFieldRenderer(_integerBox_0);
    registerDependentScopedReference(instance, _integerBox_0);
    setIncompleteInstance(instance);
    final ManagedInstance FieldRenderer_formGroupsInstance = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefaultFormGroup.class }, new Annotation[] { });
    registerDependentScopedReference(instance, FieldRenderer_formGroupsInstance);
    FieldRenderer_ManagedInstance_formGroupsInstance(instance, FieldRenderer_formGroupsInstance);
    final FormsElementWrapperWidgetUtilImpl FieldRenderer_wrapperWidgetUtil = (FormsElementWrapperWidgetUtilImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    FieldRenderer_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(instance, FieldRenderer_wrapperWidgetUtil);
    final ConfigErrorDisplayer FieldRenderer_errorDisplayer = (ConfigErrorDisplayer) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayer__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, FieldRenderer_errorDisplayer);
    FieldRenderer_ConfigErrorDisplayer_errorDisplayer(instance, FieldRenderer_errorDisplayer);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((IntegerBoxFieldRenderer) instance, contextManager);
  }

  public void destroyInstanceHelper(final IntegerBoxFieldRenderer instance, final ContextManager contextManager) {
    instance.preDestroy();
  }

  native static ManagedInstance FieldRenderer_ManagedInstance_formGroupsInstance(FieldRenderer instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer::formGroupsInstance;
  }-*/;

  native static void FieldRenderer_ManagedInstance_formGroupsInstance(FieldRenderer instance, ManagedInstance<DefaultFormGroup> value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer::formGroupsInstance = value;
  }-*/;

  native static FormsElementWrapperWidgetUtil FieldRenderer_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(FieldRenderer instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer::wrapperWidgetUtil;
  }-*/;

  native static void FieldRenderer_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(FieldRenderer instance, FormsElementWrapperWidgetUtil value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer::wrapperWidgetUtil = value;
  }-*/;

  native static ConfigErrorDisplayer FieldRenderer_ConfigErrorDisplayer_errorDisplayer(FieldRenderer instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer::errorDisplayer;
  }-*/;

  native static void FieldRenderer_ConfigErrorDisplayer_errorDisplayer(FieldRenderer instance, ConfigErrorDisplayer value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer::errorDisplayer = value;
  }-*/;
}