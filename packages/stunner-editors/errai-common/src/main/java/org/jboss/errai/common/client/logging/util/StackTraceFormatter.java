package org.jboss.errai.common.client.logging.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Formats stack traces so that the Google Chrome web console will translate the line numbers with source maps.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class StackTraceFormatter {

  private StackTraceFormatter() {}

  public static List<String> getStackTraces(final Throwable error) {
    final List<Throwable> causes = new ArrayList<>();
    Throwable cur = error.getCause();
    while (cur != null) {
      causes.add(cur);
      cur = cur.getCause();
    }

    final List<String> stacks = new ArrayList<>();

    final String errorStack = getNativeStack(error);
    if (errorStack != null) {
      // Important: As late as Google Chrome 48, only stack traces starting with 'Error: ' get translated.
      stacks.add("Error: " + getNameAndMessage(error) + errorStack);
      for (final Throwable t : causes) {
        final String rawStack = getNativeStack(t);
        stacks.add("Error caused by: " + getNameAndMessage(t) + rawStack);
      }
    }
    else {
      stacks.add(getEmulatedStack(error));
    }

    return stacks;
  }

  private static String getEmulatedStack(final Throwable t) {
    String stack;
    final StringBuilder builder = new StringBuilder();
    t.printStackTrace(new PrintStream((OutputStream) null) {
      @Override
      public void println(final String x) {
        builder.append(x).append('\n');
      }
    });

    stack = builder.toString();
    return stack;
  }

  private static String getNativeStack(final Throwable t) {
    return maybeGetJSError(t)
            .map(jsError -> {
              String stack = (String) ReflectableJSO.create(jsError).get("stack");

              stack = stack.substring(stack.indexOf('\n'));
              return stack;
            }).orElse(null);
  }

  private static Optional<Object> maybeGetJSError( final Throwable t ) {
    final ReflectableJSO reflectable = ReflectableJSO.create(t);
    return Arrays
      .stream(reflectable.properties())
      .map(key -> reflectable.get(key))
      .filter(obj -> obj != null)
      .map(prop -> ReflectableJSO.create(prop))
      .filter(prop -> prop.get("name") != null && prop.get("message") != null)
      .map(prop -> prop.unwrap())
      .findFirst();
  }

  private static String getNameAndMessage(final Throwable t) {
    return t.getClass().getSimpleName() + ": " + t.getMessage();
  }

}
