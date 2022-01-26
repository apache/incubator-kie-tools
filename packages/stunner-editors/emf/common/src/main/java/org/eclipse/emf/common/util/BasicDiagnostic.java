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
package org.eclipse.emf.common.util;

import java.util.Collections;
import java.util.List;

/**
 * A basic implementation of a diagnostic that that also acts as a chain.
 */
public class BasicDiagnostic implements Diagnostic, DiagnosticChain
{
  /**
   * The severity.
   * @see #getSeverity
   */
  protected int severity;

  /**
   * The message.
   * @see #getMessage
   */
  protected String message;

  /**
   * The message.
   * @see #getMessage
   */
  protected List<Diagnostic> children;

  /**
   * The data.
   * @see #getData
   */
  protected List<?> data;

  /**
   * The source.
   * @see #getSource
   */
  protected String source;

  /**
   * The code.
   * @see #getCode
   */
  protected int code;
  
  /**
   * Default Constructor (no initialization for local parameters)
   */
  public BasicDiagnostic()
  {
    super();
  }

  public BasicDiagnostic(String source, int code, String message, Object[] data) 
  {
    this.source = source;
    this.code = code;
    this.message = message;
    this.data = dataAsList(data);
  }

  public BasicDiagnostic(int severity, String source, int code, String message, Object[] data)
  {
    this(source, code, message, data);
    this.severity = severity;
  }

  public BasicDiagnostic(String source, int code, List<? extends Diagnostic> children, String message, Object[] data)
  {
    this(source, code, message, data);
    if (children != null)
    {
      for (Diagnostic diagnostic : children)
      {
        add(diagnostic);
      }
    }
  }

  protected List<?> dataAsList(Object [] data)
  {
    if (data == null)
    {
      return Collections.EMPTY_LIST;
    }
    else
    {
      Object [] copy = new Object [data.length];
      System.arraycopy(data, 0, copy, 0, data.length);
      return new BasicEList.UnmodifiableEList<Object>(copy.length, copy);
    }
  }
  
  protected void setSeverity(int severity)
  {
    this.severity = severity;
  }

  public int getSeverity()
  {
    return severity;
  }

  public String getMessage()
  {
    return message;
  }

  public List<?> getData()
  {
    return data;
  }

  public List<Diagnostic> getChildren()
  {
    if (children == null)
    {
      return Collections.emptyList();
    }
    else
    {
      return Collections.unmodifiableList(children);
    }
  }

  protected void setSource(String source)
  {
    this.source = source;
  }

  public String getSource()
  {
    return source;
  }
  
  protected void setCode(int code)
  {
    this.code = code;
  }

  public int getCode()
  {
    return code;
  }

  public void add(Diagnostic diagnostic)
  {
    if (children == null)
    {
      children = new BasicEList<Diagnostic>();
    }

    children.add(diagnostic);
    int childSeverity = diagnostic.getSeverity();
    if (childSeverity > getSeverity())
    {
      severity = childSeverity;
    }
  }

  public void addAll(Diagnostic diagnostic)
  {
    for (Diagnostic child : diagnostic.getChildren())
    {
      add(child);
    }
  }

  public void merge(Diagnostic diagnostic)
  {
    if (diagnostic.getChildren().isEmpty())
    {
      add(diagnostic);
    }
    else
    {
      addAll(diagnostic);
    }
  }

  public int recomputeSeverity()
  {
    if (children != null)
    {
      severity = OK;
      for (Diagnostic child : children)
      {
        int childSeverity = child instanceof BasicDiagnostic ? ((BasicDiagnostic)child).recomputeSeverity() : child.getSeverity();
        if (childSeverity > severity)
        {
          severity = childSeverity;
        }
      }
    }

    return severity;
  }
  
  /**
   * Returns the first throwable object available in the {@link #data} list, 
   * which is set when this diagnostic is instantiated.
   */
  public Throwable getException()
  {
    List<?> data = getData();
    if (data != null)
    {
      for (Object datum : data)
      {
        if (datum instanceof Throwable)
        {
          return (Throwable)datum;
        }
      }
    }
    return null;
  }
  
  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder();
    result.append("Diagnostic ");
    switch (severity)
    {
      case OK: 
      {
        result.append("OK"); 
        break;
      }
      case INFO: 
      {
        result.append("INFO"); 
        break;
      }
      case WARNING: 
      {
        result.append("WARNING"); 
        break;
      }
      case ERROR: 
      {
        result.append("ERROR"); 
        break;
      }
      case CANCEL: 
      {
        result.append("CANCEL"); 
        break;
      }
      default:
      {
        result.append(Integer.toHexString(severity));
        break;
      }
    }

    result.append(" source="); 
    result.append(source);

    result.append(" code="); 
    result.append(code);

    result.append(' ');
    result.append(message);

    if (data != null)
    {
      result.append(" data=");
      result.append(data);
    }
    if (children != null)
    {
      result.append(' ');
      result.append(children);
    }

    return result.toString();
  }  


  /**
   * Returns the throwable viewed as a {@link Diagnostic}.
   * 
   * @param throwable
   * @return {@link Diagnostic}
   */
  public static Diagnostic toDiagnostic(Throwable throwable)
  {
    if (throwable instanceof DiagnosticException)
    {
      return ((DiagnosticException)throwable).getDiagnostic();
    }
    else if (throwable instanceof WrappedException)
    {
      return toDiagnostic(throwable.getCause());
    }
    
    String message = throwable.getClass().getName();
    int index = message.lastIndexOf('.');
    if (index >= 0)
    {
      message = message.substring(index + 1);
    }
    if (throwable.getLocalizedMessage() != null)
    {
      message = message + ": " + throwable.getLocalizedMessage();
    }
    
    BasicDiagnostic basicDiagnostic = 
      new BasicDiagnostic
        (Diagnostic.ERROR,
         "org.eclipse.emf.common", 
         0, 
         message,
         new Object[] { throwable });
    
    if (throwable.getCause() != null && throwable.getCause() != throwable)
    {
      throwable = throwable.getCause();
      basicDiagnostic.add(toDiagnostic(throwable));
    }
    
    return basicDiagnostic;
  }
}
