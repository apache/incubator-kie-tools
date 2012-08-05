/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.drools.guvnor.client.util;

/**
 * The maven gwt:test mojo treats any output to stderr as
 * a test failure. gwt-log does dump some information there,
 * hence we need to proxy log invocation and be able to disable them at all.
 * <p/>
 * If you want to test the application, make sure to lauch it using
 * {@link org.jboss.bpm.console.client.Application#onModuleLoad2()}.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class ConsoleLog
{
  private static boolean enabled = true; // see javadoc comments

  public static void warn(String msg)
  {
    //if (enabled)
      //JLIU: TODO
      //com.allen_sauer.gwt.log.client.Log.warn(msg);
  }

  public static void info(String msg)
  {
    //if (enabled)
        //JLIU: TODO
      //com.allen_sauer.gwt.log.client.Log.info(msg);
  }

  public static void debug(String msg)
  {
    //if (enabled)
        //JLIU: TODO
      //com.allen_sauer.gwt.log.client.Log.debug(msg);
  }

  public static void error(String msg)
  {
    //if (enabled)
        //JLIU: TODO
      //com.allen_sauer.gwt.log.client.Log.error(msg);
  }

  public static void error(String msg, Throwable t)
  {
    //if (enabled)
        //JLIU: TODO
      //com.allen_sauer.gwt.log.client.Log.error(msg, t);
  }

  public static void setUncaughtExceptionHandler()
  {
    //if (enabled)
        //JLIU: TODO
      //com.allen_sauer.gwt.log.client.Log.setUncaughtExceptionHandler();
  }

  public static boolean isEnabled()
  {
    return enabled;
  }

  public static void setEnabled(boolean enabled)
  {
    ConsoleLog.enabled = enabled;
  }
}
