package br.com.Challenger.LiterAlura.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PeopleDetails( @JsonProperty("authors") List<Map<String, String>> author
)
{ }
