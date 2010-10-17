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
 * Module Name:           JaxcentTopic.java
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
  * JaxcentTopic provides a simple publish-and-subscribe model for a single server,
  * using Java synchronization primitives.  The items are not persistent,
  * and do not survive application server or host restart.  See ChatSample2.java for
  * an example of using this model.
  * <P>
  * To extend this model for clusters, add a special listener that sends messages
  * over a socket (or other mechanism) to other machines in the cluster, where
  * they get posted to another JaxcentTopic object.
  * <P>
  * To extend this model to have persistent items, add a special listener
  * that saves all items in a database.  At system startup, retrieve
  * messages from database and post them.
  * <P>
  * For complex requirements, a full fledged publish-and-subscribe model
  * should be used instead of this simple one.
  */

public class JaxcentTopic {

    JaxcentTopicItem head = new JaxcentTopicItem();
    JaxcentTopicItem last = null;
    int maxItems;
    long maxAge;
    int itemCount = 0;
    long itemCheck = 0;

    /** Create a new topic.
      * <P>
      * maxItems specifies the maximum number of items to keep in topic.
      * If 0 or negative, items are not removed by count.
      * <P>
      * maxAge specifies the maximum age of items in milliseconds.
      * If 0 or negative, items are not removed by age.
      * <P>
      * Both maxItems and maxAge must not be zero, otherwise the
      * topic will grow without bounds!
      *
      * @see jaxcent.JaxcentPage#saveInAppContext
      */

    public JaxcentTopic( int maxItems, long maxAge )
    {
        this.maxItems = maxItems;
        this.maxAge = maxAge;
    }

    void checkObsolete()
    {
        if ( maxItems > 0 && itemCount > maxItems ) {
            synchronized ( head ) {
                while ( itemCount > maxItems && head.link != null ) {
                    JaxcentTopicItem last = head.link;
                    last.obsolete = true;
                    head.link = last.link;
                    itemCount--;
                    if ( last.link != null )
                        itemCheck = last.link.creationTime;
                    else
                        itemCheck = 0;
                    synchronized ( last ) {
                        last.notifyAll();
                    }
                }
            }
        }
        if ( maxAge > 0 ) {
            long minCreation = System.currentTimeMillis() - maxAge;
            if ( itemCheck < minCreation ) {
                synchronized ( head ) {
                    while ( head.link != null && head.link.creationTime < minCreation ) {
                        JaxcentTopicItem last = head.link;
                        last.obsolete = true;
                        head.link = last.link;
                        itemCount--;
                        if ( last.link != null )
                            itemCheck = last.link.creationTime;
                        else
                            itemCheck = 0;
                        synchronized ( last ) {
                            last.notifyAll();
                        }
                    }
                }
            }
        }
    }

    /**
      * Post an item to the topic.
      * Any subscribers will see the item (until it is removed by count or age.)
      */

    public synchronized void postItem( Object newItem )
    {
        checkObsolete();
        JaxcentTopicItem item = new JaxcentTopicItem();
        item.creationTime = System.currentTimeMillis();
        item.item = newItem;
        item.link = null;
        JaxcentTopicItem prev = last;
        if ( prev == null ) {
            prev = head;
        }
        prev.link = item;
        last = item;
        itemCount++;
        if ( itemCheck == 0 )
            itemCheck = item.creationTime;
        synchronized (prev) {
            prev.notifyAll();
        }
    }

    /**
      * Get the next item from the topic.  If timeout is greater than 0, wait
      * for at most that many milliseconds for an item.  Timeout can be 0,
      * indicating return immediately without waiting.  If no items
      * arrivied within the timeout, returns null.  Otherwise, returns
      * a JaxcentTopicItem, from which the item and its creation
      * date can be retrieved, and which must be used for waiting
      * for the next item.
      * <P>The first time through, lastItem should be null.  After that,
      * it must be the last item returned.
      * <P><B>If using this form, you cannot use the syntax "item = getItem( item, timeout );",
      * because that will set the item to null upon timeouts.  The last non-null item returned, needs to
      * be preserved!</B>
      */

    public JaxcentTopicItem getItem( JaxcentTopicItem lastItem, long timeout ) throws InterruptedException
    {
        checkObsolete();
        if ( lastItem == null || lastItem.obsolete )
            lastItem = head;
        long start = System.currentTimeMillis();
        for (;;) {
            synchronized ( lastItem ) {
                if ( lastItem.link == null && timeout > 0 && ! lastItem.obsolete ) {
                    lastItem.wait( timeout );
                }
                if ( lastItem.link != null && ! lastItem.obsolete )
                    return lastItem.link;
                long now = System.currentTimeMillis();
                timeout -= ( now - start );
                if ( timeout <= 0 )
                    return null;
                start = now;
                if ( lastItem.obsolete )
                    lastItem = head;
            }
        }
    }

    /**
      * Get the next item from the topic, waiting until an item
      * becomes available.  Returns a JaxcentTopicItem, from
      * which the item and its creation date can be retrieved,
      * and which must be used for waiting for the next item.
      * <P>The first time through, lastItem should be null.  After that,
      * it must be the last item returned.
      */
    public JaxcentTopicItem getItem( JaxcentTopicItem lastItem ) throws InterruptedException
    {
        checkObsolete();
        if ( lastItem == null || lastItem.obsolete )
            lastItem = head;
        for (;;) {
            synchronized ( lastItem ) {
                if ( lastItem.link == null && ! lastItem.obsolete ) {
                    lastItem.wait( 50000 );  // Forever waits in Java seem to miss the "notifyAll" sometimes.
                }
                if ( lastItem.link != null && ! lastItem.obsolete )
                    return lastItem.link;
                if ( lastItem.obsolete )
                    lastItem = head;
            }
        }
    }
}
