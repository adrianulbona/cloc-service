package io.adrianulbona.cloc.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Data;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;

import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

/**
 * Created by adrianulbona on 08/08/16.
 */
public class ClocService {

    private final Gson gson;

    public static void main(String[] args) {
        new ClocService();
    }

    public ClocService() {
        this.gson = new Gson();
        get("/locate", this::locate, this.gson::toJson);
        exception(JsonSyntaxException.class, (e, req, res) -> halt(SC_BAD_REQUEST, req.body()));
    }

    private Location locate(Request req, Response res) {
        final Coordinate coordinate = this.gson.fromJson(req.body(), Coordinate.class);
        return locate(coordinate);
    }

    private Location locate(Coordinate coordinate) {
        return new Location("not-implemented-yet");
    }

    @Data
    public static class Coordinate {
        private final double lat;
        private final double lon;
    }

    @Data
    public static class Location {
        private final String countryCode;
    }
}
