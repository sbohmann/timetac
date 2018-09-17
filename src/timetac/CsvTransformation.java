package timetac;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvTransformation {
    private final File file;

    private CsvTransformation(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected one argument: input csv file path");
        }
        file = new File(args[0]);
        if (!file.isFile()) {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) throws IOException {
        new CsvTransformation(args).run();
    }

    private void run() throws IOException {
        BufferedReader in = createReader();
        ParsedLine.ignoreNonMatchingLine(in.readLine());
        List<ParsedLine> lines = in.lines()
                .map(ParsedLine::new)
                .collect(Collectors.toList());

        lines.forEach(line -> System.out.println(line.date + " - " + line.duration));

        double sum = lines
                .stream()
                .map(line -> line.duration)
                .reduce(0.0, (lhs, rhs) -> lhs + rhs);
        System.out.println("Sum: " + sum);

        LocalDate lastDate = null;
        for (ParsedLine line : lines) {
            if (!line.date.equals(lastDate)) {
                System.out.println(";;");
            }
            System.out.println(line.date + ";" + line.startTime.toLocalTime() + ";" + line.endTime.toLocalTime());
            lastDate = line.date;
        }
    }

    private BufferedReader createReader() throws FileNotFoundException {
        return new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8));
    }
}
