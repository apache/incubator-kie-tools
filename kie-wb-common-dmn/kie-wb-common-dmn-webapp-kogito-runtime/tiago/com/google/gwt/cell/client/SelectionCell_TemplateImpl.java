package com.google.gwt.cell.client;

public class SelectionCell_TemplateImpl implements com.google.gwt.cell.client.SelectionCell.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml deselected(java.lang.String arg0) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<option value=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("\">");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("</option>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml selected(java.lang.String arg0) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<option value=\"");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
sb.append("\" selected=\"selected\">");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
sb.append("</option>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
