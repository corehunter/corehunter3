package org.corehunter.services.simple.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.corehunter.CoreHunterObjective;
import org.corehunter.CoreHunterObjectiveType;
import org.corehunter.services.simple.CoreHunterRunArgumentsPojo;
import org.junit.Test;

public class CoreHunterRunArgumentsPojoTest {

    @Test
    public void testCoreHunterRunArgumentsPojoStringIntString() {
        
        CoreHunterRunArgumentsPojo pojo = new CoreHunterRunArgumentsPojo("name", 10, "dataset") ;
        
        assertNotNull(pojo.getUniqueIdentifier()) ;
        assertEquals("name", pojo.getName()) ;
        assertEquals(10, pojo.getSubsetSize()) ;
        assertEquals("dataset", pojo.getDatasetId()) ;
        assertEquals(-1, pojo.getMaxTimeWithoutImprovement()) ;
        assertEquals(-1, pojo.getTimeLimit()) ;
        assertNotNull(pojo.getObjectives()) ;
        assertTrue(pojo.getObjectives().isEmpty()) ;
    }

    @Test
    public void testCoreHunterRunArgumentsPojoStringIntStringCoreHunterObjectiveArray() {
        
        CoreHunterRunArgumentsPojo pojo = new CoreHunterRunArgumentsPojo("name", 10, "dataset", 
                new CoreHunterObjective(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        
        assertNotNull(pojo.getUniqueIdentifier()) ;
        assertEquals("name", pojo.getName()) ;
        assertEquals(10, pojo.getSubsetSize()) ;
        assertEquals("dataset", pojo.getDatasetId()) ;
        assertEquals(-1, pojo.getMaxTimeWithoutImprovement()) ;
        assertEquals(-1, pojo.getTimeLimit()) ;
        assertNotNull(pojo.getObjectives()) ;
        assertEquals(1, pojo.getObjectives().size()) ;
    }

    @Test
    public void testCoreHunterRunArgumentsPojoStringIntStringListOfCoreHunterObjective() {
        
        List<CoreHunterObjective> objectives = new ArrayList<CoreHunterObjective>(1) ;
        objectives.add(new CoreHunterObjective(CoreHunterObjectiveType.AV_ACCESSION_TO_NEAREST_ENTRY)) ;
        CoreHunterRunArgumentsPojo pojo = new CoreHunterRunArgumentsPojo("name", 10, "dataset", objectives) ;
        
        assertNotNull(pojo.getUniqueIdentifier()) ;
        assertEquals("name", pojo.getName()) ;
        assertEquals(10, pojo.getSubsetSize()) ;
        assertEquals("dataset", pojo.getDatasetId()) ;
        assertEquals(-1, pojo.getMaxTimeWithoutImprovement()) ;
        assertEquals(-1, pojo.getTimeLimit()) ;
        assertNotNull(pojo.getObjectives()) ;
        assertEquals(1, pojo.getObjectives().size()) ;
    }

    @Test
    public void testCoreHunterRunArgumentsPojoCoreHunterRunArguments() {
        
        CoreHunterRunArgumentsPojo pojo = 
                new CoreHunterRunArgumentsPojo(new CoreHunterRunArgumentsPojo("name", 10, "dataset")) ;
        
        assertNotNull(pojo.getUniqueIdentifier()) ;
        assertEquals("name", pojo.getName()) ;
        assertEquals(10, pojo.getSubsetSize()) ;
        assertEquals("dataset", pojo.getDatasetId()) ;
        assertEquals(-1, pojo.getMaxTimeWithoutImprovement()) ;
        assertEquals(-1, pojo.getTimeLimit()) ;
        assertNotNull(pojo.getObjectives()) ;
        assertTrue(pojo.getObjectives().isEmpty()) ;
        
        pojo.setTimeLimit(3);
        pojo.setMaxTimeWithoutImprovement(4);
        
        pojo =  new CoreHunterRunArgumentsPojo(pojo) ;
        
        assertNotNull(pojo.getUniqueIdentifier()) ;
        assertEquals("name", pojo.getName()) ;
        assertEquals(10, pojo.getSubsetSize()) ;
        assertEquals("dataset", pojo.getDatasetId()) ;
        assertEquals(4, pojo.getMaxTimeWithoutImprovement()) ;
        assertEquals(3, pojo.getTimeLimit()) ;
        assertNotNull(pojo.getObjectives()) ;
        assertTrue(pojo.getObjectives().isEmpty()) ;
    }

    @Test
    public void testSetTimeLimit() {
        
        CoreHunterRunArgumentsPojo pojo = new CoreHunterRunArgumentsPojo("name", 10, "dataset") ;
        
        assertEquals(-1, pojo.getTimeLimit()) ;
        pojo.setTimeLimit(3);
        assertEquals(3, pojo.getTimeLimit()) ;     
    }

    @Test
    public void testSetMaxTimeWithoutImprovement() {
        CoreHunterRunArgumentsPojo pojo = new CoreHunterRunArgumentsPojo("name", 10, "dataset") ;
        
        assertEquals(-1, pojo.getMaxTimeWithoutImprovement()) ;
        pojo.setMaxTimeWithoutImprovement(3);
        assertEquals(3, pojo.getMaxTimeWithoutImprovement()) ;   
    }
}
