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

import org.jboss.arquillian.jmx.DeploymentProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

/**
 * An {@link DeploymentProvider} implementation.
 *
 * @author thomas.diesler@jboss.com
 * @author <a href="david@redhat.com">David Bosschaert</a>
 * @since 07-Sep-2010
 */
class OSGiContainerImpl implements OSGiContainer
{
   private BundleContext context;

   OSGiContainerImpl(BundleContext context)
   {
      this.context = context;
   }

   @Override
   public Bundle getBundle(String symbolicName, Version version) throws BundleException
   {
      if (context == null)
         throw new IllegalArgumentException("Null context");
      if (symbolicName == null)
         throw new IllegalArgumentException("Null symbolicName");

      for (Bundle bundle : context.getBundles())
      {
         boolean artefactMatch = symbolicName.equals(bundle.getSymbolicName());
         boolean versionMatch = version == null || version.equals(bundle.getVersion());
         if (artefactMatch && versionMatch)
            return bundle;
      }
      return null;
   }
}
