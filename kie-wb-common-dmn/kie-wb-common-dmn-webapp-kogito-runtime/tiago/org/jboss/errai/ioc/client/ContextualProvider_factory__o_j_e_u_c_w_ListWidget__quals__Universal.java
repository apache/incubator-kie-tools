package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;
import org.jboss.errai.databinding.client.api.handler.list.ItemAddedAtHandler;
import org.jboss.errai.databinding.client.api.handler.list.ItemAddedHandler;
import org.jboss.errai.databinding.client.api.handler.list.ItemChangedHandler;
import org.jboss.errai.databinding.client.api.handler.list.ItemRemovedAtHandler;
import org.jboss.errai.databinding.client.api.handler.list.ItemsAddedAtHandler;
import org.jboss.errai.databinding.client.api.handler.list.ItemsAddedHandler;
import org.jboss.errai.databinding.client.api.handler.list.ItemsClearedHandler;
import org.jboss.errai.databinding.client.api.handler.list.ItemsRemovedAtHandler;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.widget.ListWidget;

public class ContextualProvider_factory__o_j_e_u_c_w_ListWidget__quals__Universal extends Factory<ListWidget> { public ContextualProvider_factory__o_j_e_u_c_w_ListWidget__quals__Universal() {
    super(new FactoryHandleImpl(ListWidget.class, "ContextualProvider_factory__o_j_e_u_c_w_ListWidget__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ListWidget.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, BindableListChangeHandler.class, ItemAddedHandler.class, ItemAddedAtHandler.class, ItemsAddedHandler.class, ItemsAddedAtHandler.class, ItemRemovedAtHandler.class, ItemsRemovedAtHandler.class, ItemsClearedHandler.class, ItemChangedHandler.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ListWidget createContextualInstance(final ContextManager contextManager, final Class[] typeArgs, final Annotation[] qualifiers) {
    final ContextualTypeProvider<ListWidget> provider = (ContextualTypeProvider<ListWidget>) contextManager.getInstance("Type_factory__o_j_e_u_c_w_ListWidgetProvider__quals__j_e_i_Any_j_e_i_Default");
    final ListWidget instance = provider.provide(typeArgs, qualifiers);
    return instance;
  }
}