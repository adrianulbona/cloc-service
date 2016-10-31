package io.adrianulbona.cloc.service;

import ch.hsr.geohash.GeoHash;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.adrianulbona.cloc.CountryLocator;
import lombok.Data;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ch.hsr.geohash.GeoHash.withCharacterPrecision;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static spark.Spark.*;

/**
 * Created by adrianulbona on 08/08/16.
 */
public class ClocService {

	private final Gson gson;
	private final CountryLocator countryLocator;

	public static void main(String[] args) throws IOException {
		new ClocService();
	}

	public ClocService() throws IOException {
		this.gson = new Gson();
		this.countryLocator = CountryLocator.fromFreshIndex();
		get("/locate", this::locate, this.gson::toJson);
		get("/locate/:lat/:long", this::locateLatLong, this.gson::toJson);
		exception(JsonSyntaxException.class, (e, req, res) -> halt(SC_BAD_REQUEST, req.body()));
	}

	private List<Location> locate(Request req, Response res) {
		final Coordinate coordinate = this.gson.fromJson(req.body(), Coordinate.class);
		return locate(coordinate);
	}

	private List<Location> locateLatLong(Request req, Response res) {
		final Double latitude = Double.valueOf(req.params("lat"));
		final Double longitude = Double.valueOf(req.params("long"));
		return locate(new Coordinate(latitude, longitude));
	}

	private List<Location> locate(Coordinate coordinate) {
		final GeoHash geoHash = withCharacterPrecision(coordinate.getLat(), coordinate.getLon(), 6);
		return this.countryLocator.locate(geoHash.toBase32())
				.stream()
				.map(Location::new)
				.collect(Collectors.toList());
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
