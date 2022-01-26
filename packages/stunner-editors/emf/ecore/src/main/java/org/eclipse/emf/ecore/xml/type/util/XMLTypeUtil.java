/**
 * Copyright (c) 2004-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.xml.type.util;

 
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.xml.type.internal.DataValue;
import org.eclipse.emf.ecore.xml.type.internal.RegEx;


/**
 * This class contains convenient static methods for working with XML-related information.
 */
public final class XMLTypeUtil
{
  public static final int EQUALS = 0;
  public static final int LESS_THAN = -1;
  public static final int GREATER_THAN = 1;
  public static final int INDETERMINATE = 2;

  /*
  public static int compareCalendar(Object calendar1, Object calendar2)
  {
    switch (((XMLGregorianCalendar)calendar1).compare((XMLGregorianCalendar)calendar2))
    {
      case DatatypeConstants.EQUAL:
      {
        return EQUALS;
      }
      case DatatypeConstants.LESSER:
      {
        return LESS_THAN;
      }
      case DatatypeConstants.GREATER:
      {
        return GREATER_THAN;
      }
      default:
      {
        return INDETERMINATE;
      }
    }
  }

  public static int compareDuration(Object duration1, Object duration2)
  {
    switch (((Duration)duration1).compare((Duration)duration2))
    {
      case DatatypeConstants.EQUAL:
      {
        return EQUALS;
      }
      case DatatypeConstants.LESSER:
      {
        return LESS_THAN;
      }
      case DatatypeConstants.GREATER:
      {
        return GREATER_THAN;
      }
      default:
      {
        return INDETERMINATE;
      }
    }
  }
  */

  public static boolean isSpace(char value)
  {
    return DataValue.XMLChar.isSpace(value);
  }

  public static String normalize(String value, boolean collapse) 
  {
    if (value == null)
    {
      return null;
    }

    int length = value.length();
    if (length == 0)
    {
      return "";
    }

    char [] valueArray = new char[length];
    value.getChars(0, length, valueArray, 0);
    StringBuffer buffer = null;
    boolean skipSpace = collapse;
    for (int i = 0, offset = 0; i < length; i++) 
    {
      char c = valueArray[i];
      if (isSpace(c)) 
      {
        if (skipSpace)
        {
          if (buffer == null)
          {
            buffer = new StringBuffer(value);
          }
          buffer.deleteCharAt(i - offset++);
        }
        else 
        {
          skipSpace = collapse;
          if (c != ' ')
          {
            if (buffer == null)
            {
              buffer = new StringBuffer(value);
            }
            buffer.setCharAt(i - offset, ' ');
          }
        }
      }
      else 
      {
        skipSpace = false;
      }
    }

    if (skipSpace) 
    {
      if (buffer == null)
      {
        return value.substring(0, length - 1);
      }
      else 
      {
        length = buffer.length();
        if (length > 0)
        {
          return buffer.substring(0, length - 1);
        }
        else
        {
          return "";
        }
      }
    }
    else
    {
      if (buffer == null)
      {
        return value;
      }
      else
      {
        return buffer.toString();
      }
    }
  }

  public static EValidator.PatternMatcher createPatternMatcher(String pattern)
  {
    return new PatternMatcherImpl(pattern);
  }

  private static class PatternMatcherImpl implements EValidator.PatternMatcher
  {
    protected RegEx.RegularExpression regularExpression;

    public PatternMatcherImpl(String pattern)
    {
      regularExpression =  new RegEx.RegularExpression(pattern, "X");
    }

    public boolean matches(String value)
    {
      return regularExpression.matches(value);
    }

    @Override
    public String toString()
    {
      return regularExpression.getPattern();
    }
  }

  /**
   * Returns whether the code point is the valid start of an XML Name.
   */
  public static boolean isNameStart(int codePoint)
  {
    return DataValue.XMLChar.isNameStart(codePoint);
  }

  /**
   * Returns whether the code point is a valid part of an XML Name.
   */
  public static boolean isNamePart(int codePoint)
  {
    return DataValue.XMLChar.isName(codePoint);
  }

  /**
   * Returns whether the code point is the valid start of an XML NCName.
   */
  public static boolean isNCNameStart(int codePoint)
  {
    return DataValue.XMLChar.isNCNameStart(codePoint);
  }

  /**
   * Returns whether the code point is a valid part of an XML NCName.
   */
  public static boolean isNCNamePart(int codePoint)
  {
    return DataValue.XMLChar.isNCName(codePoint);
  }
}
