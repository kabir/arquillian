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
package org.jboss.arquillian.container.jbossas.managed_7;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;

import org.jboss.arquillian.protocol.domain.AbstractDeployableContainer;
import org.jboss.arquillian.protocol.jmx.JMXTestRunnerMBean;
import org.jboss.arquillian.spi.Context;
import org.jboss.arquillian.spi.LifecycleException;
import org.jboss.arquillian.spi.Logger;

/**
 * JBossASEmbeddedContainer
 *
 * @author Thomas.Diesler@jboss.com
 * @since 17-Nov-2010
 */
public class JBossASManagedContainer extends AbstractDeployableContainer
{
   private final Logger log = Logger.getLogger(JBossASManagedContainer.class);
   private Process process;

   @Override
   public void start(Context context) throws LifecycleException
   {
      try
      {
         String jbossHomeKey = "jboss.home";
         String jbossHomeDir = System.getProperty(jbossHomeKey);
         if (jbossHomeDir == null)
            throw new IllegalStateException("Cannot find system property: " + jbossHomeKey);

         File modulesJar = new File(jbossHomeDir + "/jboss-modules.jar");
         if (modulesJar.exists() == false)
            throw new IllegalStateException("Cannot find: " + modulesJar);

         List<String> cmd = new ArrayList<String>();
         cmd.add("java");
         cmd.add("-Djboss.home.dir=" + jbossHomeDir);
         cmd.add("-Dorg.jboss.boot.log.file=" + jbossHomeDir + "/standalone/log/boot.log");
         cmd.add("-Dlogging.configuration=file:" + jbossHomeDir + "/standalone/configuration/logging.properties");
         cmd.add("-jar");
         cmd.add(modulesJar.getAbsolutePath());
         cmd.add("-mp");
         cmd.add(jbossHomeDir + "/modules");
         cmd.add("-logmodule");
         cmd.add("org.jboss.logmanager");
         cmd.add("org.jboss.as.standalone");

         log.info("Starting container with: " + cmd.toString());
         ProcessBuilder processBuilder = new ProcessBuilder(cmd);
         processBuilder.redirectErrorStream(true);
         process = processBuilder.start();

         long timeout = 5000;
         boolean testRunnerMBeanAvaialble = false;
         MBeanServerConnection mbeanServer = null;
         while (timeout > 0 && testRunnerMBeanAvaialble == false)
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

            testRunnerMBeanAvaialble = (mbeanServer != null && mbeanServer.isRegistered(JMXTestRunnerMBean.OBJECT_NAME));

            Thread.sleep(1000);
            timeout -= 1000;
         }
      }
      catch (Exception e)
      {
         throw new LifecycleException("Could not start container", e);
      }
   }

   @Override
   public void stop(Context context) throws LifecycleException
   {
      try
      {
         if (process != null)
         {
            process.destroy();
            process.waitFor();
            process = null;
         }
      }
      catch (Exception e)
      {
         throw new LifecycleException("Could not stop container", e);
      }
   }
}