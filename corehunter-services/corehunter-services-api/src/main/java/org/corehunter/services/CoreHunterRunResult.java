
package org.corehunter.services;

import org.jamesframework.core.subset.SubsetSolution;
import org.joda.time.DateTime;

import uno.informatics.data.SimpleEntity;

/**
 * Provides the results from a CoreHunter Run
 * 
 * @author daveneti
 *
 */
public interface CoreHunterRunResult extends SimpleEntity {

    String getOutputStream();

    String getErrorStream();

    String getErrorMessage();

    SubsetSolution getSubsetSolution();

    DateTime getStartDate();

    DateTime getEndDate();

    CoreHunterRunStatus getStatus();

    CoreHunterRunArguments getArguments();

}