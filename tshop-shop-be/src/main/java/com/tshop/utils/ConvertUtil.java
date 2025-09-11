package com.tshop.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Log4j2
public class ConvertUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    public static Long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        return localDateTime != null
                ? localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
                : null;
    }

    public static Long convertEpochToMillis(Long epochSeconds) {
        return Instant.ofEpochSecond(epochSeconds).toEpochMilli() + 1000;
    }


    public static Long convertDateStringToMilliseconds(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            log.error(e);
            return null;
        }

    }

    public static Timestamp convertDateStringToTimestamp(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = sdf.parse(dateStr);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            log.error(e);
            return null;
        }
    }

    public static String snakeToCamel(String text) {
        StringBuilder result = new StringBuilder();
        boolean upperNext = false;
        for (char c : text.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else {
                if (upperNext) {
                    result.append(Character.toUpperCase(c));
                    upperNext = false;
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    public static <T> List<T> jsonNodeToList(JsonNode node, Class<T> clazz) {
        if (node == null || node.isMissingNode() || !node.isArray()) {
            return Collections.emptyList();
        }
        return mapper.convertValue(
                node,
                mapper.getTypeFactory().constructCollectionType(List.class, clazz)
        );
    }

    public static <T> List<T> parseObjToList(Object obj, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (obj instanceof String str) {
                return mapper.readValue(
                        str,
                        mapper.getTypeFactory().constructCollectionType(List.class, clazz)
                );
            }
            return mapper.convertValue(
                    obj,
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    public static JsonNode convertProtoRpToJsonNode(Message protoResponse) throws IOException {
        String jsonString = JsonFormat.printer()
                .includingDefaultValueFields()
                .print(protoResponse);
        return mapper.readTree(jsonString);
    }

    public static JsonNode convertSnakeToCamel(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = mapper.createObjectNode();
            node.fields().forEachRemaining(entry -> {
                String camelKey = toCamel(entry.getKey());
                objectNode.set(camelKey, convertSnakeToCamel(entry.getValue()));
            });
            return objectNode;
        } else if (node.isArray()) {
            ArrayNode arrayNode = mapper.createArrayNode();
            node.forEach(item -> arrayNode.add(convertSnakeToCamel(item)));
            return arrayNode;
        }
        return node;
    }

    private static String toCamel(String snake) {
        StringBuilder result = new StringBuilder();
        boolean upperCaseNext = false;
        for (char c : snake.toCharArray()) {
            if (c == '_') {
                upperCaseNext = true;
            } else {
                result.append(upperCaseNext ? Character.toUpperCase(c) : c);
                upperCaseNext = false;
            }
        }
        return result.toString();
    }


    private ConvertUtil() {
    }

}
