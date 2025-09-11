package com.tshop.configurations.logging;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

public class PrettySqlFormatter implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed,
                                String category, String prepared, String sql, String url) {
        if (sql == null || sql.trim().isEmpty()) return "";

        String formattedSql = formatSql(sql, category);
        String callerClass = findCallerClass();

        return String.format("%n--- SQL (executed in %d ms) ---%nClass: %s%n%s%n", elapsed, callerClass, formattedSql);
    }

    private String formatSql(String sql, String category) {
        if (category.equalsIgnoreCase("statement")) {
            return FormatStyle.BASIC.getFormatter().format(sql);
        }
        return sql;
    }

    private String findCallerClass() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (className.contains("PrettySqlFormatter")) {
                continue;
            }
            return className + "#" + element.getMethodName();
        }
        return "UnknownCaller";
    }
}
