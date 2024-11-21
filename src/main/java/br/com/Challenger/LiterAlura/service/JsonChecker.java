package br.com.Challenger.LiterAlura.service;

import br.com.Challenger.LiterAlura.model.Book;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonChecker {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean isJsonEmpty(String jsonString) throws IOException {

        if (jsonString == null || jsonString.trim().isEmpty()) {
            System.out.println("O JSON é nulo ou vazio.");
            return true;
        }


        JsonNode jsonNode = objectMapper.readTree(jsonString);

        System.out.println("Estrutura do JSON: " + jsonNode.toString());

        // Verifica se o campo 'results' existe e está vazio
        JsonNode resultsNode = jsonNode.path("results");
        boolean isEmpty = resultsNode.isMissingNode() || (resultsNode.isArray() && resultsNode.isEmpty());

        System.out.println("O campo 'results' está vazio? " + isEmpty);
        System.out.println();
        return isEmpty;
    }

//    public static List<String> extrairJson(List<Book> jsonResponse) throws IOException {
//        ObjectNode jsonNode = objectMapper.readTree((JsonParser) jsonResponse);
//        ArrayNode results = (ArrayNode) jsonNode.get("People");
//
//        List<String> itensJson = new ArrayList<>();
//        for (JsonNode node : results) {
//               String name = String.valueOf(node.get("Name"));
//               int birthYear = node.get("birthYear").asInt();
//               int deathYear = node.get("deathYear").asInt();
//               String json = String.format("{\"name\":\"%s\", \"birthYear\":%d, \"deathYear\":%d}", name, birthYear, deathYear);
//               itensJson.add(json);
//        }
//            return itensJson;
//    }
}
