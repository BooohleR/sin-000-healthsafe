package co.wethinkcode.healthsafe;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WardCsvReaderTest {
    @Test
    void shouldReadAllWardRecordsFromCsv() throws Exception {

        WardCsvReader reader = new WardCsvReader();

        List<WardRecord> wards = reader.read("src/main/resources/wards-outdated.csv");

        assertEquals(18, wards.size());
    }

}