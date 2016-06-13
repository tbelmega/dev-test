package de.tbelmega.cityprocessor;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by tbelm on 13.06.2016.
 */
public class CityProcessorTest {

    private static final CityProcessor CITY_PROCESSOR = new CityProcessor("Berlin");

    private String sampleString;
    private JSONArray cities = new JSONArray();
    private JSONObject cityPotsdam = new JSONObject();;
    private JSONObject cityBerlin = new JSONObject();;

    public static final String CSV_STRING_POTSDAM = "123456, Potsdam, location, 52.39886, 13.06566";
    public static final String CSV_STRING_BERLIN = "123457, Berlin, location, 52.39886, 13.06566";

    @BeforeClass
    public void setUpSampleData() {
        JSONObject geoPosition = new JSONObject();
        geoPosition.put("latitude", 52.39886);
        geoPosition.put("longitude", 13.06566);

        cityPotsdam.put("_id", 123456);
        cityPotsdam.put("name", "Potsdam");
        cityPotsdam.put("type", "location");
        cityPotsdam.put("geo_position", geoPosition);

        cityBerlin.put("_id", 123457);
        cityBerlin.put("name", "Berlin");
        cityBerlin.put("type", "location");
        cityBerlin.put("geo_position", geoPosition);

        cities.put(cityPotsdam);
        cities.put(cityBerlin);

        sampleString = cities.toString();
    }

    @Test
    public void testThat_cityProcessorConvertsJSONObjectToCSVString() throws Exception {
        //arrange


        //act
        String result = CITY_PROCESSOR.processCity(cityBerlin);

        //assert
        assertEquals(CSV_STRING_BERLIN, result);
    }

    @Test
    public void testThat_cityProcessorConvertsJSONtoListOfString() throws Exception {
        //arrange

        //act
        List<String> result = CITY_PROCESSOR.process(sampleString);

        //assert
        assertEquals(cities.length(), result.size());
        assertTrue(result.contains(CSV_STRING_POTSDAM));
        assertTrue(result.contains(CSV_STRING_BERLIN));
    }


    @Test
    public void testThat_cityProcessorCreatesFile() throws Exception {
        //arrange

        //act
        File createdFile = CITY_PROCESSOR.getResultFromServer();
        createdFile.deleteOnExit();

        //assert
        assertTrue("File was not created: " + createdFile.getAbsolutePath(), createdFile.exists());
    }

    @Test
    public void testThat_cityProcessorWritesOutputToFile() throws Exception {
        //arrange
        List<String> lines = CITY_PROCESSOR.process(sampleString);

        //act
        File createdFile = CITY_PROCESSOR.writeToFile(lines);
        createdFile.deleteOnExit();

        //assert
        String result = FileUtils.readFileToString(createdFile, CharEncoding.UTF_8);
        assertTrue(result.contains(lines.get(0)));
        assertTrue(result.contains(lines.get(1)));
    }

//    /**
//     * Runs the main method. Results in a file being created in the file system.
//     * @throws Exception
//     */
//    @Test
//    public void testMainMethod() throws Exception {
//        //arrange
//
//        //act
//        CityProcessor.main(new String[]{"Berlin"});
//
//        //assert
//    }
}
