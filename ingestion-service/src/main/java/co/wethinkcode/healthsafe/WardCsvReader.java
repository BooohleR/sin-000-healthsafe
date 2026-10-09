package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class WardCsvReader {

    public List<WardRecord> read(String filePath) throws Exception {

        List<WardRecord> wards = new ArrayList<>();

        Set<String> seenWardIds = new HashSet<>();

        CSVReader csvReader = new CSVReader(new FileReader(filePath));

        String[] row;

        csvReader.readNext();

        while ((row = csvReader.readNext()) != null) {

            WardRecord ward = WardDataNormalizer.normalizeWard(
                    row[0],
                    row[1],
                    row[2],
                    row[3]
            );

            if (seenWardIds.add(ward.getWardId())) {
                wards.add(ward);
            }
        }

        csvReader.close();

        return wards;
    }

}