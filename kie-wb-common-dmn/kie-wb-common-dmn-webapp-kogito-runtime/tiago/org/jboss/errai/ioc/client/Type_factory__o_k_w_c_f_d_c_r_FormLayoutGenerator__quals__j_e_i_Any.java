package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormGeneratorDriver;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormLayoutGenerator;
import org.uberfire.ext.layout.editor.client.generator.AbstractLayoutGenerator;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;

public class Type_factory__o_k_w_c_f_d_c_r_FormLayoutGenerator__quals__j_e_i_Any extends Factory<FormLayoutGenerator> { public Type_factory__o_k_w_c_f_d_c_r_FormLayoutGenerator__quals__j_e_i_Any() {
    super(new FactoryHandleImpl(FormLayoutGenerator.class, "Type_factory__o_k_w_c_f_d_c_r_FormLayoutGenerator__quals__j_e_i_Any", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormLayoutGenerator.class, AbstractLayoutGenerator.class, Object.class, LayoutGenerator.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION });
  }

  public FormLayoutGenerator createInstance(final ContextManager contextManager) {
    final FormGeneratorDriver _driver_0 = (FormGeneratorDriver) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_FormGeneratorDriver__quals__j_e_i_Any_j_e_i_Default");
    final FormLayoutGenerator instance = new FormLayoutGenerator(_driver_0);
    registerDependentScopedReference(instance, _driver_0);
    setIncompleteInstance(instance);
    final LayoutEditorCssHelper AbstractLayoutGenerator_cssPropertiesHelper = (LayoutEditorCssHelper) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default");
    AbstractLayoutGenerator_LayoutEditorCssHelper_cssPropertiesHelper(instance, AbstractLayoutGenerator_cssPropertiesHelper);
    setIncompleteInstance(null);
    return instance;
  }

  native static LayoutEditorCssHelper AbstractLayoutGenerator_LayoutEditorCssHelper_cssPropertiesHelper(AbstractLayoutGenerator instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.generator.AbstractLayoutGenerator::cssPropertiesHelper;
  }-*/;

  native static void AbstractLayoutGenerator_LayoutEditorCssHelper_cssPropertiesHelper(AbstractLayoutGenerator instance, LayoutEditorCssHelper value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.generator.AbstractLayoutGenerator::cssPropertiesHelper = value;
  }-*/;
}