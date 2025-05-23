/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello.sandbox.common.util.collections;

import java.util.Objects;

public class Triple<A, B, C> {
  public A first;
  public B second;
  public C third;

  public Triple(A first, B second, C third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  // ctor cannot infer types w/o warning but a method can.
  public static <A, B, C> com.hello.sandbox.common.util.collections.Triple<A, B, C> triple(
      A first, B second, C third) {
    return new com.hello.sandbox.common.util.collections.Triple<>(first, second, third);
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second, third);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    com.hello.sandbox.common.util.collections.Triple<?, ?, ?> triple =
        (com.hello.sandbox.common.util.collections.Triple<?, ?, ?>) o;
    return Objects.equals(first, triple.first)
        && Objects.equals(second, triple.second)
        && Objects.equals(third, triple.third);
  }

  @Override
  public String toString() {
    return "(" + first + ", " + second + "," + third + " )";
  }
}
