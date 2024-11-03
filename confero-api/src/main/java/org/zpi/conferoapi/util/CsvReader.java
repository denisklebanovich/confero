package org.zpi.conferoapi.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CsvReader {

    @SneakyThrows
    public static List<String> readEmailList(MultipartFile file) {
        var emails = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord record : csvParser) {
                if (!record.get(0).trim().isEmpty()) {  // Assuming one email per line
                    emails.add(record.get(0).trim());
                }
            }
        }
        return emails;
    }
}
