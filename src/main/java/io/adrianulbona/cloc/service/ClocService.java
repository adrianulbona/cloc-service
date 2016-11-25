package io.adrianulbona.cloc.service;

import com.google.gson.Gson;
import io.github.adrianulbona.cloc.CountryLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;

import static ch.hsr.geohash.GeoHash.withCharacterPrecision;
import static io.github.adrianulbona.cloc.CountryLocator.fromFreshIndex;
import static java.lang.String.format;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static spark.Spark.*;

/**
 * Created by adrianulbona on 08/08/16.
 */
public class ClocService {

	private final static Logger LOGGER = LoggerFactory.getLogger(ClocService.class);

	private final CountryLocator countryLocator;

	public static void main(String[] args) throws IOException {
		new ClocService();
	}

	public ClocService() throws IOException {
		LOGGER.info("Initializing Service...");
		this.countryLocator = fromFreshIndex();
		LOGGER.info("Loaded Country Locator...");

		final Gson gson = new Gson();
		get("/locate/:lat/:long", this::locateLatLong, gson::toJson);
		get("/locate/:geohash", this::locateGeoHash, gson::toJson);

		exception(NumberFormatException.class, (e, req, res) -> halt(SC_BAD_REQUEST, e.getMessage()));
		exception(IllegalArgumentException.class, (e, req, res) -> halt(SC_BAD_REQUEST, e.getMessage()));

		LOGGER.info("Created Routes...");
	}

	private List<String> locateGeoHash(Request req, Response res) {
		final String geohash = req.params("geohash");
		return locate(geohash);
	}

	private List<String> locateLatLong(Request req, Response res) {
		final Double latitude = Double.valueOf(req.params("lat"));
		final Double longitude = Double.valueOf(req.params("long"));
		final String geohash = withCharacterPrecision(latitude, longitude, 5).toBase32();
		return locate(geohash);
	}

	private List<String> locate(String geohash) {
		LOGGER.info(format("Searching locations for: %s", geohash));
		final List<String> locations = this.countryLocator.locate(geohash);
		LOGGER.info(format("Found: %s", locations));
		return locations;
	}
}
