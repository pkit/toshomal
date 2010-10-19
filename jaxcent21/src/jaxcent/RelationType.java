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
 * Module Name:           RelationType.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Node relations for searches.
 *
 * Change History:
 *
 *     2/18/2008  MP      Initial version.
 *
 */

package jaxcent;

/**
  * RelationType is a (backwards compatible) enumeration class.  It provides
  * options for searching for HTML tag elements on the page using node relations
  * such as "next sibling", "previous sibling".  These are not available
  * for all searches, but are only provided in a limited fashion, therefore
  * the SearchType class doesn't include these.
  */

public class RelationType {

    static final int SEARCH_NEXT_SIBLING = 41;
    static final int SEARCH_PREV_SIBLING = 42;
    static final int SEARCH_PARENT_NODE = 43;
    static final int SEARCH_FIRST_CHILD = 44;
    static final int SEARCH_LAST_CHILD = 45;
    static final int SEARCH_FIRST_LI = 51;
    static final int SEARCH_NEXT_LI = 52;

    int relationType = 41;

    private RelationType( int t ) { relationType = t; }

    /**
      * Find next sibling node.
      */

    public static RelationType nextSibling = new RelationType( SEARCH_NEXT_SIBLING );

    /**
      * Find previous sibling node.
      */

    public static RelationType previousSibling = new RelationType( SEARCH_PREV_SIBLING );

    /**
      * Find parent node.
      */

    public static RelationType parentNode = new RelationType( SEARCH_PARENT_NODE );

    /**
      * Find first child node (of a parent node.)
      */

    public static RelationType firstChild = new RelationType( SEARCH_FIRST_CHILD );
    /**
      * Search by the tag name.
      */

    /**
      * Find last child node (of a parent node.)
      */

    public static RelationType lastChild = new RelationType( SEARCH_LAST_CHILD );
}
