package org.hibernate.validator;

public class ValidationMessages_ implements org.hibernate.validator.ValidationMessages {
  
  public java.lang.String javax_validation_constraints_AssertFalse_message() {
    return "must be false";
  }
  
  public java.lang.String javax_validation_constraints_AssertTrue_message() {
    return "must be true";
  }
  
  public java.lang.String javax_validation_constraints_DecimalMax_message() {
    return "must be less than or equal to {value}";
  }
  
  public java.lang.String javax_validation_constraints_DecimalMin_message() {
    return "must be greater than or equal to {value}";
  }
  
  public java.lang.String javax_validation_constraints_Digits_message() {
    return "numeric value out of bounds (<{integer} digits>.<{fraction} digits> expected)";
  }
  
  public java.lang.String javax_validation_constraints_Future_message() {
    return "must be in the future";
  }
  
  public java.lang.String javax_validation_constraints_Max_message() {
    return "must be less than or equal to {value}";
  }
  
  public java.lang.String javax_validation_constraints_Min_message() {
    return "must be greater than or equal to {value}";
  }
  
  public java.lang.String javax_validation_constraints_NotNull_message() {
    return "may not be null";
  }
  
  public java.lang.String javax_validation_constraints_Null_message() {
    return "must be null";
  }
  
  public java.lang.String javax_validation_constraints_Past_message() {
    return "must be in the past";
  }
  
  public java.lang.String javax_validation_constraints_Pattern_message() {
    return "must match \"{regexp}\"";
  }
  
  public java.lang.String javax_validation_constraints_Size_message() {
    return "size must be between {min} and {max}";
  }
  
  public java.lang.String org_hibernate_validator_constraints_CreditCardNumber_message() {
    return "invalid credit card number";
  }
  
  public java.lang.String org_hibernate_validator_constraints_Email_message() {
    return "not a well-formed email address";
  }
  
  public java.lang.String org_hibernate_validator_constraints_Length_message() {
    return "length must be between {min} and {max}";
  }
  
  public java.lang.String org_hibernate_validator_constraints_NotBlank_message() {
    return "may not be empty";
  }
  
  public java.lang.String org_hibernate_validator_constraints_NotEmpty_message() {
    return "may not be empty";
  }
  
  public java.lang.String org_hibernate_validator_constraints_Range_message() {
    return "must be between {min} and {max}";
  }
  
  public java.lang.String org_hibernate_validator_constraints_ScriptAssert_message() {
    return "script expression \"{script}\" didn't evaluate to true";
  }
  
  public java.lang.String org_hibernate_validator_constraints_URL_message() {
    return "must be a valid URL";
  }
  
  public boolean getBoolean(java.lang.String arg0) {
    Boolean target = (Boolean) cache.get(arg0);
    if (target != null) {
      return target.booleanValue();
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.hibernate.validator.ValidationMessages", arg0);
  }
  
  public double getDouble(java.lang.String arg0) {
    Double target = (Double) cache.get(arg0);
    if (target != null) {
      return target.doubleValue();
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.hibernate.validator.ValidationMessages", arg0);
  }
  
  public float getFloat(java.lang.String arg0) {
    Float target = (Float) cache.get(arg0);
    if (target != null) {
      return target.floatValue();
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.hibernate.validator.ValidationMessages", arg0);
  }
  
  public int getInt(java.lang.String arg0) {
    Integer target = (Integer) cache.get(arg0);
    if (target != null) {
      return target.intValue();
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.hibernate.validator.ValidationMessages", arg0);
  }
  
  public java.util.Map<java.lang.String, java.lang.String> getMap(java.lang.String arg0) {
    java.util.Map<java.lang.String, java.lang.String> target = (java.util.Map<java.lang.String, java.lang.String>) cache.get(arg0);
    if (target != null) {
      return target;
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.hibernate.validator.ValidationMessages", arg0);
  }
  
  public java.lang.String getString(java.lang.String arg0) {
    java.lang.String target = (java.lang.String) cache.get(arg0);
    if (target != null) {
      return target;
    }
    if (arg0.equals("javax_validation_constraints_AssertFalse_message")) {
      String answer = javax_validation_constraints_AssertFalse_message();
      cache.put("javax_validation_constraints_AssertFalse_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_AssertTrue_message")) {
      String answer = javax_validation_constraints_AssertTrue_message();
      cache.put("javax_validation_constraints_AssertTrue_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_DecimalMax_message")) {
      String answer = javax_validation_constraints_DecimalMax_message();
      cache.put("javax_validation_constraints_DecimalMax_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_DecimalMin_message")) {
      String answer = javax_validation_constraints_DecimalMin_message();
      cache.put("javax_validation_constraints_DecimalMin_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_Digits_message")) {
      String answer = javax_validation_constraints_Digits_message();
      cache.put("javax_validation_constraints_Digits_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_Future_message")) {
      String answer = javax_validation_constraints_Future_message();
      cache.put("javax_validation_constraints_Future_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_Max_message")) {
      String answer = javax_validation_constraints_Max_message();
      cache.put("javax_validation_constraints_Max_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_Min_message")) {
      String answer = javax_validation_constraints_Min_message();
      cache.put("javax_validation_constraints_Min_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_NotNull_message")) {
      String answer = javax_validation_constraints_NotNull_message();
      cache.put("javax_validation_constraints_NotNull_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_Null_message")) {
      String answer = javax_validation_constraints_Null_message();
      cache.put("javax_validation_constraints_Null_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_Past_message")) {
      String answer = javax_validation_constraints_Past_message();
      cache.put("javax_validation_constraints_Past_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_Pattern_message")) {
      String answer = javax_validation_constraints_Pattern_message();
      cache.put("javax_validation_constraints_Pattern_message",answer);
      return answer;
    }
    if (arg0.equals("javax_validation_constraints_Size_message")) {
      String answer = javax_validation_constraints_Size_message();
      cache.put("javax_validation_constraints_Size_message",answer);
      return answer;
    }
    if (arg0.equals("org_hibernate_validator_constraints_CreditCardNumber_message")) {
      String answer = org_hibernate_validator_constraints_CreditCardNumber_message();
      cache.put("org_hibernate_validator_constraints_CreditCardNumber_message",answer);
      return answer;
    }
    if (arg0.equals("org_hibernate_validator_constraints_Email_message")) {
      String answer = org_hibernate_validator_constraints_Email_message();
      cache.put("org_hibernate_validator_constraints_Email_message",answer);
      return answer;
    }
    if (arg0.equals("org_hibernate_validator_constraints_Length_message")) {
      String answer = org_hibernate_validator_constraints_Length_message();
      cache.put("org_hibernate_validator_constraints_Length_message",answer);
      return answer;
    }
    if (arg0.equals("org_hibernate_validator_constraints_NotBlank_message")) {
      String answer = org_hibernate_validator_constraints_NotBlank_message();
      cache.put("org_hibernate_validator_constraints_NotBlank_message",answer);
      return answer;
    }
    if (arg0.equals("org_hibernate_validator_constraints_NotEmpty_message")) {
      String answer = org_hibernate_validator_constraints_NotEmpty_message();
      cache.put("org_hibernate_validator_constraints_NotEmpty_message",answer);
      return answer;
    }
    if (arg0.equals("org_hibernate_validator_constraints_Range_message")) {
      String answer = org_hibernate_validator_constraints_Range_message();
      cache.put("org_hibernate_validator_constraints_Range_message",answer);
      return answer;
    }
    if (arg0.equals("org_hibernate_validator_constraints_ScriptAssert_message")) {
      String answer = org_hibernate_validator_constraints_ScriptAssert_message();
      cache.put("org_hibernate_validator_constraints_ScriptAssert_message",answer);
      return answer;
    }
    if (arg0.equals("org_hibernate_validator_constraints_URL_message")) {
      String answer = org_hibernate_validator_constraints_URL_message();
      cache.put("org_hibernate_validator_constraints_URL_message",answer);
      return answer;
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.hibernate.validator.ValidationMessages", arg0);
  }
  
  public java.lang.String[] getStringArray(java.lang.String arg0) {
    java.lang.String[] target = (java.lang.String[]) cache.get(arg0);
    if (target != null) {
      return target;
    }
    throw new java.util.MissingResourceException("Cannot find constant '" +arg0 + "'; expecting a method name", "org.hibernate.validator.ValidationMessages", arg0);
  }
  java.util.Map cache = new java.util.HashMap();
}
