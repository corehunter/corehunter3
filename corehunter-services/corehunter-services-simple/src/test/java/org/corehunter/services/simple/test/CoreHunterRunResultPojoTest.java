package org.corehunter.services.simple.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.corehunter.services.CoreHunterRunStatus;
import org.corehunter.services.simple.CoreHunterRunResultPojo;
import org.junit.Test;

public class CoreHunterRunResultPojoTest {

    @Test
    public void testCoreHunterRunResultPojo() {
        CoreHunterRunResultPojo pojo = new CoreHunterRunResultPojo(new MockCoreHunterRunResultPojo("id", "name")) ;
        
        assertEquals("id", pojo.getUniqueIdentifier()) ;
        assertEquals("name", pojo.getName()) ;
        assertNotNull(pojo.getStartInstant()) ;
        assertNotNull(pojo.getEndInstant()) ;
        assertEquals(CoreHunterRunStatus.FINISHED, pojo.getStatus()) ;
        assertEquals("ErrorMessage", pojo.getErrorMessage()) ;
        assertEquals("ErrorStream", pojo.getErrorStream()) ;
        assertEquals("OutputStream", pojo.getOutputStream()) ;
    }

}
