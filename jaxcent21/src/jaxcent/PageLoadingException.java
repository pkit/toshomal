/*
 * Copyright (c) 2008, Desiderata Software
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 
 * 
 * Neither the name of Desiderata Software nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * Module Name:           PageLoadingException.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Separated out this exception as a new exception class.
 *
 * Change History:
 *
 *      5/2/2008  MP      Initial version.
 *
 *
 */

package jaxcent;

/**  PageLoadingException is thrown if data retrieval operations are attempted before
  *  the page has completed loading.
  *  <P>In browsers, there is a delay between the start of page loading, and
  *  before the page has been loaded and HTML elements can be accessed.  Jaxcent
  *  utilizes this time to initialize the server-side Jaxlet (JaxcentPage-derived) object.
  *  <P>
  *  In the constructor, data retrieval is not available because the page loading is not complete.
  *  While the page is still loading, output can be sent, and will be processed when
  *  the page loading is complete.  But no input can retrieved, because the 
  *  HTML elements are not yet accessible.
  *  <P>
  *  Input is available from or after onLoad, and from any event handlers.
  */

public class PageLoadingException extends Jaxception {
    public PageLoadingException()
    {
        super( "Data retrieval is not available before onLoad.  (You can retrieve data from event handlers and onLoad, but not from a constructor.)" );
    }
}
