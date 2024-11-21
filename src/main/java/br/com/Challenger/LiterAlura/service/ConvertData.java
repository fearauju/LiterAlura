package br.com.Challenger.LiterAlura.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;
import java.util.Map;

public class ConvertData implements IConvertData {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T fetchData(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> fetchList(String json, Class<T> classe) {
        try {
            Map<String, Object> responseMap = mapper.readValue(json, Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) responseMap.get("results");

            CollectionType lista = mapper.getTypeFactory().constructCollectionType(List.class, classe);
            return mapper.convertValue(results, lista);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> fetchListSimple(String json, Class<T> classe) {
        try {
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, classe);
            return mapper.readValue(json, listType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
