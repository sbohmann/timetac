package timetac;

import java.time.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ParsedLine {
    private static final Pattern linePattern;

    final LocalDate date;
    final LocalDateTime startTime;
    final LocalDateTime endTime;
    final double duration;

    static {
        String other = "[^;]*";
        String date = "\\d{4}-\\d{2}-\\d{2}";
        String time = "\\d{2}:\\d{2}:\\d{2}";
        String duration = "\\d+(?:\\.\\d*)?";
        String linePatternText = other + ";" + other + ";" + other + ";" + other + ";" +
                "(" + date + ");" +
                "(" + date + ") (" + time + ");" +
                "(" + date + ") (" + time + ");" +
                "(" + duration + ");" +
                other + ";";
        linePattern = Pattern.compile(linePatternText);
    }

    static void ignoreNonMatchingLine(String line) {
        if (linePattern.matcher(line).matches()) {
            throw new IllegalArgumentException("Line actually matches: [" + line + "]");
        }
        System.out.println("Ignoring non-matching line [" + line + "]");
    }

    ParsedLine(String line) {
        Matcher matcher = linePattern.matcher(line);
        if (!matcher.matches()) {
            System.err.println(linePattern);
            throw new IllegalArgumentException("Line does not match: [" + line + "]");
        }
        date = readDate(matcher.group(1));
        startTime = readDateTime(matcher.group(2), matcher.group(3));
        endTime = readDateTime(matcher.group(4), matcher.group(5));
        duration = Double.parseDouble(matcher.group(6));
        checkConsistency();
    }

    private static LocalDate readDate(String dateAsText) {
        return LocalDate.parse(dateAsText);
    }

    private static LocalDateTime readDateTime(String dateAsText, String timeAstext) {
        LocalDate date = LocalDate.parse(dateAsText);
        LocalTime time = LocalTime.parse(timeAstext);
        return LocalDateTime.of(date, time);
    }

    private void checkConsistency() {
        checkDate(startTime, "Start");
        checkDate(endTime, "End");
        Duration delta = Duration.between(startTime, endTime);
        double calculatedDuration = delta.toHours() +
                delta.toMinutesPart() / 60.0;
        if (calculatedDuration != duration) {
            throw new IllegalArgumentException("Inconsistent calated [" + calculatedDuration + "]" +
                    " and parsed [" + duration + "] durations");
        }
    }

    private void checkDate(LocalDateTime time, String name) {
        if (!startTime.toLocalDate().equals(date)) {
            throw new IllegalArgumentException(name + "date " + startTime.toLocalDate() +
                    " inconsistent with date " + date);
        }
    }
}
