/**
 *
 * Copyright 2015 Kamran Zafar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kamranzafar.commons.cloner;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by kamran on 21/02/15.
 */
public class ReflectionCloningStrategy<T> implements CloningStrategy<T> {
    private final Objenesis objenesis;
    private final Set<Class<?>> ignoredClasses = new HashSet<Class<?>>();
    private final Map<Object, Boolean> ignoredInstances = new IdentityHashMap<Object, Boolean>();
    private final ConcurrentHashMap<Class<?>, List<Field>> fieldsMap = new ConcurrentHashMap<Class<?>, List<Field>>();

    public ReflectionCloningStrategy() {
        objenesis = new ObjenesisStd();
        init();
    }

    protected void init() {
        ignoreKnownJdkImmutableClasses();
    }

    /**
     * Ignore the constant
     *
     * @param c
     * @param privateFieldName
     */
    @SuppressWarnings("unchecked")
    protected void ignoreConstant(final Class<?> c, final String privateFieldName) {
        try {
            final Field field = c.getDeclaredField(privateFieldName);

            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    field.setAccessible(true);
                    return null;
                }
            });

            Object v = field.get(null);
            ignoredInstances.put(v, true);
        } catch (Throwable e) {
            throw new CloningException(e.getMessage(), e);
        }
    }

    /**
     * Ignore some Jdk immutable classes
     */
    protected void ignoreKnownJdkImmutableClasses() {
        ignoreClass(Integer.class, Long.class, Boolean.class, Class.class, Float.class, Double.class, Character.class,
                Byte.class, Short.class, Void.class, BigDecimal.class, BigInteger.class, URI.class, URL.class,
                UUID.class, Pattern.class);
    }

    /**
     * Add to the ignore-list
     *
     * @param clazz
     */
    protected void ignoreClass(final Class<?>... clazz) {
        for (Class<?> c : clazz)
            ignoredClasses.add(c);
    }

    /**
     * Creates a new instance of the Class
     *
     * @param c
     * @return T
     */
    @SuppressWarnings("unchecked")
    protected <T> T newInstance(final Class<T> c) {
        return (T) objenesis.newInstance(c);
    }

    /**
     * @param original
     * @return T
     */
    public T deepClone(final T original) {
        if (original == null)
            return null;

        final Map<Object, Object> clones = new IdentityHashMap<Object, Object>();
        try {
            return clone(original, clones);
        } catch (IllegalAccessException e) {
            throw new CloningException("Error during cloning of " + original, e);
        }
    }

    /**
     * @param original
     * @return T
     */
    public T shallowClone(final T original) {
        if (original == null)
            return null;

        try {
            return clone(original, null);
        } catch (IllegalAccessException e) {
            throw new CloningException("Error during cloning of " + original, e);
        }
    }

    /**
     * @param original
     * @param clones
     * @return
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    protected <T> T clone(final T original, final Map<Object, Object> clones) throws IllegalAccessException {
        if(original == null){
            return null;
        }

        final Class<T> clz = (Class<T>) original.getClass();

        if (ignoredInstances.containsKey(original) || clz.isEnum() || ignoredClasses.contains(clz))
            return original;

        if (clones != null && clones.get(original) != null) {
            return (T) clones.get(original);
        }

        if (clz.isArray()) {
            int length = Array.getLength(original);
            T newInstance = (T) Array.newInstance(clz.getComponentType(), length);

            clones.put(original, newInstance);

            for (int i = 0; i < length; i++) {
                Object v = Array.get(original, i);
                Object clone = clones != null ? clone(v, clones) : v;
                Array.set(newInstance, i, clone);
            }

            return newInstance;
        }

        final T newInstance = newInstance(clz);

        if (clones != null) {
            clones.put(original, newInstance);
        }

        final List<Field> fields = allFields(clz);

        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                Object fieldObject = field.get(original);
                Object fieldObjectClone = clones != null ? clone(fieldObject, clones) : fieldObject;
                field.set(newInstance, fieldObjectClone);
            }
        }

        return newInstance;
    }

    /**
     * @param l
     * @param fields
     */
    protected void addAll(final List<Field> l, final Field[] fields) {
        for (final Field field : fields) {
            l.add(field);
        }
    }

    /**
     * @param c
     * @return List
     */
    protected List<Field> allFields(final Class<?> c) {
        List<Field> l = fieldsMap.get(c);

        if (l == null) {
            l = new LinkedList<Field>();
            Field[] fields = c.getDeclaredFields();

            addAll(l, fields);

            Class<?> sc = c;

            while ((sc = sc.getSuperclass()) != Object.class && sc != null) {
                addAll(l, sc.getDeclaredFields());
            }

            fieldsMap.putIfAbsent(c, l);
        }
        return l;
    }
}
