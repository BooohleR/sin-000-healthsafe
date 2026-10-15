package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WardCsvReader {

    public List<WardRecord> read(String filePath) throws Exception {
        try (Reader reader = new FileReader(filePath)) {
            return readFromReader(reader);
        }
    }

    public List<WardRecord> readFromResources(String resourceName)
            throws Exception {

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(resourceName);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "CSV resource not found: " + resourceName
            );
        }

        try (Reader reader = new InputStreamReader(
                inputStream, StandardCharsets.UTF_8)) {
            return readFromReader(reader);
        }
    }

    private List<WardRecord> readFromReader(Reader reader)
            throws Exception {

        List<WardRecord> wards = new ArrayList<>();
        Set<String> seenWardIds = new HashSet<>();

        try (CSVReader csvReader = new CSVReader(reader)) {

            String[] row;

            // Skip the CSV header
            csvReader.readNext();

            while ((row = csvReader.readNext()) != null) {

                // Skip malformed rows that do not have four columns
                if (row.length != 4) {
                    System.err.println(
                            "Skipping malformed CSV row: incorrect column count"
                    );
                    continue;
                }

                WardRecord ward = WardDataNormalizer.normalizeWard(
                        row[0],
                        row[1],
                        row[2],
                        row[3]
                );

                // Keep the first occurrence of each ward ID
                if (seenWardIds.add(ward.getWardId())) {
                    wards.add(ward);
                } else {
                    System.err.println(
                            "Skipping duplicate ward ID: "
                                    + ward.getWardId()
                    );
                }
            }
        }

        return wards;
    }
}