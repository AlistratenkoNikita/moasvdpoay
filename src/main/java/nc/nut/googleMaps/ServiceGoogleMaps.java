package nc.nut.googleMaps;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Anton Bulgakov
 * @since 21.04.2017
 */
public class ServiceGoogleMaps {
    private static String apiKey = "AIzaSyBF2CMlYRcV-9zTHFND2m2InqbYdeyJz30";

    /**
     * Method gets region according to address from params and returns it.
     * If no region according to address from params, it will return NULL.
     *
     * @param address input address.
     * @return region of address from param.
     */
    public String getRegion(String address) {
        Logger logger = LoggerFactory.getLogger(getClass());
        Boolean regionExists = false;
        String region = null;
        GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
        GeocodingResult[] results;
        try {
            results = GeocodingApi.geocode(context, address).await();
            if (results != null && results.length != 0) {
                // if it`s region
                AddressComponent[] components = results[0].addressComponents;
                for (int i = 0; i < components.length; i++) {
                    AddressComponentType[] type = components[i].types;
                    if (type[0].toString().equals("administrative_area_level_1")) {
                        region = components[i].longName;
                        regionExists = true;
                        break;
                    }
                }
                // if it`s city-region
                if (!regionExists) {
                    for (int i = 0; i < components.length; i++) {
                        AddressComponentType[] type = components[i].types;
                        if (type[0].toString().equals("administrative_area_level_2")) {
                            region = components[i].longName;
                            break;
                        }
                    }
                }
            }
        } catch (ApiException | InterruptedException | IOException e) {
            logger.error("Can`t find this address.", e);
        }
        return region;
    }

    /**
     * Method gets formatted address according to address from params and returns it.
     * If no address according to address from params, it will return NULL.
     *
     * @param address input address.
     * @return formatted address.
     */
    public String getFormattedAddress(String address) {
        Logger logger = LoggerFactory.getLogger(getClass());
        String formattedAddress = null;
        GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
        GeocodingResult[] results;
        try {
            results = GeocodingApi.geocode(context, address).await();
            if (results != null && results.length != 0) {
                formattedAddress = results[0].formattedAddress;
            }
        } catch (ApiException | InterruptedException | IOException e) {
            logger.error("Can`t find this address.", e);
        }
        return formattedAddress;
    }
}