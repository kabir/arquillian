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
package org.jboss.arquillian.jmx;

import java.io.InputStream;
import java.net.URL;

import org.jboss.arquillian.api.ArchiveProvider;
import org.jboss.arquillian.protocol.jmx.ResourceCallbackHandler;
import org.jboss.arquillian.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;

/**
 * A provider for client generated deployments.
 *
 * An instance of this object can be injected into an test case like this
 * 
 * <code>
 * @Inject
 * DeploymentProvider provider;
 * </code>
 * 
 * @author thomas.diesler@jboss.com
 * @since 07-Sep-2010
 */
public interface DeploymentProvider
{
   /**
    * Get the archive URL for the given maven artifact id.
    * This method expects the artifact on the test client's classpath.
    */
   URL getArchiveURL(String artifactId);

   /**
    * Get the archive URL for the given maven artifact.
    * This method expects the artifact in the local maven repository.
    */
   URL getArchiveURL(String groupId, String artifactId, String version);

   /**
    * Gets an archive with the given name by invoking the {@link ArchiveProvider}.
    * This method makes a callback to the client side to generate the archive.
    */
   Archive<?> getClientDeployment(String name);

   /**
    * Gets an an input stream for an archive with the given name by invoking the {@link ArchiveProvider}.
    * This method makes a callback to the client side to generate the archive.
    */
   InputStream getClientDeploymentAsStream(String name);

   class Factory
   {
      public static DeploymentProvider newInstance(TestClass testClass, ResourceCallbackHandler callbackHandler)
      {
         return new DeploymentProviderImpl(testClass, callbackHandler);
      }
   }
}
