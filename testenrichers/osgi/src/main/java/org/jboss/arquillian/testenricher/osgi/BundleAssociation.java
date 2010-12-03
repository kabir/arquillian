/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.testenricher.osgi;

import org.osgi.framework.Bundle;

/**
 * A thread local {@link Bundle} association
 *
 * @author thomas.diesler@jboss.com
 * @since 18-Nov-2010
 */
public final class BundleAssociation
{
   private static ThreadLocal<Bundle> association = new ThreadLocal<Bundle>();

   public static Bundle getBundle()
   {
      return association.get();
   }

   public static void setBundle(Bundle type)
   {
      association.set(type);
   }
}
