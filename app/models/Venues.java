package models;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import controllers.MorphiaObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import play.Logger;
import play.data.validation.Constraints.Required;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Entity("venues")
public class Venues {

    @Id
    public String id;
    @Required
    public String name;
    @Required
    public String about;

    @Valid
    @Embedded
    public Address address;

    @Valid
    @Required
    @Embedded
    public Contact contact;

    public List<String> photos;

    //@Required
    //@Embedded
    //private List<Rooms> rooms;
    //@Required
    //private List<HireType> hireType;
    //public String parkingInfo;

    public static VenuesSearchResults all() {
        if (MorphiaObject.datastore != null) {
            List<Venues> venues = MorphiaObject.datastore.find(Venues.class).asList();
            VenuesSearchResults venuesSearchResults = new VenuesSearchResults();
            venuesSearchResults.totalNoOfSearchResults = venues.size();
            venuesSearchResults.venuesList = venues;
            return venuesSearchResults;
        } else {
            return new VenuesSearchResults();
        }
    }

    public static VenuesSearchResults venuesByPage(int page, int limit) {
        if (MorphiaObject.datastore != null) {
            int offset = page == 1 ? 0 : (page - 1) * limit ;
            VenuesSearchResults venuesSearchResults = new VenuesSearchResults();
            venuesSearchResults.totalNoOfSearchResults = MorphiaObject.datastore.find(Venues.class).asList().size();
            venuesSearchResults.venuesList = MorphiaObject.datastore.createQuery(Venues.class).offset(offset).limit(limit).asList();
            venuesSearchResults.page = page;
            return venuesSearchResults;
        } else {
            return new VenuesSearchResults();
        }

    }

    public static void create(Venues venue) {
        String _address = venue.address.line1 + " " + venue.address.line2 + " " + venue.address.postCode;
        Float[] latlog = getLongLatGoogle(_address);
        venue.address.latitude = latlog[0];
        venue.address.longitude = latlog[1];
        venue.photos = new ArrayList<String>();
        venue.photos.add("sound.jpg");
        venue.photos.add("bound.jpg");
        venue.photos.add("feeling.jpg");
        Key<Venues> save = MorphiaObject.datastore.save(venue);
    }

    public static String delete(String idToDelete) {
        try {
            Logger.info("toDelete: " + idToDelete);
            MorphiaObject.datastore.findAndDelete(MorphiaObject.datastore.createQuery(Venues.class).field("_id").equal(new ObjectId(idToDelete)));
            return "success";
        } catch (Exception e) {
            Logger.debug("Venue with id not deleted: " + idToDelete);
            return idToDelete.toString() + "not deleted please try again";
        }
    }

    public static Venues getVenue(String id) {
        try {
            Venues venue = MorphiaObject.datastore.find(Venues.class).field("_id").equal(new ObjectId(id)).get();
            Logger.info("returning: " + venue);
            return venue;
        } catch (Exception e) {
            Logger.debug("ID Not Found: " + id);
        }
        return null;
    }



    public static Float[] getLongLat(String address)  {
        float latValue = 0.0F;
        float longValue = 0.0F;
        try {
            HttpClient client = new DefaultHttpClient();
            URIBuilder builder = new URIBuilder();
            builder.setScheme("http").setHost("nominatim.openstreetmap.org").setPath("/search")
                    .setParameter("q", address)
                    .setParameter("countrycodes", "GB")
                    .setParameter("format", "json");
            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            HttpResponse response = client.execute(request);

            int code = response.getStatusLine().getStatusCode();
            Logger.debug("Status code was "+ Integer.toString(code));

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            String json = rd.readLine();
            Logger.debug(json);
            Object obj = JSONValue.parse(json);
            JSONArray _array = (JSONArray)obj;
            Logger.debug("size is " + Integer.toString( _array.size()));
            for (Object a_array : _array) {
                JSONObject result = (JSONObject) a_array;
                String latitude = (String) result.get("lat");
                latValue = Float.parseFloat(latitude);
                String longitude = (String) result.get("lon");
                longValue = Float.parseFloat(longitude);
                Logger.debug(String.format("lat:%s long:%s", latitude, longitude));
                break;
            }
        }  catch (Exception ex) {
            Logger.debug(ex.getMessage());
        }
        return new Float[] { latValue, longValue }  ;
    }

    public static Float[] getLongLatGoogle(String address)  {
        float latValue;
        float longValue;
        Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(address).getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
        List<GeocoderResult> results = geocoderResponse.getResults();
        latValue = results.get(0).getGeometry().getLocation().getLat().floatValue();
        longValue = results.get(0).getGeometry().getLocation().getLng().floatValue();
        return new Float[] { latValue, longValue }  ;
    }


    public static Venues updateVenue(String id, Venues venue) {
        try {
            Query<Venues> query = MorphiaObject.datastore.createQuery(Venues.class).field("_id").equal(new ObjectId(id));
            UpdateOperations<Venues> ops = MorphiaObject.datastore.createUpdateOperations(Venues.class)
                    .set("name", venue.name)
                    .set("about", venue.about)
                    .set("address", venue.address)
                    .set("contact", venue.contact);

            UpdateResults<Venues> update = MorphiaObject.datastore.update(query, ops);

            //Logger.info("returning: " + venue);
            Venues updatedVenue = MorphiaObject.datastore.find(Venues.class).field("_id").equal(new ObjectId(id)).get();
            return updatedVenue;
        } catch (Exception e) {
            Logger.debug("ID Not Found: " + id);
        }
        return null;
    }
}

