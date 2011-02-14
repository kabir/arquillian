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
package org.jboss.arquillian.osgi;

import org.jboss.arquillian.spi.TestClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * Provides OSGi framework specific functionality.
 * 
 * An instance of this object can be injected into an test case like this
 * 
 * <code>
 * @Inject
 * OSGiContainer container;
 * </code>
 * 
 * @author thomas.diesler@jboss.com
 * @since 07-Sep-2010
 */
public interface OSGiContainer
{
   /**
    * Get a bundle from the local framework instance.
    * @param symbolicName The madatory bundle symbolic name
    * @param version The optional bundle version
    * @return The bundle or null.
    * @throws BundleException If there is a problem accessing the framework
    */
   Bundle getBundle(String symbolicName, Version version) throws BundleException;

   class Factory
   {
      public static OSGiContainer newInstance(BundleContext context, TestClass testClass)
      {
         return new OSGiContainerImpl(context);
      }
   }
}
