package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class WardCsvReader {

    public List<WardRecord> read(String filePath) throws Exception {

        List<WardRecord> wards = new ArrayList<>();

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

            wards.add(ward);
        }

        csvReader.close();

        return wards;
    }

}