package co.wethinkcode.healthsafe;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class WardCsvReaderTest {
    @Test
    void shouldReadAllWardRecordsFromCsv() throws Exception {

        WardCsvReader reader = new WardCsvReader();

        List<WardRecord> wards = reader.read("src/main/resources/wards-outdated.csv");

        assertEquals(17, wards.size());
    }

    @Test
    void shouldCleanWardDataWhenReadingCsv() throws Exception {

        WardCsvReader reader = new WardCsvReader();

        List<WardRecord> wards =
                reader.read("src/main/resources/wards-outdated.csv");

        WardRecord firstWard = wards.get(0);

        assertEquals("W-01", firstWard.getWardId());
        assertEquals("East Wing", firstWard.getWing());
        assertEquals("Cardiology", firstWard.getDepartment());
        assertEquals(3, firstWard.getBedsAvailable());
    }

    @Test
    void shouldNormalizeDirtyWardFromCsv() throws Exception {

        WardCsvReader reader = new WardCsvReader();

        List<WardRecord> wards =
                reader.read("src/main/resources/wards-outdated.csv");

        WardRecord secondWard = wards.get(1);

        assertEquals("W-02", secondWard.getWardId());
        assertEquals("West Wing", secondWard.getWing());
        assertEquals("Paediatrics", secondWard.getDepartment());
        assertNull(secondWard.getBedsAvailable());
    }

}