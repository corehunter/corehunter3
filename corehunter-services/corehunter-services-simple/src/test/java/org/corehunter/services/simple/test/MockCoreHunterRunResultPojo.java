package org.corehunter.services.simple.test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.corehunter.CoreHunterObjective;
import org.corehunter.CoreHunterObjectiveType;
import org.corehunter.services.CoreHunterRunArguments;
import org.corehunter.services.CoreHunterRunResult;
import org.corehunter.services.CoreHunterRunStatus;
import org.corehunter.services.simple.CoreHunterRunArgumentsPojo;
import org.jamesframework.core.subset.SubsetSolution;

import uno.informatics.data.pojo.SimpleEntityPojo;

public class MockCoreHunterRunResultPojo extends SimpleEntityPojo implements CoreHunterRunResult {
    

    public MockCoreHunterRunResultPojo(String uniqueIdentifier, String name) {
        super(uniqueIdentifier, name);
    }

    @Override
    public String getOutputStream() {
        return "OutputStream";
    }

    @Override
    public String getErrorStream() {
        return "ErrorStream";
    }

    @Override
    public String getErrorMessage() {
        return "ErrorMessage";
    }

    @Override
    public SubsetSolution getSubsetSolution() {
        TreeSet<Integer> set = new TreeSet<Integer>() ;
        
        for (int i = 0 ; i < 10 ; ++i)
            set.add(i) ;
        
        return new SubsetSolution(set);
    }

    @Override
    public Instant getStartInstant() {
        return Instant.now();
    }

    @Override
    public Instant getEndInstant() {
        return Instant.now();
    }

    @Override
    public CoreHunterRunStatus getStatus() {
        return CoreHunterRunStatus.FINISHED;
    }

    @Override
    public CoreHunterRunArguments getArguments() {
        
        List<CoreHunterObjective> objectives = new ArrayList<CoreHunterObjective>(1) ;
        objectives.add(new CoreHunterObjective(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        CoreHunterRunArgumentsPojo pojo = new CoreHunterRunArgumentsPojo("name", 10, "dataset", objectives) ;

        return pojo ;
    }

}
