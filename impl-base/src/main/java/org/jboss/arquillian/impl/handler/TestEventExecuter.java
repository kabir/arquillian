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
package org.jboss.arquillian.impl.handler;

import org.jboss.arquillian.spi.Context;
import org.jboss.arquillian.spi.TestResult;
import org.jboss.arquillian.spi.TestResult.Status;
import org.jboss.arquillian.spi.event.suite.EventHandler;
import org.jboss.arquillian.spi.event.suite.Test;

/**
 * A Handler for executing the Test Method.<br/>
 *  <br/>
 *  <b>Exports:</b><br/>
 *   {@link TestResult}<br/>
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class TestEventExecuter implements EventHandler<Test>
{
   /* (non-Javadoc)
    * @see org.jboss.arquillian.spi.event.suite.EventHandler#callback(org.jboss.arquillian.spi.Context, java.lang.Object)
    */
   public void callback(Context context, Test event) throws Exception 
   {
      TestResult result = new TestResult();
      try 
      {
         event.getTestMethodExecutor().invoke();
         result.setStatus(Status.PASSED);
      } 
      catch (Throwable e) 
      {
         result.setStatus(Status.FAILED);
         result.setThrowable(e);
      }
      finally 
      {
         result.setEnd(System.currentTimeMillis());         
      }
      context.add(TestResult.class, result);
   }
}
