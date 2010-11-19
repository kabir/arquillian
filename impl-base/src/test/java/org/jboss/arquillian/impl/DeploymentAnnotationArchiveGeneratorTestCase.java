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
package org.jboss.arquillian.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.junit.Test;

/**
 * DeploymentAnnotationArchiveGeneratorTestCase
 *
 * @author <a href="mailto:aknutsen@redhat.com">Aslak Knutsen</a>
 * @author Thomas.Diesler@jboss.com
 */
public class DeploymentAnnotationArchiveGeneratorTestCase
{
   @Test
   public void shouldGenerateArchiveOnDeploymentNotPresent() throws Exception
   {
      DeploymentAnnotationArchiveGenerator generator = new DeploymentAnnotationArchiveGenerator();
      TestClass testCase = new TestClass(DeploymentNotPresent.class);

      Archive<?> archive = generator.generateApplicationArchive(testCase);
      assertNotNull("Archive generated", archive);
      assertEquals(DeploymentNotPresent.class.getSimpleName(), archive.getName());
      ArchivePath path = createArchivePath(DeploymentNotPresent.class);
      assertTrue("Contains " + path, archive.contains(path));
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionOnDeploymentNotStatic() throws Exception
   {
      new DeploymentAnnotationArchiveGenerator().generateApplicationArchive(new TestClass(DeploymentNotStatic.class));
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionOnDeploymentWrongReturnType() throws Exception
   {
      new DeploymentAnnotationArchiveGenerator().generateApplicationArchive(new TestClass(DeploymentWrongReturnType.class));
   }

   @Test
   public void shouldIncludeTestClassInDeployment() throws Exception
   {
      Archive<?> archive = new DeploymentAnnotationArchiveGenerator().generateApplicationArchive(new TestClass(DeploymentOK.class));

      ArchivePath testPath = createArchivePath(DeploymentOK.class);

      // verify that the test class was added to the archive
      Assert.assertNotNull(archive.contains(testPath));
   }

   @Test
   public void shouldNotIncludeTheTestClassIfClassesNotSupportedByTheArchive() throws Exception
   {
      Archive<?> archive = new DeploymentAnnotationArchiveGenerator().generateApplicationArchive(new TestClass(DeploymentClassesNotSupported.class));

      // verify that nothing was added to the archive
      Assert.assertTrue(archive.getContent().isEmpty());
   }

   @Test
   public void shouldNotIncludeTheTestClassIfRunAsClient() throws Exception
   {
      Archive<?> archive = new DeploymentAnnotationArchiveGenerator().generateApplicationArchive(new TestClass(DeploymentRunAsClient.class));

      ArchivePath testPath = createArchivePath(DeploymentRunAsClient.class);

      // verify that the test class was not added
      Assert.assertFalse(archive.contains(testPath));
   }

   private ArchivePath createArchivePath(Class<?> clazz)
   {
      return ArchivePaths.create(clazz.getName().replaceAll("\\.", "/") + ".class");
   }

   private static class DeploymentNotPresent
   {
   }

   private static class DeploymentNotStatic
   {
      @SuppressWarnings("unused")
      @Deployment
      public Archive<?> test()
      {
         return ShrinkWrap.create(JavaArchive.class);
      }
   }

   private static class DeploymentWrongReturnType
   {
      @SuppressWarnings("unused")
      @Deployment
      public Object test()
      {
         return ShrinkWrap.create(JavaArchive.class);
      }
   }

   private static class DeploymentOK
   {
      @SuppressWarnings("unused")
      @Deployment
      public static JavaArchive test()
      {
         return ShrinkWrap.create(JavaArchive.class);
      }
   }

   private static class DeploymentClassesNotSupported
   {
      @SuppressWarnings("unused")
      @Deployment
      public static ResourceAdapterArchive test()
      {
         return ShrinkWrap.create(ResourceAdapterArchive.class);
      }
   }

   @Run(RunModeType.AS_CLIENT)
   private static class DeploymentRunAsClient
   {
      @SuppressWarnings("unused")
      @Deployment
      public static JavaArchive test()
      {
         return ShrinkWrap.create(JavaArchive.class);
      }
   }
}
