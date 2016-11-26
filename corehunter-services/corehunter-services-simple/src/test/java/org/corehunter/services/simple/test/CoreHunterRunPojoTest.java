package org.corehunter.services.simple.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.corehunter.services.CoreHunterRunStatus;
import org.corehunter.services.simple.CoreHunterRunPojo;
import org.junit.Test;

public class CoreHunterRunPojoTest {
    
    DateTimeFormatter[] INVALID_FORMATS = new  DateTimeFormatter[] {
            DateTimeFormatter.BASIC_ISO_DATE,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_TIME,
            DateTimeFormatter.ISO_ORDINAL_DATE,
            DateTimeFormatter.ISO_TIME,
            DateTimeFormatter.ISO_WEEK_DATE,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.RFC_1123_DATE_TIME        
    } ;
    
    DateTimeFormatter[] VALID_FORMATS = new  DateTimeFormatter[] {
            DateTimeFormatter.ISO_INSTANT     
    } ;
    

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
    
    @Test
    public void testValidDataFormatters() {
        CoreHunterRunPojo pojo = new CoreHunterRunPojo("id", "name") ;
        
        for (int i = 0 ; i < VALID_FORMATS.length ; ++i) {
            try {
                System.out.println(VALID_FORMATS[i].format(pojo.getStartInstant())) ;
            } catch (Exception e) {
                fail(String.format("Format failed {} {}", (i + 1), VALID_FORMATS[i].toFormat().toString()));
            }
        }
    }
    
    @Test
    public void testInValidDataFormatters() {
        CoreHunterRunPojo pojo = new CoreHunterRunPojo("id", "name") ;
        
        for (int i = 0 ; i < INVALID_FORMATS.length ; ++i) {
            try {
                INVALID_FORMATS[i].format(pojo.getStartInstant()) ;
                fail(String.format("In valid Format passed {} {}", (i + 1), INVALID_FORMATS[i].toFormat().toString()));
            } catch (Exception e) {
                
            }
        }
    }

}
