package org.uberfire.ext.layout.editor.client.resources.i18n;

public class CssAllowedValueConstants_ implements org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants {
  
  public java.lang.String TEXT_ALIGN__LEFT() {
    return "Left";
  }
  
  public java.lang.String TEXT_ALIGN__CENTER() {
    return "Center";
  }
  
  public java.lang.String TEXT_ALIGN__RIGHT() {
    return "Right";
  }
  
  public java.lang.String FONT_SIZE__XX_SMALL() {
    return "Extra Small";
  }
  
  public java.lang.String FONT_SIZE__X_SMALL() {
    return "Super Small";
  }
  
  public java.lang.String FONT_SIZE__SMALL() {
    return "Small";
  }
  
  public java.lang.String FONT_SIZE__MEDIUM() {
    return "Medium";
  }
  
  public java.lang.String FONT_SIZE__LARGE() {
    return "Large";
  }
  
  public java.lang.String FONT_SIZE__X_LARGE() {
    return "Super Large";
  }
  
  public java.lang.String FONT_SIZE__XX_LARGE() {
    return "Extra Large";
  }
  
  public java.lang.String FONT_WEIGHT__NORMAL() {
    return "Normal";
  }
  
  public java.lang.String FONT_WEIGHT__BOLD() {
    return "Bold";
  }
  
  public java.lang.String FONT_WEIGHT__BOLDER() {
    return "Bolder";
  }
  
  public java.lang.String FONT_WEIGHT__LIGHTER() {
    return "Lighter";
  }
  
  public java.lang.String TEXT_DECORATION__NONE() {
    return "None";
  }
  
  public java.lang.String TEXT_DECORATION__UNDERLINE() {
    return "Underline";
  }
  
  public java.lang.String TEXT_DECORATION__OVERLINE() {
    return "Overline";
  }
  
  public java.lang.String TEXT_DECORATION__LINE_THROUGH() {
    return "Line through";
  }
  
  public boolean getBoolean(java.lang.String arg0) {
    Boolean target = (Boolean) cache.get(arg0);
    if (target != null) {
      return target.booleanValue();
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants", arg0);
  }
  
  public double getDouble(java.lang.String arg0) {
    Double target = (Double) cache.get(arg0);
    if (target != null) {
      return target.doubleValue();
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants", arg0);
  }
  
  public float getFloat(java.lang.String arg0) {
    Float target = (Float) cache.get(arg0);
    if (target != null) {
      return target.floatValue();
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants", arg0);
  }
  
  public int getInt(java.lang.String arg0) {
    Integer target = (Integer) cache.get(arg0);
    if (target != null) {
      return target.intValue();
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants", arg0);
  }
  
  public java.util.Map<java.lang.String, java.lang.String> getMap(java.lang.String arg0) {
    java.util.Map<java.lang.String, java.lang.String> target = (java.util.Map<java.lang.String, java.lang.String>) cache.get(arg0);
    if (target != null) {
      return target;
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants", arg0);
  }
  
  public java.lang.String getString(java.lang.String arg0) {
    java.lang.String target = (java.lang.String) cache.get(arg0);
    if (target != null) {
      return target;
    }
    if (arg0.equals("TEXT_ALIGN__LEFT")) {
      String answer = TEXT_ALIGN__LEFT();
      cache.put("TEXT_ALIGN__LEFT",answer);
      return answer;
    }
    if (arg0.equals("TEXT_ALIGN__CENTER")) {
      String answer = TEXT_ALIGN__CENTER();
      cache.put("TEXT_ALIGN__CENTER",answer);
      return answer;
    }
    if (arg0.equals("TEXT_ALIGN__RIGHT")) {
      String answer = TEXT_ALIGN__RIGHT();
      cache.put("TEXT_ALIGN__RIGHT",answer);
      return answer;
    }
    if (arg0.equals("FONT_SIZE__XX_SMALL")) {
      String answer = FONT_SIZE__XX_SMALL();
      cache.put("FONT_SIZE__XX_SMALL",answer);
      return answer;
    }
    if (arg0.equals("FONT_SIZE__X_SMALL")) {
      String answer = FONT_SIZE__X_SMALL();
      cache.put("FONT_SIZE__X_SMALL",answer);
      return answer;
    }
    if (arg0.equals("FONT_SIZE__SMALL")) {
      String answer = FONT_SIZE__SMALL();
      cache.put("FONT_SIZE__SMALL",answer);
      return answer;
    }
    if (arg0.equals("FONT_SIZE__MEDIUM")) {
      String answer = FONT_SIZE__MEDIUM();
      cache.put("FONT_SIZE__MEDIUM",answer);
      return answer;
    }
    if (arg0.equals("FONT_SIZE__LARGE")) {
      String answer = FONT_SIZE__LARGE();
      cache.put("FONT_SIZE__LARGE",answer);
      return answer;
    }
    if (arg0.equals("FONT_SIZE__X_LARGE")) {
      String answer = FONT_SIZE__X_LARGE();
      cache.put("FONT_SIZE__X_LARGE",answer);
      return answer;
    }
    if (arg0.equals("FONT_SIZE__XX_LARGE")) {
      String answer = FONT_SIZE__XX_LARGE();
      cache.put("FONT_SIZE__XX_LARGE",answer);
      return answer;
    }
    if (arg0.equals("FONT_WEIGHT__NORMAL")) {
      String answer = FONT_WEIGHT__NORMAL();
      cache.put("FONT_WEIGHT__NORMAL",answer);
      return answer;
    }
    if (arg0.equals("FONT_WEIGHT__BOLD")) {
      String answer = FONT_WEIGHT__BOLD();
      cache.put("FONT_WEIGHT__BOLD",answer);
      return answer;
    }
    if (arg0.equals("FONT_WEIGHT__BOLDER")) {
      String answer = FONT_WEIGHT__BOLDER();
      cache.put("FONT_WEIGHT__BOLDER",answer);
      return answer;
    }
    if (arg0.equals("FONT_WEIGHT__LIGHTER")) {
      String answer = FONT_WEIGHT__LIGHTER();
      cache.put("FONT_WEIGHT__LIGHTER",answer);
      return answer;
    }
    if (arg0.equals("TEXT_DECORATION__NONE")) {
      String answer = TEXT_DECORATION__NONE();
      cache.put("TEXT_DECORATION__NONE",answer);
      return answer;
    }
    if (arg0.equals("TEXT_DECORATION__UNDERLINE")) {
      String answer = TEXT_DECORATION__UNDERLINE();
      cache.put("TEXT_DECORATION__UNDERLINE",answer);
      return answer;
    }
    if (arg0.equals("TEXT_DECORATION__OVERLINE")) {
      String answer = TEXT_DECORATION__OVERLINE();
      cache.put("TEXT_DECORATION__OVERLINE",answer);
      return answer;
    }
    if (arg0.equals("TEXT_DECORATION__LINE_THROUGH")) {
      String answer = TEXT_DECORATION__LINE_THROUGH();
      cache.put("TEXT_DECORATION__LINE_THROUGH",answer);
      return answer;
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants", arg0);
  }
  
  public java.lang.String[] getStringArray(java.lang.String arg0) {
    java.lang.String[] target = (java.lang.String[]) cache.get(arg0);
    if (target != null) {
      return target;
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants", arg0);
  }
  java.util.Map cache = new java.util.HashMap();
}
