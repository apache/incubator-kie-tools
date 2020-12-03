package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.validation.client.ProviderValidationMessageResolver;
import com.google.gwt.validation.client.ValidationMessageResolver;
import com.google.gwt.validation.client.constraints.AssertFalseValidator;
import com.google.gwt.validation.client.constraints.AssertTrueValidator;
import com.google.gwt.validation.client.constraints.DecimalMaxValidatorForNumber;
import com.google.gwt.validation.client.constraints.DecimalMaxValidatorForString;
import com.google.gwt.validation.client.constraints.DigitsValidatorForNumber;
import com.google.gwt.validation.client.constraints.DigitsValidatorForString;
import com.google.gwt.validation.client.constraints.MinValidatorForString;
import com.google.gwt.validation.client.constraints.PastValidatorForDate;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfBoolean;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfByte;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfChar;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfDouble;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfFloat;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfInt;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfLong;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfObject;
import com.google.gwt.validation.client.constraints.SizeValidatorForArrayOfShort;
import com.google.gwt.validation.client.constraints.SizeValidatorForCollection;
import com.google.gwt.validation.client.constraints.SizeValidatorForString;
import com.google.gwt.validation.client.impl.ConstraintViolationImpl;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.inject.Singleton;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.impl.CreditCardNumberValidator;
import org.hibernate.validator.constraints.impl.DecimalMinValidatorForNumber;
import org.hibernate.validator.constraints.impl.DecimalMinValidatorForString;
import org.hibernate.validator.constraints.impl.EmailValidator;
import org.hibernate.validator.constraints.impl.FutureValidatorForCalendar;
import org.hibernate.validator.constraints.impl.FutureValidatorForDate;
import org.hibernate.validator.constraints.impl.LengthValidator;
import org.hibernate.validator.constraints.impl.MaxValidatorForNumber;
import org.hibernate.validator.constraints.impl.MaxValidatorForString;
import org.hibernate.validator.constraints.impl.MinValidatorForNumber;
import org.hibernate.validator.constraints.impl.NotBlankValidator;
import org.hibernate.validator.constraints.impl.NotNullValidator;
import org.hibernate.validator.constraints.impl.NullValidator;
import org.hibernate.validator.constraints.impl.PastValidatorForCalendar;
import org.hibernate.validator.constraints.impl.PatternValidator;
import org.hibernate.validator.constraints.impl.ScriptAssertValidator;
import org.hibernate.validator.constraints.impl.SizeValidatorForArray;
import org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfBoolean;
import org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfByte;
import org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfChar;
import org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfDouble;
import org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfFloat;
import org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfInt;
import org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfLong;
import org.hibernate.validator.constraints.impl.SizeValidatorForArraysOfShort;
import org.hibernate.validator.constraints.impl.SizeValidatorForMap;
import org.hibernate.validator.constraints.impl.URLValidator;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.jboss.errai.validation.client.dynamic.DynamicValidatorUtil;
import org.jboss.errai.validation.client.dynamic.GeneratedDynamicValidator;
import org.jboss.errai.validation.client.shared.GwtCompatibleEmail;
import org.jboss.errai.validation.client.shared.GwtCompatibleEmailValidator;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.validation.NoValidation;
import org.kie.workbench.common.dmn.api.validation.NoValidationValidator;

public class DynamicValidatorFactory extends Factory<DynamicValidator> {
  private ValidationMessageResolver messageResolver = GWT.create(ProviderValidationMessageResolver.class);
  public class DynamicOrgHibernateValidatorConstraintsImplPatternValidator extends PatternValidator implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Pattern() {
        public String regexp() {
          return (String) parameters.get("regexp");
        }
        public Flag[] flags() {
          return (Flag[]) parameters.get("flags");
        }
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Pattern.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplEmailValidator extends EmailValidator implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Email() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Email.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfDouble extends SizeValidatorForArrayOfDouble implements GeneratedDynamicValidator<double[]> { public Set validate(final Map parameters, final double[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfBoolean extends SizeValidatorForArraysOfBoolean implements GeneratedDynamicValidator<boolean[]> { public Set validate(final Map parameters, final boolean[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfInt extends SizeValidatorForArrayOfInt implements GeneratedDynamicValidator<int[]> { public Set validate(final Map parameters, final int[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplMaxValidatorForNumber extends MaxValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new Max() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public long value() {
          return (long) parameters.get("value");
        }
        public Class annotationType() {
          return Max.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfLong extends SizeValidatorForArrayOfLong implements GeneratedDynamicValidator<long[]> { public Set validate(final Map parameters, final long[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsAssertTrueValidator extends AssertTrueValidator implements GeneratedDynamicValidator<Boolean> { public Set validate(final Map parameters, final Boolean value) {
      initialize(new AssertTrue() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return AssertTrue.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplURLValidator extends URLValidator implements GeneratedDynamicValidator { public Set validate(final Map parameters, final Object value) {
      initialize(new URL() {
        public String protocol() {
          return (String) parameters.get("protocol");
        }
        public String host() {
          return (String) parameters.get("host");
        }
        public int port() {
          return (int) parameters.get("port");
        }
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return URL.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsAssertFalseValidator extends AssertFalseValidator implements GeneratedDynamicValidator<Boolean> { public Set validate(final Map parameters, final Boolean value) {
      initialize(new AssertFalse() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return AssertFalse.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplDecimalMinValidatorForString extends DecimalMinValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new DecimalMin() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public String value() {
          return (String) parameters.get("value");
        }
        public Class annotationType() {
          return DecimalMin.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfDouble extends SizeValidatorForArraysOfDouble implements GeneratedDynamicValidator<double[]> { public Set validate(final Map parameters, final double[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplPastValidatorForCalendar extends PastValidatorForCalendar implements GeneratedDynamicValidator { public Set validate(final Map parameters, final Object value) {
      initialize(new Past() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Past.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplNotNullValidator extends NotNullValidator implements GeneratedDynamicValidator { public Set validate(final Map parameters, final Object value) {
      initialize(new NotNull() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return NotNull.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsDecimalMinValidatorForString extends com.google.gwt.validation.client.constraints.DecimalMinValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new DecimalMin() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public String value() {
          return (String) parameters.get("value");
        }
        public Class annotationType() {
          return DecimalMin.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplFutureValidatorForCalendar extends FutureValidatorForCalendar implements GeneratedDynamicValidator { public Set validate(final Map parameters, final Object value) {
      initialize(new Future() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Future.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplNullValidator extends NullValidator implements GeneratedDynamicValidator { public Set validate(final Map parameters, final Object value) {
      initialize(new Null() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Null.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplDecimalMinValidatorForNumber extends DecimalMinValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new DecimalMin() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public String value() {
          return (String) parameters.get("value");
        }
        public Class annotationType() {
          return DecimalMin.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForCollection extends SizeValidatorForCollection implements GeneratedDynamicValidator<Collection> { public Set validate(final Map parameters, final Collection value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplMaxValidatorForString extends MaxValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Max() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public long value() {
          return (long) parameters.get("value");
        }
        public Class annotationType() {
          return Max.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForCollection extends org.hibernate.validator.constraints.impl.SizeValidatorForCollection implements GeneratedDynamicValidator<Collection> { public Set validate(final Map parameters, final Collection value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplScriptAssertValidator extends ScriptAssertValidator implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Email() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Email.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfChar extends SizeValidatorForArraysOfChar implements GeneratedDynamicValidator<char[]> { public Set validate(final Map parameters, final char[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsDecimalMinValidatorForNumber extends com.google.gwt.validation.client.constraints.DecimalMinValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new DecimalMin() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public String value() {
          return (String) parameters.get("value");
        }
        public Class annotationType() {
          return DecimalMin.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsPastValidatorForDate extends PastValidatorForDate implements GeneratedDynamicValidator<Date> { public Set validate(final Map parameters, final Date value) {
      initialize(new Past() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Past.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsNotNullValidator extends com.google.gwt.validation.client.constraints.NotNullValidator implements GeneratedDynamicValidator { public Set validate(final Map parameters, final Object value) {
      initialize(new NotNull() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return NotNull.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfByte extends SizeValidatorForArrayOfByte implements GeneratedDynamicValidator<byte[]> { public Set validate(final Map parameters, final byte[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgJbossErraiValidationClientSharedGwtCompatibleEmailValidator extends GwtCompatibleEmailValidator implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new GwtCompatibleEmail() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return GwtCompatibleEmail.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsDecimalMaxValidatorForString extends DecimalMaxValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new DecimalMax() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public String value() {
          return (String) parameters.get("value");
        }
        public Class annotationType() {
          return DecimalMax.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForMap extends SizeValidatorForMap implements GeneratedDynamicValidator<Map> { public Set validate(final Map parameters, final Map value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForString extends SizeValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsDigitsValidatorForString extends DigitsValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Digits() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int integer() {
          return (int) parameters.get("integer");
        }
        public int fraction() {
          return (int) parameters.get("fraction");
        }
        public Class annotationType() {
          return Digits.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfFloat extends SizeValidatorForArraysOfFloat implements GeneratedDynamicValidator<float[]> { public Set validate(final Map parameters, final float[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplNotBlankValidator extends NotBlankValidator implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new NotBlank() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return NotBlank.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplAssertTrueValidator extends org.hibernate.validator.constraints.impl.AssertTrueValidator implements GeneratedDynamicValidator<Boolean> { public Set validate(final Map parameters, final Boolean value) {
      initialize(new AssertTrue() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return AssertTrue.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplLengthValidator extends LengthValidator implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Length() {
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Length.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsDecimalMaxValidatorForNumber extends DecimalMaxValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new DecimalMax() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public String value() {
          return (String) parameters.get("value");
        }
        public Class annotationType() {
          return DecimalMax.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplFutureValidatorForDate extends FutureValidatorForDate implements GeneratedDynamicValidator<Date> { public Set validate(final Map parameters, final Date value) {
      initialize(new Future() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Future.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsFutureValidatorForDate extends com.google.gwt.validation.client.constraints.FutureValidatorForDate implements GeneratedDynamicValidator<Date> { public Set validate(final Map parameters, final Date value) {
      initialize(new Future() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Future.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgKieWorkbenchCommonDmnApiValidationNoValidationValidator extends NoValidationValidator implements GeneratedDynamicValidator<DMNDefinition> { public Set validate(final Map parameters, final DMNDefinition value) {
      initialize(new NoValidation() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return NoValidation.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArray extends SizeValidatorForArray implements GeneratedDynamicValidator<Object[]> { public Set validate(final Map parameters, final Object[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsMinValidatorForString extends MinValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Min() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public long value() {
          return (long) parameters.get("value");
        }
        public Class annotationType() {
          return Min.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfBoolean extends SizeValidatorForArrayOfBoolean implements GeneratedDynamicValidator<boolean[]> { public Set validate(final Map parameters, final boolean[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsDigitsValidatorForNumber extends DigitsValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new Digits() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int integer() {
          return (int) parameters.get("integer");
        }
        public int fraction() {
          return (int) parameters.get("fraction");
        }
        public Class annotationType() {
          return Digits.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfShort extends SizeValidatorForArraysOfShort implements GeneratedDynamicValidator<short[]> { public Set validate(final Map parameters, final short[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfLong extends SizeValidatorForArraysOfLong implements GeneratedDynamicValidator<long[]> { public Set validate(final Map parameters, final long[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsPatternValidator extends com.google.gwt.validation.client.constraints.PatternValidator implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Pattern() {
        public String regexp() {
          return (String) parameters.get("regexp");
        }
        public Flag[] flags() {
          return (Flag[]) parameters.get("flags");
        }
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Pattern.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfShort extends SizeValidatorForArrayOfShort implements GeneratedDynamicValidator<short[]> { public Set validate(final Map parameters, final short[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForMap extends com.google.gwt.validation.client.constraints.SizeValidatorForMap implements GeneratedDynamicValidator<Map> { public Set validate(final Map parameters, final Map value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsNullValidator extends com.google.gwt.validation.client.constraints.NullValidator implements GeneratedDynamicValidator { public Set validate(final Map parameters, final Object value) {
      initialize(new Null() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Null.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfChar extends SizeValidatorForArrayOfChar implements GeneratedDynamicValidator<char[]> { public Set validate(final Map parameters, final char[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplPastValidatorForDate extends org.hibernate.validator.constraints.impl.PastValidatorForDate implements GeneratedDynamicValidator<Date> { public Set validate(final Map parameters, final Date value) {
      initialize(new Past() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return Past.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsMaxValidatorForNumber extends com.google.gwt.validation.client.constraints.MaxValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new Max() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public long value() {
          return (long) parameters.get("value");
        }
        public Class annotationType() {
          return Max.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplCreditCardNumberValidator extends CreditCardNumberValidator implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new CreditCardNumber() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return CreditCardNumber.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplDecimalMaxValidatorForString extends org.hibernate.validator.constraints.impl.DecimalMaxValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new DecimalMax() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public String value() {
          return (String) parameters.get("value");
        }
        public Class annotationType() {
          return DecimalMax.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsMaxValidatorForString extends com.google.gwt.validation.client.constraints.MaxValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Max() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public long value() {
          return (long) parameters.get("value");
        }
        public Class annotationType() {
          return Max.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplMinValidatorForString extends org.hibernate.validator.constraints.impl.MinValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Min() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public long value() {
          return (long) parameters.get("value");
        }
        public Class annotationType() {
          return Min.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplMinValidatorForNumber extends MinValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new Min() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public long value() {
          return (long) parameters.get("value");
        }
        public Class annotationType() {
          return Min.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplDecimalMaxValidatorForNumber extends org.hibernate.validator.constraints.impl.DecimalMaxValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new DecimalMax() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public String value() {
          return (String) parameters.get("value");
        }
        public Class annotationType() {
          return DecimalMax.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfByte extends SizeValidatorForArraysOfByte implements GeneratedDynamicValidator<byte[]> { public Set validate(final Map parameters, final byte[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsMinValidatorForNumber extends com.google.gwt.validation.client.constraints.MinValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new Min() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public long value() {
          return (long) parameters.get("value");
        }
        public Class annotationType() {
          return Min.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplDigitsValidatorForString extends org.hibernate.validator.constraints.impl.DigitsValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Digits() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int integer() {
          return (int) parameters.get("integer");
        }
        public int fraction() {
          return (int) parameters.get("fraction");
        }
        public Class annotationType() {
          return Digits.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForString extends org.hibernate.validator.constraints.impl.SizeValidatorForString implements GeneratedDynamicValidator<String> { public Set validate(final Map parameters, final String value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfInt extends SizeValidatorForArraysOfInt implements GeneratedDynamicValidator<int[]> { public Set validate(final Map parameters, final int[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplAssertFalseValidator extends org.hibernate.validator.constraints.impl.AssertFalseValidator implements GeneratedDynamicValidator<Boolean> { public Set validate(final Map parameters, final Boolean value) {
      initialize(new AssertFalse() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public Class annotationType() {
          return AssertFalse.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfObject extends SizeValidatorForArrayOfObject implements GeneratedDynamicValidator<Object[]> { public Set validate(final Map parameters, final Object[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicOrgHibernateValidatorConstraintsImplDigitsValidatorForNumber extends org.hibernate.validator.constraints.impl.DigitsValidatorForNumber implements GeneratedDynamicValidator<Number> { public Set validate(final Map parameters, final Number value) {
      initialize(new Digits() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int integer() {
          return (int) parameters.get("integer");
        }
        public int fraction() {
          return (int) parameters.get("fraction");
        }
        public Class annotationType() {
          return Digits.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  public class DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfFloat extends SizeValidatorForArrayOfFloat implements GeneratedDynamicValidator<float[]> { public Set validate(final Map parameters, final float[] value) {
      initialize(new Size() {
        public String message() {
          return (String) parameters.get("message");
        }
        public Class[] groups() {
          return (Class[]) parameters.get("groups");
        }
        public Class[] payload() {
          return (Class[]) parameters.get("payload");
        }
        public int min() {
          return (int) parameters.get("min");
        }
        public int max() {
          return (int) parameters.get("max");
        }
        public Class annotationType() {
          return Size.class;
        }
      });
      if (isValid(value, (ConstraintValidatorContext) null)) {
        return Collections.emptySet();
      } else {
        String paramMessage = (String) parameters.get("message");
        paramMessage = paramMessage != null ? paramMessage.replaceAll("{", "").replaceAll("}", "") : "";
        final String message = messageResolver.get(paramMessage);
        return Collections.singleton(ConstraintViolationImpl.builder().setInvalidValue(value).setMessage(DynamicValidatorUtil.interpolateMessage(parameters, message)).build());
      }
    }
  }
  private class DynamicValidatorFactoryProxyImpl extends DynamicValidator implements Proxy<DynamicValidator> {
    private final ProxyHelper<DynamicValidator> proxyHelper = new ProxyHelperImpl<DynamicValidator>("DynamicValidatorFactory");
    public void initProxyProperties(final DynamicValidator instance) {

    }

    public DynamicValidator asBeanType() {
      return this;
    }

    public void setInstance(final DynamicValidator instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void addValidator(String constraint, String valueType, GeneratedDynamicValidator validator) {
      if (proxyHelper != null) {
        final DynamicValidator proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addValidator(constraint, valueType, validator);
      } else {
        super.addValidator(constraint, valueType, validator);
      }
    }

    @Override public Set validate(Class constraint, Map parameters, Object value) {
      if (proxyHelper != null) {
        final DynamicValidator proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.validate(constraint, parameters, value);
        return retVal;
      } else {
        return super.validate(constraint, parameters, value);
      }
    }

    @Override public Set validate(String constraint, Map parameters, Object value) {
      if (proxyHelper != null) {
        final DynamicValidator proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.validate(constraint, parameters, value);
        return retVal;
      } else {
        return super.validate(constraint, parameters, value);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DynamicValidator proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public DynamicValidatorFactory() {
    super(new FactoryHandleImpl(DynamicValidator.class, "DynamicValidatorFactory", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DynamicValidator.class, Object.class });
  }

  public DynamicValidator createInstance(final ContextManager contextManager) {
    final DynamicValidator dynamicValidator = new DynamicValidator();
    dynamicValidator.addValidator("javax.validation.constraints.Pattern", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplPatternValidator());
    dynamicValidator.addValidator("org.hibernate.validator.constraints.Email", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplEmailValidator());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[D", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfDouble());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[Z", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfBoolean());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[I", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfInt());
    dynamicValidator.addValidator("javax.validation.constraints.Max", "java.lang.Number", new DynamicOrgHibernateValidatorConstraintsImplMaxValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[J", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfLong());
    dynamicValidator.addValidator("javax.validation.constraints.AssertTrue", "java.lang.Boolean", new DynamicComGoogleGwtValidationClientConstraintsAssertTrueValidator());
    dynamicValidator.addValidator("org.hibernate.validator.constraints.URL", "java.lang.Object", new DynamicOrgHibernateValidatorConstraintsImplURLValidator());
    dynamicValidator.addValidator("javax.validation.constraints.AssertFalse", "java.lang.Boolean", new DynamicComGoogleGwtValidationClientConstraintsAssertFalseValidator());
    dynamicValidator.addValidator("javax.validation.constraints.DecimalMin", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplDecimalMinValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[D", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfDouble());
    dynamicValidator.addValidator("javax.validation.constraints.Past", "java.lang.Object", new DynamicOrgHibernateValidatorConstraintsImplPastValidatorForCalendar());
    dynamicValidator.addValidator("javax.validation.constraints.NotNull", "java.lang.Object", new DynamicOrgHibernateValidatorConstraintsImplNotNullValidator());
    dynamicValidator.addValidator("javax.validation.constraints.DecimalMin", "java.lang.String", new DynamicComGoogleGwtValidationClientConstraintsDecimalMinValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Future", "java.lang.Object", new DynamicOrgHibernateValidatorConstraintsImplFutureValidatorForCalendar());
    dynamicValidator.addValidator("javax.validation.constraints.Null", "java.lang.Object", new DynamicOrgHibernateValidatorConstraintsImplNullValidator());
    dynamicValidator.addValidator("javax.validation.constraints.DecimalMin", "java.lang.Number", new DynamicOrgHibernateValidatorConstraintsImplDecimalMinValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "java.util.Collection", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForCollection());
    dynamicValidator.addValidator("javax.validation.constraints.Max", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplMaxValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "java.util.Collection", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForCollection());
    dynamicValidator.addValidator("org.hibernate.validator.constraints.Email", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplScriptAssertValidator());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[C", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfChar());
    dynamicValidator.addValidator("javax.validation.constraints.DecimalMin", "java.lang.Number", new DynamicComGoogleGwtValidationClientConstraintsDecimalMinValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.Past", "java.util.Date", new DynamicComGoogleGwtValidationClientConstraintsPastValidatorForDate());
    dynamicValidator.addValidator("javax.validation.constraints.NotNull", "java.lang.Object", new DynamicComGoogleGwtValidationClientConstraintsNotNullValidator());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[B", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfByte());
    dynamicValidator.addValidator("org.jboss.errai.validation.client.shared.GwtCompatibleEmail", "java.lang.String", new DynamicOrgJbossErraiValidationClientSharedGwtCompatibleEmailValidator());
    dynamicValidator.addValidator("javax.validation.constraints.DecimalMax", "java.lang.String", new DynamicComGoogleGwtValidationClientConstraintsDecimalMaxValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "java.util.Map", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForMap());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "java.lang.String", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Digits", "java.lang.String", new DynamicComGoogleGwtValidationClientConstraintsDigitsValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[F", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfFloat());
    dynamicValidator.addValidator("org.hibernate.validator.constraints.NotBlank", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplNotBlankValidator());
    dynamicValidator.addValidator("javax.validation.constraints.AssertTrue", "java.lang.Boolean", new DynamicOrgHibernateValidatorConstraintsImplAssertTrueValidator());
    dynamicValidator.addValidator("org.hibernate.validator.constraints.Length", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplLengthValidator());
    dynamicValidator.addValidator("javax.validation.constraints.DecimalMax", "java.lang.Number", new DynamicComGoogleGwtValidationClientConstraintsDecimalMaxValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.Future", "java.util.Date", new DynamicOrgHibernateValidatorConstraintsImplFutureValidatorForDate());
    dynamicValidator.addValidator("javax.validation.constraints.Future", "java.util.Date", new DynamicComGoogleGwtValidationClientConstraintsFutureValidatorForDate());
    dynamicValidator.addValidator("org.kie.workbench.common.dmn.api.validation.NoValidation", "org.kie.workbench.common.dmn.api.definition.DMNDefinition", new DynamicOrgKieWorkbenchCommonDmnApiValidationNoValidationValidator());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[Ljava.lang.Object;", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArray());
    dynamicValidator.addValidator("javax.validation.constraints.Min", "java.lang.String", new DynamicComGoogleGwtValidationClientConstraintsMinValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[Z", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfBoolean());
    dynamicValidator.addValidator("javax.validation.constraints.Digits", "java.lang.Number", new DynamicComGoogleGwtValidationClientConstraintsDigitsValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[S", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfShort());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[J", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfLong());
    dynamicValidator.addValidator("javax.validation.constraints.Pattern", "java.lang.String", new DynamicComGoogleGwtValidationClientConstraintsPatternValidator());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[S", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfShort());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "java.util.Map", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForMap());
    dynamicValidator.addValidator("javax.validation.constraints.Null", "java.lang.Object", new DynamicComGoogleGwtValidationClientConstraintsNullValidator());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[C", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfChar());
    dynamicValidator.addValidator("javax.validation.constraints.Past", "java.util.Date", new DynamicOrgHibernateValidatorConstraintsImplPastValidatorForDate());
    dynamicValidator.addValidator("javax.validation.constraints.Max", "java.lang.Number", new DynamicComGoogleGwtValidationClientConstraintsMaxValidatorForNumber());
    dynamicValidator.addValidator("org.hibernate.validator.constraints.CreditCardNumber", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplCreditCardNumberValidator());
    dynamicValidator.addValidator("javax.validation.constraints.DecimalMax", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplDecimalMaxValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Max", "java.lang.String", new DynamicComGoogleGwtValidationClientConstraintsMaxValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Min", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplMinValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Min", "java.lang.Number", new DynamicOrgHibernateValidatorConstraintsImplMinValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.DecimalMax", "java.lang.Number", new DynamicOrgHibernateValidatorConstraintsImplDecimalMaxValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[B", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfByte());
    dynamicValidator.addValidator("javax.validation.constraints.Min", "java.lang.Number", new DynamicComGoogleGwtValidationClientConstraintsMinValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.Digits", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplDigitsValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "java.lang.String", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForString());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[I", new DynamicOrgHibernateValidatorConstraintsImplSizeValidatorForArraysOfInt());
    dynamicValidator.addValidator("javax.validation.constraints.AssertFalse", "java.lang.Boolean", new DynamicOrgHibernateValidatorConstraintsImplAssertFalseValidator());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[Ljava.lang.Object;", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfObject());
    dynamicValidator.addValidator("javax.validation.constraints.Digits", "java.lang.Number", new DynamicOrgHibernateValidatorConstraintsImplDigitsValidatorForNumber());
    dynamicValidator.addValidator("javax.validation.constraints.Size", "[F", new DynamicComGoogleGwtValidationClientConstraintsSizeValidatorForArrayOfFloat());
    return dynamicValidator;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DynamicValidator> proxyImpl = new DynamicValidatorFactoryProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}