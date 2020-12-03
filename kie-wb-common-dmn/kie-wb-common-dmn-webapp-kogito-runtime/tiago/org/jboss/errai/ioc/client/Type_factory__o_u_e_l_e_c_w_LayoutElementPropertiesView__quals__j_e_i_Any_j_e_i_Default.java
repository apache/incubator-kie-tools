package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesPresenter.View;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesView;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;

public class Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutElementPropertiesView> { public Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutElementPropertiesView.class, "Type_factory__o_u_e_l_e_c_w_LayoutElementPropertiesView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutElementPropertiesView.class, Object.class, View.class, UberView.class, IsWidget.class, HasPresenter.class });
  }

  public LayoutElementPropertiesView createInstance(final ContextManager contextManager) {
    final LayoutElementPropertiesView instance = new LayoutElementPropertiesView();
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onPropertyEditorChangeSubscription", CDI.subscribeLocal("org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent", new AbstractCDIEventCallback<PropertyEditorChangeEvent>() {
      public void fireEvent(final PropertyEditorChangeEvent event) {
        LayoutElementPropertiesView_onPropertyEditorChange_PropertyEditorChangeEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LayoutElementPropertiesView) instance, contextManager);
  }

  public void destroyInstanceHelper(final LayoutElementPropertiesView instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onPropertyEditorChangeSubscription", Subscription.class)).remove();
  }

  public native static void LayoutElementPropertiesView_onPropertyEditorChange_PropertyEditorChangeEvent(LayoutElementPropertiesView instance, PropertyEditorChangeEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesView::onPropertyEditorChange(Lorg/uberfire/ext/properties/editor/model/PropertyEditorChangeEvent;)(a0);
  }-*/;
}