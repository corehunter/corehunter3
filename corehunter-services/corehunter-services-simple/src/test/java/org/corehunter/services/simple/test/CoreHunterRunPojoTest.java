package org.corehunter.services.simple.test;

import static org.junit.Assert.*;

import java.time.Instant;

import org.corehunter.services.CoreHunterRunStatus;
import org.corehunter.services.simple.CoreHunterRunPojo;
import org.junit.Test;

public class CoreHunterRunPojoTest {

    @Test
    public void testCoreHunterRunPojoString() {
        
        CoreHunterRunPojo pojo = new CoreHunterRunPojo("name") ;
        
        assertNotNull(pojo.getUniqueIdentifier()) ;
        assertEquals("name", pojo.getName()) ;
        assertNotNull(pojo.getStartInstant()) ;
        assertNull(pojo.getEndInstant()) ;
        assertEquals(CoreHunterRunStatus.NOT_STARTED, pojo.getStatus()) ;
    }

    @Test
    public void testCoreHunterRunPojoStringString() {
        CoreHunterRunPojo pojo = new CoreHunterRunPojo("id", "name") ;
        
        assertEquals("id", pojo.getUniqueIdentifier()) ;
        assertEquals("name", pojo.getName()) ;
        assertNotNull(pojo.getStartInstant()) ;
        assertNull(pojo.getEndInstant()) ;
        assertEquals(CoreHunterRunStatus.NOT_STARTED, pojo.getStatus()) ;
    }

    @Test
    public void testSetStartInstant() {
        CoreHunterRunPojo pojo = new CoreHunterRunPojo("id", "name") ;
        
        assertNotNull(pojo.getStartInstant()) ;
        pojo.setStartInstant(Instant.now());
        assertNotNull(pojo.getStartInstant()) ;
    }

    @Test
    public void testSetEndInstant() {
        CoreHunterRunPojo pojo = new CoreHunterRunPojo("id", "name") ;
        
        assertNull(pojo.getEndInstant()) ;
        pojo.setEndInstant(Instant.now());
        assertNotNull(pojo.getEndInstant()) ;
    }

    @Test
    public void testSetStatus() {
        CoreHunterRunPojo pojo = new CoreHunterRunPojo("id", "name") ;
        
        assertEquals(CoreHunterRunStatus.NOT_STARTED, pojo.getStatus()) ;
        pojo.setStatus(CoreHunterRunStatus.FINISHED);
        assertEquals(CoreHunterRunStatus.FINISHED, pojo.getStatus()) ;
    }

}
