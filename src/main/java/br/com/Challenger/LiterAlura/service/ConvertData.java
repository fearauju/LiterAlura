package br.com.Challenger.LiterAlura.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ConvertData implements IConvertData {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public <T> List<T> fetchList(String json, Class<T> clazz) {
        try {
            // Use TypeReference para definir o tipo de Map<String, Object>
            Map<String, Object> responseMap = objectMapper.readValue(
                    json,
                    new TypeReference<Map<String, Object>>() {}
            );

            // Obter a lista com o tipo correto usando TypeReference
            List<Map<String, Object>> results = objectMapper.convertValue(
                    responseMap.get("results"),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            // Converter cada item da lista para o tipo específico
            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz);
            return objectMapper.convertValue(results, listType);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON: " + e.getMessage(), e);
        }
    }

    public static boolean isJsonEmpty(String jsonString) {
        try {
            if (jsonString == null || jsonString.trim().isEmpty()) return true;

            JsonNode jsonNode = objectMapper.readTree(jsonString);
            JsonNode resultsNode = jsonNode.path("results");
            return resultsNode.isMissingNode() || (resultsNode.isArray() && resultsNode.isEmpty());
        } catch (IOException e) {
            throw new RuntimeException("Error checking JSON: " + e.getMessage(), e);
        }
    }
}
