package timetac;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
        double sum = in.lines()
                .map(line -> {
                    ParsedLine data = new ParsedLine(line);
                    System.out.println(data.duration);
                    return data.duration;
                })
                .reduce(0.0, (lhs, rhs) -> lhs + rhs);
        System.out.println("Sum: " + sum);
    }

    private BufferedReader createReader() throws FileNotFoundException {
        return new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8));
    }
}
