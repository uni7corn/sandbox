/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package external.org.apache.commons.lang3.builder;

import external.org.apache.commons.lang3.ObjectUtils;

/**
 * Assists in implementing {@link Object#toString()} methods.
 *
 * <p>This class enables a good and consistent <code>toString()</code> to be built for any class or
 * object. This class aims to simplify the process by:
 *
 * <ul>
 *   <li>allowing field names
 *   <li>handling all types consistently
 *   <li>handling nulls consistently
 *   <li>outputting arrays and multi-dimensional arrays
 *   <li>enabling the detail level to be controlled for Objects and Collections
 *   <li>handling class hierarchies
 * </ul>
 *
 * <p>To use this class write code as follows:
 *
 * <pre>
 * public class Person {
 *   String name;
 *   int age;
 *   boolean smoker;
 *
 *   ...
 *
 *   public String toString() {
 *     return new ToStringBuilder(this).
 *       append("name", name).
 *       append("age", age).
 *       append("smoker", smoker).
 *       toString();
 *   }
 * }
 * </pre>
 *
 * <p>This will produce a toString of the format: <code>
 * Person@7f54[name=Stephen,age=29,smoker=false]</code>
 *
 * <p>To add the superclass <code>toString</code>, use {@link #appendSuper}. To append the <code>
 * toString</code> from an object that is delegated to (or any other object), use {@link
 * #appendToString}.
 *
 * <p>Alternatively, there is a method that uses reflection to determine the fields to test. Because
 * these fields are usually private, the method, <code>reflectionToString</code>, uses <code>
 * AccessibleObject.setAccessible</code> to change the visibility of the fields. This will fail
 * under a security manager, unless the appropriate permissions are set up correctly. It is also
 * slower than testing explicitly.
 *
 * <p>A typical invocation for this method would look like:
 *
 * <pre>
 * public String toString() {
 *   return ToStringBuilder.reflectionToString(this);
 * }
 * </pre>
 *
 * <p>You can also use the builder to debug 3rd party objects:
 *
 * <pre>
 * System.out.println("An object: " + ToStringBuilder.reflectionToString(anObject));
 * </pre>
 *
 * <p>The exact format of the <code>toString</code> is determined by the {@link ToStringStyle}
 * passed into the constructor.
 *
 * @since 1.0
 * @version $Id: ToStringBuilder.java 1088899 2011-04-05 05:31:27Z bayard $
 */
public class ToStringBuilder implements Builder<String> {

  /** The default style of output to use, not null. */
  private static volatile ToStringStyle defaultStyle = ToStringStyle.DEFAULT_STYLE;

  // ----------------------------------------------------------------------------

  /**
   * Gets the default <code>ToStringStyle</code> to use.
   *
   * <p>This method gets a singleton default value, typically for the whole JVM. Changing this
   * default should generally only be done during application startup. It is recommended to pass a
   * <code>ToStringStyle</code> to the constructor instead of using this global default.
   *
   * <p>This method can be used from multiple threads. Internally, a <code>volatile</code> variable
   * is used to provide the guarantee that the latest value set using {@link #setDefaultStyle} is
   * the value returned. It is strongly recommended that the default style is only changed during
   * application startup.
   *
   * <p>One reason for changing the default could be to have a verbose style during development and
   * a compact style in production.
   *
   * @return the default <code>ToStringStyle</code>, never null
   */
  public static ToStringStyle getDefaultStyle() {
    return defaultStyle;
  }

  /**
   * Sets the default <code>ToStringStyle</code> to use.
   *
   * <p>This method sets a singleton default value, typically for the whole JVM. Changing this
   * default should generally only be done during application startup. It is recommended to pass a
   * <code>ToStringStyle</code> to the constructor instead of changing this global default.
   *
   * <p>This method is not intended for use from multiple threads. Internally, a <code>volatile
   * </code> variable is used to provide the guarantee that the latest value set is the value
   * returned from {@link #getDefaultStyle}.
   *
   * @param style the default <code>ToStringStyle</code>
   * @throws IllegalArgumentException if the style is <code>null</code>
   */
  public static void setDefaultStyle(ToStringStyle style) {
    if (style == null) {
      throw new IllegalArgumentException("The style must not be null");
    }
    defaultStyle = style;
  }

  // ----------------------------------------------------------------------------
  /**
   * Uses <code>ReflectionToStringBuilder</code> to generate a <code>toString</code> for the
   * specified object.
   *
   * @param object the Object to be output
   * @return the String result
   * @see ReflectionToStringBuilder#toString(Object)
   */
  public static String reflectionToString(Object object) {
    return ReflectionToStringBuilder.toString(object);
  }

  /**
   * Uses <code>ReflectionToStringBuilder</code> to generate a <code>toString</code> for the
   * specified object.
   *
   * @param object the Object to be output
   * @param style the style of the <code>toString</code> to create, may be <code>null</code>
   * @return the String result
   * @see ReflectionToStringBuilder#toString(Object,ToStringStyle)
   */
  public static String reflectionToString(Object object, ToStringStyle style) {
    return ReflectionToStringBuilder.toString(object, style);
  }

  /**
   * Uses <code>ReflectionToStringBuilder</code> to generate a <code>toString</code> for the
   * specified object.
   *
   * @param object the Object to be output
   * @param style the style of the <code>toString</code> to create, may be <code>null</code>
   * @param outputTransients whether to include transient fields
   * @return the String result
   * @see ReflectionToStringBuilder#toString(Object,ToStringStyle,boolean)
   */
  public static String reflectionToString(
      Object object, ToStringStyle style, boolean outputTransients) {
    return ReflectionToStringBuilder.toString(object, style, outputTransients, false, null);
  }

  /**
   * Uses <code>ReflectionToStringBuilder</code> to generate a <code>toString</code> for the
   * specified object.
   *
   * @param <T> the type of the object
   * @param object the Object to be output
   * @param style the style of the <code>toString</code> to create, may be <code>null</code>
   * @param outputTransients whether to include transient fields
   * @param reflectUpToClass the superclass to reflect up to (inclusive), may be <code>null</code>
   * @return the String result
   * @see ReflectionToStringBuilder#toString(Object,ToStringStyle,boolean,boolean,Class)
   * @since 2.0
   */
  public static <T> String reflectionToString(
      T object, ToStringStyle style, boolean outputTransients, Class<? super T> reflectUpToClass) {
    return ReflectionToStringBuilder.toString(
        object, style, outputTransients, false, reflectUpToClass);
  }

  // ----------------------------------------------------------------------------

  /** Current toString buffer, not null. */
  private final StringBuffer buffer;
  /** The object being output, may be null. */
  private final Object object;
  /** The style of output to use, not null. */
  private final ToStringStyle style;

  /**
   * Constructs a builder for the specified object using the default output style.
   *
   * <p>This default style is obtained from {@link #getDefaultStyle()}.
   *
   * @param object the Object to build a <code>toString</code> for, not recommended to be null
   */
  public ToStringBuilder(Object object) {
    this(object, null, null);
  }

  /**
   * Constructs a builder for the specified object using the a defined output style.
   *
   * <p>If the style is <code>null</code>, the default style is used.
   *
   * @param object the Object to build a <code>toString</code> for, not recommended to be null
   * @param style the style of the <code>toString</code> to create, null uses the default style
   */
  public ToStringBuilder(Object object, ToStringStyle style) {
    this(object, style, null);
  }

  /**
   * Constructs a builder for the specified object.
   *
   * <p>If the style is <code>null</code>, the default style is used.
   *
   * <p>If the buffer is <code>null</code>, a new one is created.
   *
   * @param object the Object to build a <code>toString</code> for, not recommended to be null
   * @param style the style of the <code>toString</code> to create, null uses the default style
   * @param buffer the <code>StringBuffer</code> to populate, may be null
   */
  public ToStringBuilder(Object object, ToStringStyle style, StringBuffer buffer) {
    if (style == null) {
      style = getDefaultStyle();
    }
    if (buffer == null) {
      buffer = new StringBuffer(512);
    }
    this.buffer = buffer;
    this.style = style;
    this.object = object;

    style.appendStart(buffer, object);
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>boolean</code> value.
   *
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(boolean value) {
    style.append(buffer, null, value);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>boolean</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(boolean[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>byte</code> value.
   *
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(byte value) {
    style.append(buffer, null, value);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>byte</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(byte[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>char</code> value.
   *
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(char value) {
    style.append(buffer, null, value);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>char</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(char[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>double</code> value.
   *
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(double value) {
    style.append(buffer, null, value);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>double</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(double[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>float</code> value.
   *
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(float value) {
    style.append(buffer, null, value);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>float</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(float[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> an <code>int</code> value.
   *
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(int value) {
    style.append(buffer, null, value);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> an <code>int</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(int[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>long</code> value.
   *
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(long value) {
    style.append(buffer, null, value);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>long</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(long[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> an <code>Object</code> value.
   *
   * @param obj the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(Object obj) {
    style.append(buffer, null, obj, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> an <code>Object</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(Object[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>short</code> value.
   *
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(short value) {
    style.append(buffer, null, value);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append to the <code>toString</code> a <code>short</code> array.
   *
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(short[] array) {
    style.append(buffer, null, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>boolean</code> value.
   *
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, boolean value) {
    style.append(buffer, fieldName, value);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>boolean</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>hashCode</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, boolean[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>boolean</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, boolean[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>byte</code> value.
   *
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, byte value) {
    style.append(buffer, fieldName, value);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>byte</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, byte[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>byte</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, byte[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>char</code> value.
   *
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, char value) {
    style.append(buffer, fieldName, value);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>char</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, char[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>char</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, char[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>double</code> value.
   *
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, double value) {
    style.append(buffer, fieldName, value);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>double</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, double[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>double</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, double[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>float</code> value.
   *
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, float value) {
    style.append(buffer, fieldName, value);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>float</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, float[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>float</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, float[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>int</code> value.
   *
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, int value) {
    style.append(buffer, fieldName, value);
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>int</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, int[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>int</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, int[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>long</code> value.
   *
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, long value) {
    style.append(buffer, fieldName, value);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>long</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, long[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>long</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, long[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code> value.
   *
   * @param fieldName the field name
   * @param obj the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, Object obj) {
    style.append(buffer, fieldName, obj, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code> value.
   *
   * @param fieldName the field name
   * @param obj the value to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, Object obj, boolean fullDetail) {
    style.append(buffer, fieldName, obj, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, Object[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>Object</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, Object[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Append to the <code>toString</code> an <code>short</code> value.
   *
   * @param fieldName the field name
   * @param value the value to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, short value) {
    style.append(buffer, fieldName, value);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>short</code> array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @return this
   */
  public ToStringBuilder append(String fieldName, short[] array) {
    style.append(buffer, fieldName, array, null);
    return this;
  }

  /**
   * Append to the <code>toString</code> a <code>short</code> array.
   *
   * <p>A boolean parameter controls the level of detail to show. Setting <code>true</code> will
   * output the array in full. Setting <code>false</code> will output a summary, typically the size
   * of the array.
   *
   * @param fieldName the field name
   * @param array the array to add to the <code>toString</code>
   * @param fullDetail <code>true</code> for detail, <code>false</code> for summary info
   * @return this
   */
  public ToStringBuilder append(String fieldName, short[] array, boolean fullDetail) {
    style.append(buffer, fieldName, array, Boolean.valueOf(fullDetail));
    return this;
  }

  /**
   * Appends with the same format as the default <code>Object toString()
   * </code> method. Appends the class name followed by {@link
   * System#identityHashCode(java.lang.Object)}.
   *
   * @param object the <code>Object</code> whose class name and id to output
   * @return this
   * @since 2.0
   */
  public ToStringBuilder appendAsObjectToString(Object object) {
    ObjectUtils.identityToString(this.getStringBuffer(), object);
    return this;
  }

  // ----------------------------------------------------------------------------

  /**
   * Append the <code>toString</code> from the superclass.
   *
   * <p>This method assumes that the superclass uses the same <code>ToStringStyle</code> as this
   * one.
   *
   * <p>If <code>superToString</code> is <code>null</code>, no change is made.
   *
   * @param superToString the result of <code>super.toString()</code>
   * @return this
   * @since 2.0
   */
  public ToStringBuilder appendSuper(String superToString) {
    if (superToString != null) {
      style.appendSuper(buffer, superToString);
    }
    return this;
  }

  /**
   * Append the <code>toString</code> from another object.
   *
   * <p>This method is useful where a class delegates most of the implementation of its properties
   * to another class. You can then call <code>toString()</code> on the other class and pass the
   * result into this method.
   *
   * <pre>
   *   private AnotherObject delegate;
   *   private String fieldInThisClass;
   *
   *   public String toString() {
   *     return new ToStringBuilder(this).
   *       appendToString(delegate.toString()).
   *       append(fieldInThisClass).
   *       toString();
   *   }</pre>
   *
   * <p>This method assumes that the other object uses the same <code>ToStringStyle</code> as this
   * one.
   *
   * <p>If the <code>toString</code> is <code>null</code>, no change is made.
   *
   * @param toString the result of <code>toString()</code> on another object
   * @return this
   * @since 2.0
   */
  public ToStringBuilder appendToString(String toString) {
    if (toString != null) {
      style.appendToString(buffer, toString);
    }
    return this;
  }

  /**
   * Returns the <code>Object</code> being output.
   *
   * @return The object being output.
   * @since 2.0
   */
  public Object getObject() {
    return object;
  }

  /**
   * Gets the <code>StringBuffer</code> being populated.
   *
   * @return the <code>StringBuffer</code> being populated
   */
  public StringBuffer getStringBuffer() {
    return buffer;
  }

  // ----------------------------------------------------------------------------

  /**
   * Gets the <code>ToStringStyle</code> being used.
   *
   * @return the <code>ToStringStyle</code> being used
   * @since 2.0
   */
  public ToStringStyle getStyle() {
    return style;
  }

  /**
   * Returns the built <code>toString</code>.
   *
   * <p>This method appends the end of data indicator, and can only be called once. Use {@link
   * #getStringBuffer} to get the current string state.
   *
   * <p>If the object is <code>null</code>, return the style's <code>nullText</code>
   *
   * @return the String <code>toString</code>
   */
  @Override
  public String toString() {
    if (this.getObject() == null) {
      this.getStringBuffer().append(this.getStyle().getNullText());
    } else {
      style.appendEnd(this.getStringBuffer(), this.getObject());
    }
    return this.getStringBuffer().toString();
  }

  /**
   * Returns the String that was build as an object representation. The default implementation
   * utilizes the {@link #toString()} implementation.
   *
   * @return the String <code>toString</code>
   * @see #toString()
   * @since 3.0
   */
  public String build() {
    return toString();
  }
}
