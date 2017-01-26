
package org.corehunter.services;

import org.jamesframework.core.subset.SubsetSolution;
import java.time.Instant;

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

    Instant getStartInstant();

    Instant getEndInstant();

    CoreHunterRunStatus getStatus();

    CoreHunterRunArguments getArguments();

}