package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditorActivity;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchClientEditorActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.IsClientEditor;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchClientEditorActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditorActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named_o_k_w_c_s_c_c_a_DiagramEditor_o_u_c_m_IsClientEditor extends Factory<DMNDiagramEditorActivity> { public Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditorActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named_o_k_w_c_s_c_c_a_DiagramEditor_o_u_c_m_IsClientEditor() {
    super(new FactoryHandleImpl(DMNDiagramEditorActivity.class, "Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditorActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named_o_k_w_c_s_c_c_a_DiagramEditor_o_u_c_m_IsClientEditor", Dependent.class, false, "DMNDiagramEditor", true));
    handle.setAssignableTypes(new Class[] { DMNDiagramEditorActivity.class, AbstractWorkbenchClientEditorActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchClientEditorActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("DMNDiagramEditor"), new DiagramEditor() {
        public Class annotationType() {
          return DiagramEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor()";
        }
      }, new IsClientEditor() {
        public Class annotationType() {
          return IsClientEditor.class;
        }
        public String toString() {
          return "@org.uberfire.client.mvp.IsClientEditor()";
        }
    } });
  }

  public DMNDiagramEditorActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramEditorActivity instance = new DMNDiagramEditorActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final DMNDiagramEditor DMNDiagramEditorActivity_realPresenter = (DMNDiagramEditor) contextManager.getInstance("Type_factory__o_k_w_c_d_s_c_e_DMNDiagramEditor__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_a_DiagramEditor");
    DMNDiagramEditorActivity_DMNDiagramEditor_realPresenter(instance, DMNDiagramEditorActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static DMNDiagramEditor DMNDiagramEditorActivity_DMNDiagramEditor_realPresenter(DMNDiagramEditorActivity instance) /*-{
    return instance.@org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditorActivity::realPresenter;
  }-*/;

  native static void DMNDiagramEditorActivity_DMNDiagramEditor_realPresenter(DMNDiagramEditorActivity instance, DMNDiagramEditor value) /*-{
    instance.@org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditorActivity::realPresenter = value;
  }-*/;
}