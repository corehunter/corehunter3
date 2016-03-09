/*--------------------------------------------------------------*/
/* Licensed to the Apache Software Foundation (ASF) under one   */
/* or more contributor license agreements.  See the NOTICE file */
/* distributed with this work for additional information        */
/* regarding copyright ownership.  The ASF licenses this file   */
/* to you under the Apache License, Version 2.0 (the            */
/* "License"); you may not use this file except in compliance   */
/* with the License.  You may obtain a copy of the License at   */
/*                                                              */
/*   http://www.apache.org/licenses/LICENSE-2.0                 */
/*                                                              */
/* Unless required by applicable law or agreed to in writing,   */
/* software distributed under the License is distributed on an  */
/* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       */
/* KIND, either express or implied.  See the License for the    */
/* specific language governing permissions and limitations      */
/* under the License.                                           */
/*--------------------------------------------------------------*/

package org.corehunter.util;

import java.util.Arrays;

/**
 * @author Herman De Beukelaer
 */
public class StringUtils {

    private StringUtils(){}
    
    /**
     * Removes single or double quote characters from a string.
     * Only removes quotes at the beginning and end of a string
     * and only if both are single or double quotes.
     * 
     * @param str string to unquote
     * @return unquoted string, or <code>null</code> if input was <code>null</code>
     */
    public static String unquote(String str){
        if(str != null
             && ((str.startsWith("'") && str.endsWith("'"))
             || (str.startsWith("\"") && str.endsWith("\"")))){
            str = str.substring(1, str.length()-1);
        }
        return str;
    }
    
    /**
     * Trim leading and trailing whitespace from the given string.
     * 
     * @param str string to trim
     * @return trimmed string, or <code>null</code> if input was <code>null</code>
     */
    public static String trim(String str){
        if(str != null){
            str = str.trim();
        }
        return str;
    }
    
    /**
     * First trim and then unquote the given string.
     * If the input is <code>null</code> or the unquoted, trimmed
     * string consists of space only, <code>null</code> is returned.
     * 
     * @param str string to trim and unquote
     * @return trimmed, unquoted string; <code>null</code> if input was <code>null</code>
     *         or resulting string consists of spaces only
     */
    public static String trimAndUnquote(String str){
        str = unquote(trim(str));
        if(str != null && trim(str).equals("")){
            return null;
        }
        return str;
    }
    
    /**
     * Trim and unquote all string in the given array.
     * 
     * @param str string array
     * @return array with unquoted, trimmed strings
     */
    public static String[] trimAndUnquote(String[] str){
        return Arrays.stream(str)
                     .map(StringUtils::trimAndUnquote)        
                     .toArray(n -> new String[n]);
    }
    
    /**
     * Checks whether the given string is blank. A string is defined to be blank if it is
     * <code>null</code> or if it consists of spaces only after being trimmed and unquoted.
     * 
     * @param str string to check
     * @return <code>true</code> if the string is blank
     */
    public static boolean isBlank(String str){
        return trimAndUnquote(str) == null;
    }
    
}
