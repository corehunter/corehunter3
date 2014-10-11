/*******************************************************************************
 * Copyright 2014 Guy Davenport Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package org.corehunter.tests;

import java.util.Set;
import java.util.TreeSet;

import org.corehunter.DistanceMatrixData;
import org.corehunter.simple.SimpleDistanceMatrixData;

/**
 * @author Guy Davenport
 */
public class TestData
{
	protected static final double[][]	        DISTANCES	= new double[][] {
	    new double[] { 1.0, 0.8, 0.6, 0.4, 0.2 },
	    new double[] { 0.8, 1.0, 0.8, 0.6, 0.4 },
	    new double[] { 0.6, 0.8, 1.0, 0.9, 0.6 },
	    new double[] { 0.4, 0.6, 0.9, 1.0, 0.8 },
	    new double[] { 0.2, 0.4, 0.6, 0.8, 1.0 }	      };

	protected static final String[]	          NAMES	    = new String[] { "Name1",
	    "Name2", "Name3", "Name4", "Name5"	            };

	protected static final Set<Integer>	      SET	      = new TreeSet<Integer>();

	static
	{
		SET.add(0);
		SET.add(1);
		SET.add(2);
		SET.add(3);
		SET.add(4);
	}

	protected static final Set<Integer>	      SUBSET	  = new TreeSet<Integer>();

	static
	{
		SUBSET.add(2);
		SUBSET.add(3);
	}

	protected static final DistanceMatrixData	DATA	    = new SimpleDistanceMatrixData(
	                                                        NAMES, DISTANCES);

}
