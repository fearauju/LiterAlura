package br.com.Challenger.LiterAlura.service;

import java.util.List;

public interface IConvertData {

    <T> T fetchData(String json, Class<T> classe);
    <T> List<T> fetchList(String json, Class<T> classe);
    <T> List<T> fetchListSimple(String json, Class<T> classe);
}
