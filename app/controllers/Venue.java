package controllers;

import models.Venues;
import models.VenuesSearchResults;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.addnewvenue.addvenueform;
import views.html.addnewvenue.summary;

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

        VenuesSearchResults results = Venues.all();
        return ok(toJson(results.venuesList));
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
            return ok(summary.render(created));
        }
    }

    public static Result details(String id) {
        Venues venue = Venues.getVenue(id);
        if(venue != null)
            return ok(toJson(venue));
        else {
            return notFound(Json.newObject());
        }
    }




}
