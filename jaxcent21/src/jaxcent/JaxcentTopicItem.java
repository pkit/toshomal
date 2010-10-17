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
 * Module Name:           JaxcentTopicItem.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Simple publish-and-subscribe model for Jaxcent.
 *
 * Change History:
 *
 *    1/17/2008   MP      Initial version.
 *
 */

package jaxcent;

/**
  * JaxcentTopicItem objects are returned by JaxcentTopic.  They are given
  * to the JaxcentTopic to retrieve or to wait for the next item.
  *
  * The item and its creation time can be retrieved from objects of
  * this class.
  */

public class JaxcentTopicItem {
    long creationTime;
    Object item;
    JaxcentTopicItem link;
    boolean obsolete;

    JaxcentTopicItem()
    {
        creationTime = 0;
        item = null;
        link = null;
        obsolete = false;
    }

    /** Get the underlying item posted to the topic.
      */
    public Object getItem()
    {
        return item;
    }

    /** Get the posting time of the item.
      */
    public java.util.Date getPostingTime()
    {
        return new java.util.Date( creationTime );
    }
}
