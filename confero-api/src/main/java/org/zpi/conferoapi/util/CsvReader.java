package org.zpi.conferoapi.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class CsvReader {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static List<String> readEmailList(MultipartFile file) throws IOException {
        var emails = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord record : csvParser) {
                if (record.size() != 1) {
                    throw new IOException("Invalid CSV format: file should contain only one column of emails.");
                }

                String email = record.get(0).trim();
                if (!email.isEmpty()) {
                    if (!EMAIL_PATTERN.matcher(email).matches()) {
                        throw new IOException("Invalid email format: " + email);
                    }
                    emails.add(email);
                }
            }
        }
        return emails;
    }
}
