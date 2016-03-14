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
     * and only if both are single or double quotes. If an empty
     * string or empty quotes are given as input, the result
     * is <code>null</code>.
     * 
     * @param str string to unquote
     * @return unquoted string, or <code>null</code> if input was
     *         empty quotes or <code>null</code>
     */
    public static String unquote(String str){
        if(str != null
             && ((str.startsWith("'") && str.endsWith("'"))
             || (str.startsWith("\"") && str.endsWith("\"")))){
            str = str.substring(1, str.length()-1);
        }
        if(str != null && str.equals("")){
            str = null;
        }
        return str;
    }
    
    /**
     * Unquote all strings in the given array.
     * 
     * @param str string array
     * @return array with unquoted strings
     */
    public static String[] unquote(String[] str){
        return Arrays.stream(str)
                     .map(StringUtils::unquote)        
                     .toArray(n -> new String[n]);
    }
    
    /**
     * Checks whether the given string is blank. A string is considered blank if it is
     * <code>null</code>, empty or consists of empty quotes (single or double).
     * 
     * @param str string to check
     * @return <code>true</code> if the string is blank
     */
    public static boolean isBlank(String str){
        return unquote(str) == null;
    }
    
}
