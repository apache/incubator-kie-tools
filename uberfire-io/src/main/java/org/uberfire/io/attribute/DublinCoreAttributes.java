package org.uberfire.io.attribute;

import java.util.List;

import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */
public interface DublinCoreAttributes extends BasicFileAttributes {

    List<String> titles();

    List<String> creators();

    List<String> subjects();

    List<String> descriptions();

    List<String> publishers();

    List<String> contributors();

    List<String> types();

    List<String> formats();

    List<String> identifiers();

    List<String> sources();

    List<String> languages();

    List<String> relations();

    List<String> coverages();

    List<String> rights();
}
