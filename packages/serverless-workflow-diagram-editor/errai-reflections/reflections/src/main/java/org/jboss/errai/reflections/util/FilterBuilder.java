/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.reflections.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.jboss.errai.reflections.ReflectionsException;

/**
 * an include exclude filter builder
 * <p>for example:
 * <pre>
 * Predicate<String> filter1 = FilterBuilder.parse("+.*, -java.*");
 * Predicate<String> filter2 = new FilterBuilder().include(".*").exclude("java.*");
 * </pre>
 */
public class FilterBuilder implements Predicate<String> {
    private final List<Predicate<String>> chain;

    public FilterBuilder() {chain = Lists.newArrayList();}
    private FilterBuilder(final Iterable<Predicate<String>> filters) {chain = Lists.newArrayList(filters);}

    /** include a regular expression */
    public FilterBuilder include(final String regex) {return add(new Include(regex));}
    /** exclude a regular expression*/
    public FilterBuilder exclude(final String regex) {add(new Exclude(regex)); return this;}
    /** add a Predicate to the chain of predicates*/
    public FilterBuilder add(Predicate<String> filter) {chain.add(filter); return this;}
    /** include a package of a given class */
    public FilterBuilder includePackage(final Class<?> aClass) {return add(new Include(packageNameRegex(aClass)));}
    /** exclude a package of a given class */
    public FilterBuilder excludePackage(final Class<?> aClass) {return add(new Exclude(packageNameRegex(aClass)));}

    private static String packageNameRegex(Class<?> aClass) {return prefix(aClass.getPackage().getName() + ".");}

    public static String prefix(String qualifiedName) {return qualifiedName.replace(".","\\.") + ".*";}

    @Override public String toString() {return Joiner.on(", ").join(chain);}

    public boolean apply(String regex) {
        boolean accept = chain == null || chain.isEmpty() || chain.get(0) instanceof Exclude;

        if (chain != null) {
            for (Predicate<String> filter : chain) {
                if (accept && filter instanceof Include) {continue;} //skip if this filter won't change
                if (!accept && filter instanceof Exclude) {continue;}
                accept = filter.apply(regex);
            }
        }
        return accept;
    }

    public abstract static class Matcher extends FilterBuilder {
        final Pattern pattern;
        public Matcher(final String regex) {pattern = Pattern.compile(regex);}
        @Override public abstract boolean apply(String regex);
        @Override public String toString() {return pattern.pattern();}
    }

    public static class Include extends Matcher {
        public Include(final String patternString) {super(patternString);}
        @Override public boolean apply(final String regex) {return pattern.matcher(regex).matches();}
        @Override public String toString() {return "+" + super.toString();}
    }

    public static class Exclude extends Matcher {
        public Exclude(final String patternString) {super(patternString);}
        @Override public boolean apply(final String regex) {return !pattern.matcher(regex).matches();}
        @Override public String toString() {return "-" + pattern.pattern();}
    }

    /**
     * parses a string representation of include exclude filter
     * <p>the given includeExcludeString is a comma separated list of patterns, each starts with either + or - to indicate include/exclude resp.
     * followed by the regular expression pattern
     * <p>for example parse("-java., -javax., -sun., -com.sun.") or parse("+com.myn,-com.myn.excluded")
     * */
    public static FilterBuilder parse(String includeExcludeString) {
        List<Predicate<String>> filters = new ArrayList<Predicate<String>>();

        if (!Utils.isEmpty(includeExcludeString)) {
            for (String string : includeExcludeString.split(",")) {
                String trimmed = string.trim();
                char prefix = trimmed.charAt(0);
                String pattern = trimmed.substring(1);

                Predicate<String> filter;
                switch (prefix) {
                    case '+':
                        filter = new Include(pattern);
                        break;
                    case '-':
                        filter = new Exclude(pattern);
                        break;
                    default:
                        throw new ReflectionsException("includeExclude should start with either + or -");
                }

                filters.add(filter);
            }

            return new FilterBuilder(filters);
        } else {
            return new FilterBuilder();
        }
    }
}
