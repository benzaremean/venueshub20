import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.MongoURI;
import controllers.MorphiaObject;
import models.Address;
import models.Contact;
import models.Images;
import models.Venues;
import play.GlobalSettings;
import play.Logger;

public class Global extends GlobalSettings {
    @Override
    public void onStart(play.Application arg0) {
        super.beforeStart(arg0);
        Logger.debug("*** onStart ***");
        MongoURI mongoURI = null;
        String mongoHQ = System.getenv("MONGOHQ_URL") != null ? System.getenv("MONGOHQ_URL") : "mongodb://heroku:d9e634c2734105515fdc60aed77a98ad@alex.mongohq.com:10008/app15209235";
        try {
            mongoURI = new MongoURI(mongoHQ);
            DB db = mongoURI.connectDB();
            db.authenticate(mongoURI.getUsername(), mongoURI.getPassword());

            MorphiaObject.mongo = db.getMongo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MorphiaObject.morphia = new Morphia();
        MorphiaObject.morphia.map(Venues.class)
                .map(Contact.class)
                //.map(Rooms.class)
                .map(Address.class)
                .map(Images.class);
        MorphiaObject.datastore = MorphiaObject.morphia.createDatastore(MorphiaObject.mongo, "app15209235", mongoURI.getUsername(), mongoURI.getPassword());
        MorphiaObject.datastore.ensureIndexes();
        MorphiaObject.datastore.ensureCaps();

        Logger.debug(String.format("** Morphia datastore: ", MorphiaObject.datastore.getDB()));
    }
}
