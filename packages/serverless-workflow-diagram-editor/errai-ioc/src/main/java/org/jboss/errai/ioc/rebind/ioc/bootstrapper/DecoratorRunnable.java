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


package org.jboss.errai.ioc.rebind.ioc.bootstrapper;

import java.lang.annotation.ElementType;

import org.jboss.errai.ioc.client.api.CodeDecorator;

/**
 * A runnable for executing {@link CodeDecorator CodeDecorators} that can be sorted in the proper execution order.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class DecoratorRunnable implements Comparable<DecoratorRunnable>, Runnable {

  public int decoratorOrder;
  public ElementType elementType;
  private final Runnable runnable;

  public DecoratorRunnable(final int decoratorOrder, final ElementType elementType, final Runnable runnable) {
    this.decoratorOrder = decoratorOrder;
    this.elementType = elementType;
    this.runnable = runnable;
  }

  @Override
  public int compareTo(final DecoratorRunnable o) {
    final Long orderDifference = getTotalOrder() - o.getTotalOrder();

    if (orderDifference < 0) {
      return -1;
    }
    else if (orderDifference > 0) {
      return 1;
    }
    else {
      return 0;
    }
  }

  private long getTotalOrder() {
    return  (decoratorOrder << 2) + elementTypeToLong();
  }

  private long elementTypeToLong() {
    switch (elementType) {
    case FIELD:
      return 0L;
    case PARAMETER:
      return 1L;
    case METHOD:
      return 2L;
    case TYPE:
      return 3L;
    default:
      throw new RuntimeException("Invalid element type " + elementType.toString());
    }
  }

  @Override
  public void run() {
    runnable.run();
  }

}
