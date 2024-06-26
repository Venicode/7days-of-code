package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //Buscando os IDS dos filmes mais populares
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://imdb8.p.rapidapi.com/title/v2/get-popular-movies-by-genre?genre=adventure&limit=10"))
                .header("x-rapidapi-key", "ba28d4e6e5msh0ddc995e729898bp11066cjsn52beff912427")
                .header("x-rapidapi-host", "imdb8.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        //Convertendo em uma lista
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> titles = new ArrayList<>();
        List<String> filmsList = mapper.readValue(response.body(), new TypeReference<List<String>>(){});

        //Retirando caracteres não necessários
        for (int i = 0; i < filmsList.size(); i++) {
            String idFilm = filmsList.get(i);
            String title = idFilm.replaceAll("/title/", "");
            String newTitle = title.replaceAll("/","");
            titles.add(newTitle);
        }

        //Pesquisando e extraindo o título de cada ID de filme
        for (String title: titles) {
            HttpRequest requestFilm = HttpRequest.newBuilder()
                    .uri(URI.create("https://imdb8.p.rapidapi.com/title/v2/get-details?tconst="+title+"&country=US&language=en-US"))
                    .header("x-rapidapi-key", "0bdba97149msh0b74d48ba8a71d4p1107bejsne189cdf81eaf")
                    .header("x-rapidapi-host", "imdb8.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> responseFilm = HttpClient.newHttpClient().send(requestFilm, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapperFilm = new ObjectMapper();
            Map<String, Map> filmsMap = mapperFilm.readValue(responseFilm.body(), new TypeReference<Map<String, Map>>() {});
            Map<String, Object> data = (Map<String, Object>) filmsMap.get("data");
            Map<String, Object> titleMap = (Map<String, Object>) data.get("title");
            Map<String, Object> titleText = (Map<String, Object>) titleMap.get("titleText");
            System.out.println(titleText.get("text"));
        }
    }
}