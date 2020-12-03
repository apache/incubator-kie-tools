package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.resources.client.CssResource;

public interface PagedTable_BinderImpl_GenCss_style extends CssResource {
  String leftToolBar();
  String rightToolBar();
  String centerToolBar();
  String pager();
  String pagedTableContainer();
  String dataGridContainer();
  @ClassName("pager-row")
  String pagerRow();
}
