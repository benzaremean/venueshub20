package controllers;

import models.*;
import org.codehaus.jackson.JsonNode;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.addnewvenue.addvenueform;
import views.html.addnewvenue.summary;
import play.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

public class Venue extends Controller {

    /**
     * Defines a form wrapping the Venues class.
     */
    static Form<Venues> venuesForm = Form.form(Venues.class);

    /**
     * Display a blank form.
     */
    public static Result blank() {
        return ok(addvenueform.render(venuesForm));
    }

    public static Result listVenues() {
        Logger.debug(request().getQueryString("a"));
        Logger.debug(request().getQueryString("b"));
        Logger.debug(request().getQueryString("c"));
        VenuesSearchResults results = Venues.all();
        if (request().accepts("text/html")) {
            return TODO;
        } else if (request().accepts("application/json")) {
            return ok(Json.toJson(results.venuesList));
        } else {
            return badRequest();
        }
    }

    /**
     * Handle the form submission.
     */
    public static Result submit() {
        Form<Venues> filledForm = venuesForm.bindFromRequest();

        // Check accept conditions
        if(!"true".equals(filledForm.field("accept").value())) {
            filledForm.reject("accept", "You must accept the terms and conditions");
        }
        if(filledForm.hasErrors()) {
            flash("error", "Please correct the form below");
            return badRequest(addvenueform.render(filledForm));
        } else {
            Venues.create(filledForm.get());
            Venues created = filledForm.get();
            //List<Rooms> rooms = new ArrayList<Rooms>();
            for(Rooms room : created.rooms) {
                Logger.debug(room.description);
            }

            List<String> upload = upload(created.id);
            Images image = new Images();
            image.id = created.id;
            image.photos = new ArrayList();
            for(String copy : upload) {
                image.photos.add(copy);
            }
            image.primary = upload.get(1);
            Images.create(image);

            return ok(summary.render(created));
        }
    }

    public static Result details(String id) {
        Venues venue = Venues.getVenue(id);
        if(venue != null) {
            if (request().accepts("text/html")) {
                return TODO;
            } else if (request().accepts("application/json")) {
                return ok(toJson(venue));
            } else {
                return badRequest();
            }
        }  else {
            return notFound(Json.newObject());
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(String id) {
        JsonNode json = request().body().asJson();
        if(json == null) {
            return badRequest("Expecting Json data");
        } else {
            Venues venue = fromJson(json, Venues.class);
            Venues updatedVenue = Venues.updateVenue(id, venue);
            return ok(toJson(updatedVenue));
        }

    }

    public static Result delete(String id) {
        String deleted = Venues.delete(id);
        Logger.debug(deleted);
        if(deleted != "success")  {
            return notFound(deleted);
        }
        return ok(deleted);
    }

//    public static String upload(String venueId) {
//        Http.MultipartFormData body = request().body().asMultipartFormData();
//        Http.MultipartFormData.FilePart picture = body.getFile("picture");
//        if (picture != null) {
//            S3File s3File = new S3File();
//            String pictureFileName = picture.getFilename();
//            s3File.name = pictureFileName;
//            s3File.file = picture.getFile();
//            s3File.id = venueId;
//            s3File.save();
//            Logger.debug("yes we got it");
//            return String.format("%s/%s", venueId, pictureFileName);
//        } else {
//            Logger.debug("awwwwwwww");
//            return null;
//        }
//    }

    public static List<String> upload(String venueId) {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        List<Http.MultipartFormData.FilePart> pictures = body.getFiles();
        if (pictures.size() > 0) {
            List<String> uploadedImages = new ArrayList<String>();
            for(Http.MultipartFormData.FilePart picture : pictures) {
                S3File s3File = new S3File();
                String pictureFileName = picture.getFilename();
                s3File.name = pictureFileName;
                s3File.file = picture.getFile();
                s3File.id = venueId;
                s3File.save();
                uploadedImages.add(String.format("%s/%s", venueId, pictureFileName));
                Logger.debug(String.format("Uploaded %s pictures", Integer.toString(pictures.size())));
            }
            return uploadedImages;

        } else {
            return null;
        }
    }






}
