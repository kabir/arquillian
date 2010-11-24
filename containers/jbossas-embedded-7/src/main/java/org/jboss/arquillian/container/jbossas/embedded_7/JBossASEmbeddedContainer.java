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
package org.jboss.arquillian.container.jbossas.embedded_7;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Properties;

import javax.management.MBeanServerConnection;

import org.jboss.arquillian.protocol.domain.AbstractDeployableContainer;
import org.jboss.arquillian.protocol.jmx.JMXMethodExecutor;
import org.jboss.arquillian.protocol.jmx.JMXTestRunnerMBean;
import org.jboss.arquillian.protocol.jmx.JMXMethodExecutor.ExecutionType;
import org.jboss.arquillian.spi.ContainerMethodExecutor;
import org.jboss.arquillian.spi.Context;
import org.jboss.arquillian.spi.LifecycleException;
import org.jboss.as.server.EmbeddedServerFactory;
import org.jboss.as.server.StandaloneServer;
import org.jboss.as.server.StandaloneServerFactory;

/**
 * JBossASEmbeddedContainer
 *
 * @author Thomas.Diesler@jboss.com
 * @since 17-Nov-2010
 */
public class JBossASEmbeddedContainer extends AbstractDeployableContainer
{
   private StandaloneServer server;

   @Override
   public void start(Context context) throws LifecycleException
   {
      try
      {
         String jbossHomeKey = "jboss.home";
         String jbossHomeProp = System.getProperty(jbossHomeKey);
         if (jbossHomeProp == null)
             throw new IllegalStateException("Cannot find system property: " + jbossHomeKey);

         File jbossHomeDir = new File(jbossHomeProp).getAbsoluteFile();
         if (jbossHomeDir.isDirectory() == false)
            throw new IllegalStateException("Invalid jboss home directory: " + jbossHomeDir);

         File modulesJar = new File(jbossHomeDir + "/jboss-modules.jar");
         if (modulesJar.exists() == false)
            throw new IllegalStateException("Cannot find: " + modulesJar);

         Properties sysprops = new Properties();
         sysprops.putAll(System.getProperties());
         sysprops.setProperty("jboss.home.dir", jbossHomeDir.getAbsolutePath());
         sysprops.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
         sysprops.setProperty("logging.configuration", "file:" + jbossHomeDir + "/standalone/configuration/logging.properties");
         sysprops.setProperty("org.jboss.boot.log.file", jbossHomeDir + "/standalone/log/boot.log");

         server = EmbeddedServerFactory.create(jbossHomeDir, sysprops);
         server.start();

         long timeout = 5000;
         boolean testRunnerMBeanAvailable = false;
         MBeanServerConnection mbeanServer = null;
         while (timeout > 0 && testRunnerMBeanAvailable == false)
         {
            if (mbeanServer == null)
            {
               try
               {
                  mbeanServer = getMBeanServerConnection();
               }
               catch (Exception ex)
               {
                  // ignore
               }
            }

            testRunnerMBeanAvailable = (mbeanServer != null && mbeanServer.isRegistered(JMXTestRunnerMBean.OBJECT_NAME));

            Thread.sleep(1000);
            timeout -= 1000;
         }
      }
      catch (Throwable th)
      {
         throw handleStartThrowable(th);
      }
   }

   @Override
   public void stop(Context context) throws LifecycleException
   {
      try
      {
         if (server != null)
            server.stop();
      }
      catch (Exception e)
      {
         throw new LifecycleException("Could not stop container", e);
      }
   }
   
   @Override
   public MBeanServerConnection getMBeanServerConnection() 
   {
      return ManagementFactory.getPlatformMBeanServer();
   }

   protected ContainerMethodExecutor getContainerMethodExecutor()
   {
      return new JMXMethodExecutor(getMBeanServerConnection(), ExecutionType.EMBEDDED, JMXTestRunnerMBean.OBJECT_NAME);
   }

   private LifecycleException handleStartThrowable(Throwable th) throws LifecycleException
   {
      if (th instanceof UndeclaredThrowableException)
         throw handleStartThrowable(((UndeclaredThrowableException)th).getUndeclaredThrowable());

      if (th instanceof InvocationTargetException)
         throw handleStartThrowable(((InvocationTargetException)th).getTargetException());

      if (th instanceof RuntimeException)
         throw (RuntimeException)th;

      return new LifecycleException("Could not start container", th);
   }
}