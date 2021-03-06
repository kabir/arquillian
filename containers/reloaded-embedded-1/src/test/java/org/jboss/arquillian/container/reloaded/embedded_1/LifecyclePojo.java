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
package org.jboss.arquillian.container.reloaded.embedded_1;

import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.beans.metadata.api.annotations.Stop;

/**
 * Simple POJO with lifecycle operations to set state
 * in for testing
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class LifecyclePojo
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Possible states for this POJO
    */
   enum State {
      STARTED, STOPPED;
   }

   /**
    * State flag so the test can see that we've been deployed
    */
   State state = State.STOPPED;

   //-------------------------------------------------------------------------------------||
   // Lifecycle Methods ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Lifecycle start callback
    * @throws Exception
    */
   @Start
   public void start() throws Exception
   {
      state = State.STARTED;
   }

   /**
    * Lifecycle stop callback 
    * @throws Exception
    */
   @Stop
   public void stop() throws Exception
   {
      state = State.STOPPED;
   }
}
