package io.swagger.parser.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by russellb337 on 7/14/15.
 */
public class DeserializationUtils {
    public static JsonNode deserializeIntoTree(String contents, String fileOrHost) {
        JsonNode result;

        try {
            if (isJson(contents)) {
                result = Json.mapper().readTree(contents);
            } else {
                result = readYamlTree(contents);
            }
        } catch (IOException e) {
            throw new RuntimeException("An exception was thrown while trying to deserialize the contents of " + fileOrHost + " into a JsonNode tree", e);
        }

        return result;
    }

    public static <T> T deserialize(Object contents, String fileOrHost, Class<T> expectedType) {
        T result;

        boolean isJson = false;

        if(contents instanceof String && isJson((String)contents)) {
            isJson = true;
        }

        try {
            if (contents instanceof String) {
                if (isJson) {
                    result = Json.mapper().readValue((String) contents, expectedType);
                } else {
                    result = Yaml.mapper().readValue((String) contents, expectedType);
                }
            } else {
                result = Json.mapper().convertValue(contents, expectedType);
            }
        } catch (Exception e) {
            throw new RuntimeException("An exception was thrown while trying to deserialize the contents of " + fileOrHost + " into type " + expectedType, e);
        }

        return result;
    }

    private static boolean isJson(String contents) {
        return contents.toString().trim().startsWith("{");
    }

    public static JsonNode readYamlTree(String contents) {
        String pattern = "&";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(contents);
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml(new SafeConstructor());
        if (!m.lookingAt()) {
            return Json.mapper().convertValue(yaml.load(contents), JsonNode.class);

        }
        return null;
    }

    public static <T> T readYamlValue(String contents, Class<T> expectedType) {
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml(new SafeConstructor());
        return Json.mapper().convertValue(yaml.load(contents), expectedType);

    }
}
