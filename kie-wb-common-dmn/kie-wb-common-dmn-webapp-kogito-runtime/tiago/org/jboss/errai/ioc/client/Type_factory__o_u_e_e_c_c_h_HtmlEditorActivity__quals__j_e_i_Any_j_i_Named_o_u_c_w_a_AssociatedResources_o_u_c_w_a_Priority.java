package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditor;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorActivity;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlResourceType;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_e_c_c_h_HtmlEditorActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority extends Factory<HtmlEditorActivity> { public Type_factory__o_u_e_e_c_c_h_HtmlEditorActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority() {
    super(new FactoryHandleImpl(HtmlEditorActivity.class, "Type_factory__o_u_e_e_c_c_h_HtmlEditorActivity__quals__j_e_i_Any_j_i_Named_o_u_c_w_a_AssociatedResources_o_u_c_w_a_Priority", Dependent.class, false, "HtmlEditor", true));
    handle.setAssignableTypes(new Class[] { HtmlEditorActivity.class, AbstractWorkbenchEditorActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchEditorActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.createNamed("HtmlEditor"), new AssociatedResources() {
        public Class annotationType() {
          return AssociatedResources.class;
        }
        public String toString() {
          return "@org.uberfire.client.workbench.annotations.AssociatedResources(value=[class org.uberfire.ext.editor.commons.client.htmleditor.HtmlResourceType])";
        }
        public Class[] value() {
          return new Class[] { HtmlResourceType.class };
        }
      }, new Priority() {
        public Class annotationType() {
          return Priority.class;
        }
        public String toString() {
          return "@org.uberfire.client.workbench.annotations.Priority(value=0)";
        }
        public int value() {
          return 0;
        }
    } });
  }

  public HtmlEditorActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final HtmlEditorActivity instance = new HtmlEditorActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final HtmlEditor HtmlEditorActivity_realPresenter = (HtmlEditor) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_HtmlEditor__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, HtmlEditorActivity_realPresenter);
    HtmlEditorActivity_HtmlEditor_realPresenter(instance, HtmlEditorActivity_realPresenter);
    final Instance AbstractWorkbenchEditorActivity_lockManagerProvider = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { LockManager.class }, new Annotation[] { });
    registerDependentScopedReference(instance, AbstractWorkbenchEditorActivity_lockManagerProvider);
    AbstractWorkbenchEditorActivity_Instance_lockManagerProvider(instance, AbstractWorkbenchEditorActivity_lockManagerProvider);
    setIncompleteInstance(null);
    return instance;
  }

  native static Instance AbstractWorkbenchEditorActivity_Instance_lockManagerProvider(AbstractWorkbenchEditorActivity instance) /*-{
    return instance.@org.uberfire.client.mvp.AbstractWorkbenchEditorActivity::lockManagerProvider;
  }-*/;

  native static void AbstractWorkbenchEditorActivity_Instance_lockManagerProvider(AbstractWorkbenchEditorActivity instance, Instance<LockManager> value) /*-{
    instance.@org.uberfire.client.mvp.AbstractWorkbenchEditorActivity::lockManagerProvider = value;
  }-*/;

  native static HtmlEditor HtmlEditorActivity_HtmlEditor_realPresenter(HtmlEditorActivity instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorActivity::realPresenter;
  }-*/;

  native static void HtmlEditorActivity_HtmlEditor_realPresenter(HtmlEditorActivity instance, HtmlEditor value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorActivity::realPresenter = value;
  }-*/;
}