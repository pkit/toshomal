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
 * Module Name:           SearchType.java
 * Original Author:       Mukesh Prasad
 * Brief Description:     Different types of searches, include create-new.
 *
 * Change History:
 *
 *    12/24/2007  MP      Initial version.
 *
 */

package jaxcent;

/**
  * SearchType is a (backwards compatible) enumeration class.  It provides
  * various options for searching for HTML tag elements on the page.
  * Note that "search" in this context may not actually be a search, because
  * this class also provides a "createNew" enumeration!
  */

public class SearchType {
    static final int SEARCH_BY_ID = 1;
    static final int SEARCH_BY_TAG = 2;
    static final int SEARCH_BY_NAME = 3;
    static final int SEARCH_INPUT_BY_TYPE = 4;
    static final int SEARCH_INPUT_BY_VALUE = 5;
    static final int SEARCH_BY_CLASSNAME = 6;

    static final int CREATE_NEW = 7;

    static final int SEARCH_FIRST_TAG = 8;
    static final int SEARCH_NEXT_TAG = 9;

    static final int SEARCH_WINDOW = 11;
    static final int SEARCH_SCREEN = 12;
    static final int SEARCH_DOCUMENT = 13;
    static final int SEARCH_BODY = 14;
    static final int SEARCH_LOCATION = 15;

    static final int SEARCH_TABLE_ROW = 21;
    static final int SEARCH_TABLE_CELL = 22;
    static final int SEARCH_ROW_CELL = 23;

    static final int SEARCH_SELECT_OPTION = 24;

    static final int CREATE_ROW = 31;
    static final int CREATE_CELL = 32;
    static final int CREATE_OPTION = 33;

    static final int SEARCH_NEXT_SIBLING = 41;
    static final int SEARCH_PREV_SIBLING = 42;
    static final int SEARCH_PARENT_NODE = 43;
    static final int SEARCH_FIRST_CHILD = 44;
    static final int SEARCH_LAST_CHILD = 45;
    static final int SEARCH_FIRST_LI = 51;
    static final int SEARCH_NEXT_LI = 52;

    int searchType = 0;

    private SearchType( int t ) { searchType = t; }

    /**
      * Search by the "ID" attribute.  This is the recommended search method.
      */

    public static SearchType searchById = new SearchType( SEARCH_BY_ID );

    /**
      * Search by the tag name.
      */

    public static SearchType searchByTag = new SearchType( SEARCH_BY_TAG );

    /**
      * Search by the NAME attribute.
      */

    public static SearchType searchByName = new SearchType( SEARCH_BY_NAME );

    /**
      * Search INPUT elements by TYPE.
      */

    public static SearchType searchInputByType = new SearchType( SEARCH_INPUT_BY_TYPE );

    /**
      * Search INPUT elements by VALUE.  Useful in cases like multiple SUBMIT buttons, checkboxes,
      * or radio buttons etc.
      */

    public static SearchType searchInputByValue = new SearchType( SEARCH_INPUT_BY_VALUE );
    // public static SearchType searchByClassName = new SearchType( SEARCH_BY_CLASSNAME );

    /**
      * createNew is for creating new elements on the page.  It is not really a search!
      * The newly created element then needs to be inserted at the appopriate place in the page.
      */

    public static SearchType createNew = new SearchType( CREATE_NEW );
}
