package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidget;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetView;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl;

public class Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureWidget> { public Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PictureWidget.class, "Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PictureWidget.class, Object.class, IsWidget.class });
  }

  public PictureWidget createInstance(final ContextManager contextManager) {
    final PictureWidgetView _view_0 = (PictureWidgetViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final PictureWidget instance = new PictureWidget(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}