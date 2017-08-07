package org.corehunter.data;

import java.io.IOException;
import java.nio.file.Path;
import org.corehunter.data.simple.SimpleBiAllelicGenotypeData;
import org.corehunter.data.simple.SimpleDefaultGenotypeData;
import org.corehunter.data.simple.SimpleFrequencyGenotypeData;
import uno.informatics.data.io.FileType;

public enum GenotypeDataFormat {
    
    // define formats, each with their own reader
    DEFAULT((file, type) -> SimpleDefaultGenotypeData.readData(file, type)),
    FREQUENCY((file, type) -> SimpleFrequencyGenotypeData.readData(file, type)),
    BIPARENTAL((file, type) -> SimpleBiAllelicGenotypeData.readData(file, type));
    
    @FunctionalInterface
    private interface GenotypeDataReader {
        public FrequencyGenotypeData readGenotypeData(Path filePath, FileType fileType) throws IOException;
    }
    
    private final GenotypeDataReader reader;
    
    private GenotypeDataFormat(GenotypeDataReader reader){
        this.reader = reader;
    }
    
    public FrequencyGenotypeData readData(Path filePath, FileType fileType) throws IOException {
        return reader.readGenotypeData(filePath, fileType);
    }
    
}
