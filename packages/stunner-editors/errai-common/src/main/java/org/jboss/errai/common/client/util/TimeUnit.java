/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package org.jboss.errai.common.client.util;

/**
 * A <tt>TimeUnit</tt> represents time durations at a given unit of
 * granularity and provides utility methods to convert across units,
 * and to perform timing and delay operations in these units.  A
 * <tt>TimeUnit</tt> does not maintain time information, but only
 * helps organize and use time representations that may be maintained
 * separately across various contexts.  A nanosecond is defined as one
 * thousandth of a microsecond, a microsecond as one thousandth of a
 * millisecond, a millisecond as one thousandth of a second, a minute
 * as sixty seconds, an hour as sixty minutes, and a day as twenty four
 * hours.
 * <p/>
 * <p>A <tt>TimeUnit</tt> is mainly used to inform time-based methods
 * how a given timing parameter should be interpreted. For example,
 * the following code will timeout in 50 milliseconds if the {@link
 * java.util.concurrent.locks.Lock lock} is not available:
 * <p/>
 * <pre>  Lock lock = ...;
 *  if ( lock.tryLock(50L, TimeUnit.MILLISECONDS) ) ...
 * </pre>
 * while this code will timeout in 50 seconds:
 * <pre>
 *  Lock lock = ...;
 *  if ( lock.tryLock(50L, TimeUnit.SECONDS) ) ...
 * </pre>
 * <p/>
 * Note however, that there is no guarantee that a particular timeout
 * implementation will be able to notice the passage of time at the
 * same granularity as the given <tt>TimeUnit</tt>.
 * <p/>
 * This implementation has been modified to work within the GWT client
 * emulation layer.
 *
 * @author Doug Lea
 * @since 1.5
 */
public enum TimeUnit {
  NANOSECONDS {
    public long toNanos(final long d) {
      return d;
    }

    public long toMicros(final long d) {
      return d / (C1);
    }

    public long toMillis(final long d) {
      return d / (C2);
    }

    public long toSeconds(final long d) {
      return d / (C3);
    }

    public long toMinutes(final long d) {
      return d / (C4);
    }

    public long toHours(final long d) {
      return d / (C5);
    }

    public long toDays(final long d) {
      return d / (C6);
    }

    public long convert(final long d, final TimeUnit u) {
      return u.toNanos(d);
    }

    int excessNanos(final long d, final long m) {
      return (int) (d - (m * C2));
    }
  },
  MICROSECONDS {
    public long toNanos(final long d) {
      return x(d, C1, Long.MAX_VALUE / (C1));
    }

    public long toMicros(final long d) {
      return d;
    }

    public long toMillis(final long d) {
      return d / (C2 / C1);
    }

    public long toSeconds(final long d) {
      return d / (C3 / C1);
    }

    public long toMinutes(final long d) {
      return d / (C4 / C1);
    }

    public long toHours(final long d) {
      return d / (C5 / C1);
    }

    public long toDays(final long d) {
      return d / (C6 / C1);
    }

    public long convert(final long d, final TimeUnit u) {
      return u.toMicros(d);
    }

    int excessNanos(final long d, final long m) {
      return (int) ((d * C1) - (m * C2));
    }
  },
  MILLISECONDS {
    public long toNanos(final long d) {
      return x(d, C2, Long.MAX_VALUE / (C2));
    }

    public long toMicros(final long d) {
      return x(d, C2 / C1, Long.MAX_VALUE / (C2 / C1));
    }

    public long toMillis(final long d) {
      return d;
    }

    public long toSeconds(final long d) {
      return d / (C3 / C2);
    }

    public long toMinutes(final long d) {
      return d / (C4 / C2);
    }

    public long toHours(final long d) {
      return d / (C5 / C2);
    }

    public long toDays(final long d) {
      return d / (C6 / C2);
    }

    public long convert(final long d, final TimeUnit u) {
      return u.toMillis(d);
    }

    int excessNanos(final long d, final long m) {
      return 0;
    }
  },
  SECONDS {
    public long toNanos(final long d) {
      return x(d, C3, Long.MAX_VALUE / (C3));
    }

    public long toMicros(final long d) {
      return x(d, C3 / C1, Long.MAX_VALUE / (C3 / C1));
    }

    public long toMillis(final long d) {
      return x(d, C3 / C2, Long.MAX_VALUE / (C3 / C2));
    }

    public long toSeconds(final long d) {
      return d;
    }

    public long toMinutes(final long d) {
      return d / (C4 / C3);
    }

    public long toHours(final long d) {
      return d / (C5 / C3);
    }

    public long toDays(final long d) {
      return d / (C6 / C3);
    }

    public long convert(final long d, final TimeUnit u) {
      return u.toSeconds(d);
    }

    int excessNanos(final long d, final long m) {
      return 0;
    }
  },
  MINUTES {
    public long toNanos(final long d) {
      return x(d, C4, Long.MAX_VALUE / (C4));
    }

    public long toMicros(final long d) {
      return x(d, C4 / C1, Long.MAX_VALUE / (C4 / C1));
    }

    public long toMillis(final long d) {
      return x(d, C4 / C2, Long.MAX_VALUE / (C4 / C2));
    }

    public long toSeconds(final long d) {
      return x(d, C4 / C3, Long.MAX_VALUE / (C4 / C3));
    }

    public long toMinutes(final long d) {
      return d;
    }

    public long toHours(final long d) {
      return d / (C5 / C4);
    }

    public long toDays(final long d) {
      return d / (C6 / C4);
    }

    public long convert(final long d, final TimeUnit u) {
      return u.toMinutes(d);
    }

    int excessNanos(final long d, final long m) {
      return 0;
    }
  },
  HOURS {
    public long toNanos(final long d) {
      return x(d, C5, Long.MAX_VALUE / (C5));
    }

    public long toMicros(final long d) {
      return x(d, C5 / C1, Long.MAX_VALUE / (C5 / C1));
    }

    public long toMillis(final long d) {
      return x(d, C5 / C2, Long.MAX_VALUE / (C5 / C2));
    }

    public long toSeconds(final long d) {
      return x(d, C5 / C3, Long.MAX_VALUE / (C5 / C3));
    }

    public long toMinutes(final long d) {
      return x(d, C5 / C4, Long.MAX_VALUE / (C5 / C4));
    }

    public long toHours(final long d) {
      return d;
    }

    public long toDays(final long d) {
      return d / (C6 / C5);
    }

    public long convert(final long d, final TimeUnit u) {
      return u.toHours(d);
    }

    int excessNanos(final long d, final long m) {
      return 0;
    }
  },
  DAYS {
    public long toNanos(final long d) {
      return x(d, C6, Long.MAX_VALUE / (C6));
    }

    public long toMicros(final long d) {
      return x(d, C6 / C1, Long.MAX_VALUE / (C6 / C1));
    }

    public long toMillis(final long d) {
      return x(d, C6 / C2, Long.MAX_VALUE / (C6 / C2));
    }

    public long toSeconds(final long d) {
      return x(d, C6 / C3, Long.MAX_VALUE / (C6 / C3));
    }

    public long toMinutes(final long d) {
      return x(d, C6 / C4, Long.MAX_VALUE / (C6 / C4));
    }

    public long toHours(final long d) {
      return x(d, C6 / C5, Long.MAX_VALUE / (C6 / C5));
    }

    public long toDays(final long d) {
      return d;
    }

    public long convert(final long d, final TimeUnit u) {
      return u.toDays(d);
    }

    int excessNanos(final long d, final long m) {
      return 0;
    }
  };

  // Handy constants for conversion methods
  static final long C1 = 1000L;
  static final long C2 = C1 * 1000L;
  static final long C3 = C2 * 1000L;
  static final long C4 = C3 * 60L;
  static final long C5 = C4 * 60L;
  static final long C6 = C5 * 24L;


  /**
   * Scale d by m, checking for overflow.
   * This has a short name to make above code more readable.
   */
  static long x(final long d, final long m, final long over) {
    if (d > over) return Long.MAX_VALUE;
    if (d < -over) return Long.MIN_VALUE;
    return d * m;
  }

  // To maintain full signature compatibility with 1.5, and to improve the
  // clarity of the generated javadoc (see 6287639: Abstract methods in
  // enum classes should not be listed as abstract), method convert
  // etc. are not declared abstract but otherwise act as abstract methods.

  /**
   * Convert the given time duration in the given unit to this
   * unit.  Conversions from finer to coarser granularities
   * truncate, so lose precision. For example converting
   * <tt>999</tt> milliseconds to seconds results in
   * <tt>0</tt>. Conversions from coarser to finer granularities
   * with arguments that would numerically overflow saturate to
   * <tt>Long.MIN_VALUE</tt> if negative or <tt>Long.MAX_VALUE</tt>
   * if positive.
   * <p/>
   * <p>For example, to convert 10 minutes to milliseconds, use:
   * <tt>TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)</tt>
   *
   * @param sourceDuration the time duration in the given <tt>sourceUnit</tt>
   * @param sourceUnit     the unit of the <tt>sourceDuration</tt> argument
   * @return the converted duration in this unit,
   *         or <tt>Long.MIN_VALUE</tt> if conversion would negatively
   *         overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
   */
  public long convert(final long sourceDuration, final TimeUnit sourceUnit) {
    throw new RuntimeException("abstract method not implemented");
  }

  /**
   * Equivalent to <tt>NANOSECONDS.convert(duration, this)</tt>.
   *
   * @param duration the duration
   * @return the converted duration,
   *         or <tt>Long.MIN_VALUE</tt> if conversion would negatively
   *         overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
   * @see #convert
   */
  public long toNanos(final long duration) {
    throw new RuntimeException("abstract method not implemented");
  }

  /**
   * Equivalent to <tt>MICROSECONDS.convert(duration, this)</tt>.
   *
   * @param duration the duration
   * @return the converted duration,
   *         or <tt>Long.MIN_VALUE</tt> if conversion would negatively
   *         overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
   * @see #convert
   */
  public long toMicros(final long duration) {
    throw new RuntimeException("abstract method not implemented");
  }

  /**
   * Equivalent to <tt>MILLISECONDS.convert(duration, this)</tt>.
   *
   * @param duration the duration
   * @return the converted duration,
   *         or <tt>Long.MIN_VALUE</tt> if conversion would negatively
   *         overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
   * @see #convert
   */
  public long toMillis(final long duration) {
    throw new RuntimeException("abstract method not implemented");
  }

  /**
   * Equivalent to <tt>SECONDS.convert(duration, this)</tt>.
   *
   * @param duration the duration
   * @return the converted duration,
   *         or <tt>Long.MIN_VALUE</tt> if conversion would negatively
   *         overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
   * @see #convert
   */
  public long toSeconds(final long duration) {
    throw new RuntimeException("abstract method not implemented");
  }

  /**
   * Equivalent to <tt>MINUTES.convert(duration, this)</tt>.
   *
   * @param duration the duration
   * @return the converted duration,
   *         or <tt>Long.MIN_VALUE</tt> if conversion would negatively
   *         overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
   * @see #convert
   * @since 1.6
   */
  public long toMinutes(final long duration) {
    throw new RuntimeException("abstract method not implemented");
  }

  /**
   * Equivalent to <tt>HOURS.convert(duration, this)</tt>.
   *
   * @param duration the duration
   * @return the converted duration,
   *         or <tt>Long.MIN_VALUE</tt> if conversion would negatively
   *         overflow, or <tt>Long.MAX_VALUE</tt> if it would positively overflow.
   * @see #convert
   * @since 1.6
   */
  public long toHours(final long duration) {
    throw new RuntimeException("abstract method not implemented");
  }

  /**
   * Equivalent to <tt>DAYS.convert(duration, this)</tt>.
   *
   * @param duration the duration
   * @return the converted duration
   * @see #convert
   * @since 1.6
   */
  public long toDays(final long duration) {
    throw new RuntimeException("abstract method not implemented");
  }

  /**
   * Utility to compute the excess-nanosecond argument to wait,
   * sleep, join.
   *
   * @param d the duration
   * @param m the number of milliseconds
   * @return the number of nanoseconds
   */
  abstract int excessNanos(long d, long m);

  /**
   * Not supported in this implementation.
   *
   * @param obj
   * @param timeout
   * @throws InterruptedException
   */
  public void timedWait(final Object obj, final long timeout) {
    throw new RuntimeException("not supported");
  }
}