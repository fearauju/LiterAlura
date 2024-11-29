package br.com.Challenger.LiterAlura.service;

import java.util.List;

public interface IConvertData {

    <T> List<T> fetchList(String json, Class<T> classe);

}
