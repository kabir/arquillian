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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

import org.jboss.arquillian.protocol.jmx.ResourceCallbackHandler;
import org.jboss.arquillian.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.ArchiveBase;

/**
 * An {@link DeploymentProvider} implementation.
 *
 * @author thomas.diesler@jboss.com
 * @author <a href="david@redhat.com">David Bosschaert</a>
 * @since 07-Sep-2010
 */
class DeploymentProviderImpl implements DeploymentProvider
{
   private TestClass testClass;
   private ResourceCallbackHandler callbackHandler;

   DeploymentProviderImpl(TestClass testClass, ResourceCallbackHandler callbackHandler)
   {
      this.testClass = testClass;
      this.callbackHandler = callbackHandler;
   }

   @Override
   public URL getArchiveURL(String artifactId)
   {
      return getArchiveURL(null, artifactId, null);
   }

   @Override
   public URL getArchiveURL(String groupId, String artifactId, String version)
   {
      URL artifactURL = RepositoryArchiveLocator.getArtifactURL(groupId, artifactId, version);
      return artifactURL;
   }

   @Override
   public Archive<?> getClientDeployment(String name)
   {
      InputStream input = getClientDeploymentAsStream(name);

      ClassLoader ctxLoader = SecurityActions.getThreadContextClassLoader();
      try
      {
         // Create the archive in the context of the arquillian-osgi-bundle
         SecurityActions.setThreadContextClassLoader(ArchiveBase.class.getClassLoader());
         JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
         ZipImporter zipImporter = archive.as(ZipImporter.class);
         zipImporter.importZip(new ZipInputStream(input));
         return archive;
      }
      finally
      {
         SecurityActions.setThreadContextClassLoader(ctxLoader);
      }
   }

   @Override
   public InputStream getClientDeploymentAsStream(String name)
   {
      try
      {
         byte[] bytes = callbackHandler.requestResource(testClass, name);
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         return bais;
      }
      catch (Exception ex)
      {
         throw new IllegalStateException("Cannot obtain test archive: " + name, ex);
      }
   }
}
