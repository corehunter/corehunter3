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

package org.corehunter.data;

/**
 * Specifies the format in which a symmetric matrix is encoded in a file.
 * Values are always stored row-wise. The format indicates whether redundant
 * values are included or not.
 * 
 * @author Herman De Beukelaer
 */
public enum SymmetricMatrixFormat {

    FULL,           // full matrix
    LOWER,          // lower triangular part without diagonal
    LOWER_DIAG,     // lower triangular part with diagonal
    
}
