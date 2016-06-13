package de.tbelmega.cityprocessor;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * @author tbelmega
 * This class solves the challenge published at https://github.com/goeuro/dev-test .
 */
public class CityProcessor {

    private static final Logger LOG = Logger.getLogger(CityProcessor.class);

    public static final String DELIMITER = ", ";

    public static final String JSON_KEY_ID = "_id";
    public static final String JSON_KEY_NAME = "name";
    public static final String JSON_KEY_TYPE = "type";
    public static final String JSON_KEY_LATITUDE = "latitude";
    public static final String JSON_KEY_LONGITUDE = "longitude";
    public static final String JSON_KEY_GEO_POSITION = "geo_position";

    private String cityName;

    public CityProcessor(String cityName) {
        this.cityName = cityName;
    }

    /**
     * Use HttpConnector to get data from the server.
     * Write data in .csv format to a file in the file system
     * @return output File object
     * @throws IOException
     */
    public File getResultFromServer() throws IOException {
        HttpConnector connector = new HttpConnector();
        String data = connector.getResponseEntity(cityName);
        List<String> csvLines = process(data);

        return writeToFile(csvLines);
    }

    /**
     * Create a file in the working directory and write the lines to the file.
     * @param csvLines
     * @return
     * @throws IOException
     */
    File writeToFile(List<String> csvLines) throws IOException {
        File outputFile = new File(this.cityName + "_" + System.currentTimeMillis() + ".csv");
        outputFile.createNewFile();
        FileUtils.writeLines(outputFile, CharEncoding.UTF_8, csvLines);
        return outputFile;
    }

    /**
     * Create a list of comma-separated lines
     * @param citiesJsonArrayAsString a JSONArray containing JSONObjects that represent city entries
     * @return
     */
    List<String> process(String citiesJsonArrayAsString) {
        JSONArray arr = new JSONArray(citiesJsonArrayAsString);
        List<String> result = new LinkedList<>();

        for (int i = 0; i < arr.length(); i++) {
            result.add(processCity(arr.getJSONObject(i)));
        }

        return result;
    }

    /**
     * Create a comma-separated line of the required values of the JSONObject.
     * @param city a JSONObject that represents a city as specified
     * @return
     */
    String processCity(JSONObject city) {
        JSONObject geop = city.getJSONObject(JSON_KEY_GEO_POSITION);

        StringBuilder csvLineBuilder = new StringBuilder()
                .append(city.getInt(JSON_KEY_ID))           .append(DELIMITER)
                .append(city.getString(JSON_KEY_NAME))      .append(DELIMITER)
                .append(city.getString(JSON_KEY_TYPE))      .append(DELIMITER)
                .append(geop.getDouble(JSON_KEY_LATITUDE))  .append(DELIMITER)
                .append(geop.getDouble(JSON_KEY_LONGITUDE));
        return csvLineBuilder.toString();
    }


    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("Please provide exactly one argument: CITY_NAME");

        CityProcessor processor = new CityProcessor(args[0]);

        try {
            File output = processor.getResultFromServer();
            LOG.info("Created result file: " + output.getAbsolutePath());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
