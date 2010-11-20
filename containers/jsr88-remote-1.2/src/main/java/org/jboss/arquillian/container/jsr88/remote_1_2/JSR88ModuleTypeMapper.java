/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.container.jsr88.remote_1_2;

import javax.enterprise.deploy.shared.ModuleType;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * A mapper that determines the JSR 88 {@link ModuleType} of a ShrinkWrap archive.
 *
 * @author Dan Allen
 */
public class JSR88ModuleTypeMapper
{
   public JSR88ModuleTypeMapper()
   {
   }

   public ModuleType getModuleType(Archive<?> archive)
   {
      if (WebArchive.class.isInstance(archive))
      {
         return ModuleType.WAR;
      }
      if (EnterpriseArchive.class.isInstance(archive))
      {
         return ModuleType.EAR;
      }
      if (ResourceAdapterArchive.class.isInstance(archive))
      {
         return ModuleType.RAR;
      }
      return ModuleType.EJB;
   }
}
