
package org.corehunter.services.simple;

import org.corehunter.services.CoreHunterRunArguments;
import org.corehunter.services.CoreHunterRunResult;
import org.corehunter.services.CoreHunterRunStatus;
import org.jamesframework.core.subset.SubsetSolution;
import org.joda.time.DateTime;

import uno.informatics.data.pojo.SimpleEntityPojo;

public class CoreHunterRunResultPojo extends SimpleEntityPojo implements CoreHunterRunResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String outputStream;
    private String errorStream;
    private String errorMessage;
    private SubsetSolution subsetSolution;
    private DateTime startDate;
    private DateTime endDate;
    private CoreHunterRunStatus status;
    private CoreHunterRunArguments arguments;

    public CoreHunterRunResultPojo(CoreHunterRunResult result) {
        super(result);

        outputStream = result.getOutputStream();
        errorStream = result.getErrorStream();
        errorMessage = result.getErrorMessage();
        subsetSolution = new SubsetSolution(result.getSubsetSolution());
        startDate = result.getStartDate();
        endDate = result.getEndDate();
        status = result.getStatus();
        arguments = result.getArguments();
    }

    @Override
    public String getOutputStream() {
        return outputStream;
    }

    @Override
    public String getErrorStream() {
        return errorStream;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public SubsetSolution getSubsetSolution() {
        return subsetSolution;
    }

    @Override
    public DateTime getStartDate() {
        return startDate;
    }

    @Override
    public DateTime getEndDate() {
        return endDate;
    }

    @Override
    public CoreHunterRunStatus getStatus() {
        return status;
    }

    @Override
    public CoreHunterRunArguments getArguments() {
        return arguments;
    }

}
