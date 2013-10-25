/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.sample.edejket.camel.ra;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.camel.impl.DefaultClassResolver;
import org.apache.camel.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCAClassResolver extends DefaultClassResolver {

    private static final Logger log = LoggerFactory.getLogger(JCAClassResolver.class);

    private final ClassLoader modularClassloader;

    public JCAClassResolver(final ClassLoader cl) {
        log.trace("Created instance of JCAClassResolver using classloader {}", cl);
        this.modularClassloader = cl;
    }

    @Override
    public Class<?> resolveClass(final String name) {
        log.trace("resolveClass called for {}", name);
        return loadClass(name, modularClassloader);
    }

    @Override
    public <T> Class<T> resolveClass(final String name, final Class<T> type) {
        log.trace("resolveClass called for {} and type {}", new Object[] { name, type });
        final Class<T> answer = CastUtils.cast(loadClass(name, modularClassloader));
        return answer;
    }

    @Override
    public URL loadResourceAsURL(final String uri) {
        ObjectHelper.notEmpty(uri, "uri");
        final String resolvedName = resolveUriPath(uri);
        final URL url = modularClassloader.getResource(resolvedName);
        return url;
    }

    @Override
    public InputStream loadResourceAsStream(final String uri) {
        log.trace("loadResourceAsStream called for {}", uri);
        ObjectHelper.notEmpty(uri, "uri");
        final String resolvedName = resolveUriPath(uri);
        final InputStream in = modularClassloader.getResourceAsStream(resolvedName);
        return in;
    }

    @Override
    public Enumeration<URL> loadResourcesAsURL(final String uri) {
        ObjectHelper.notEmpty(uri, "uri");
        Enumeration<URL> url = null;
        try {
            url = modularClassloader.getResources(uri);
        } catch (final IOException e) {
            // ignore
        }

        return url;
    }

    /**
     * Cleans the string to a pure Java identifier so we can use it for loading class names.
     * <p/>
     * Especially from Spring DSL people can have \n \t or other characters that otherwise would result in ClassNotFoundException
     * 
     * @param name
     *            the class name
     * @return normalized classname that can be load by a class loader.
     */
    public static String normalizeClassName(final String name) {
        final StringBuilder sb = new StringBuilder(name.length());
        for (final char ch : name.toCharArray()) {
            if (ch == '.' || ch == '[' || ch == ']' || ch == '-' || Character.isJavaIdentifierPart(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * Attempts to load the given class name using the thread context class loader or the given class loader
     * 
     * @param name
     *            the name of the class to load
     * @param loader
     *            the class loader to use after the thread context class loader
     * @param needToWarn
     *            when <tt>true</tt> logs a warning when a class with the given name could not be loaded
     * @return the class or <tt>null</tt> if it could not be loaded
     */
    public Class<?> loadClass(String name, final ClassLoader loader, final boolean needToWarn) {
        // must clean the name so its pure java name, eg removing \n or whatever people can do in the Spring XML
        name = normalizeClassName(name);
        if (ObjectHelper.isEmpty(name)) {
            return null;
        }
        // Try simple type first
        Class<?> clazz = loadSimpleType(name);
        if (clazz == null) {
            // and fallback to the loader the loaded the modularClassloader
            clazz = doLoadClass(name, modularClassloader);
        }
        if (clazz == null) {
            if (needToWarn) {
                log.warn("Cannot find class: " + name);
            }
        }

        return clazz;
    }

    @Override
    protected Class<?> loadClass(final String name, final ClassLoader loader) {
        ObjectHelper.notEmpty(name, "name");
        return loadClass(name, loader, true);
    }

    /**
     * Helper operation used to remove relative path notation from resources. Most critical for resources on the Classpath as resource loaders will
     * not resolve the relative paths correctly.
     * 
     * @param name
     *            the name of the resource to load
     * @return the modified or unmodified string if there were no changes
     */
    private static String resolveUriPath(final String name) {
        // compact the path and use / as separator as that's used for loading resources on the classpath
        return FileUtil.compactPath(name, '/');
    }

    /**
     * Load a simple type
     * 
     * @param name
     *            the name of the class to load
     * @return the class or <tt>null</tt> if it could not be loaded
     */
    public static Class<?> loadSimpleType(final String name) {
        // special for byte[] or Object[] as its common to use
        if ("java.lang.byte[]".equals(name) || "byte[]".equals(name)) {
            return byte[].class;
        } else if ("java.lang.Byte[]".equals(name) || "Byte[]".equals(name)) {
            return Byte[].class;
        } else if ("java.lang.Object[]".equals(name) || "Object[]".equals(name)) {
            return Object[].class;
        } else if ("java.lang.String[]".equals(name) || "String[]".equals(name)) {
            return String[].class;
            // and these is common as well
        } else if ("java.lang.String".equals(name) || "String".equals(name)) {
            return String.class;
        } else if ("java.lang.Boolean".equals(name) || "Boolean".equals(name)) {
            return Boolean.class;
        } else if ("boolean".equals(name)) {
            return boolean.class;
        } else if ("java.lang.Integer".equals(name) || "Integer".equals(name)) {
            return Integer.class;
        } else if ("int".equals(name)) {
            return int.class;
        } else if ("java.lang.Long".equals(name) || "Long".equals(name)) {
            return Long.class;
        } else if ("long".equals(name)) {
            return long.class;
        } else if ("java.lang.Short".equals(name) || "Short".equals(name)) {
            return Short.class;
        } else if ("short".equals(name)) {
            return short.class;
        } else if ("java.lang.Byte".equals(name) || "Byte".equals(name)) {
            return Byte.class;
        } else if ("byte".equals(name)) {
            return byte.class;
        } else if ("java.lang.Float".equals(name) || "Float".equals(name)) {
            return Float.class;
        } else if ("float".equals(name)) {
            return float.class;
        } else if ("java.lang.Double".equals(name) || "Double".equals(name)) {
            return Double.class;
        } else if ("double".equals(name)) {
            return double.class;
        }

        return null;
    }

    /**
     * Loads the given class with the provided classloader (may be null). Will ignore any class not found and return null.
     * 
     * @param name
     *            the name of the class to load
     * @param loader
     *            a provided loader (may be null)
     * @return the class, or null if it could not be loaded
     */
    private static Class<?> doLoadClass(final String name, final ClassLoader loader) {
        ObjectHelper.notEmpty(name, "name");
        if (loader == null) {
            return null;
        }

        try {
            log.trace("Loading class: {} using classloader: {}", name, loader);
            return loader.loadClass(name);
        } catch (final ClassNotFoundException e) {
            if (log.isTraceEnabled()) {
                log.trace("Cannot load class: " + name + " using classloader: " + loader, e);
            }
        }

        return null;
    }

}
