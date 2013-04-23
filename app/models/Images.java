package models;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import controllers.MorphiaObject;
import java.util.List;

@Entity("images")
public class Images {

    @Id
    public String id;
    public String primary;
    public List<String> photos;

    public static void create(Images image) {
        Key<Images> save = MorphiaObject.datastore.save(image);
    }


}
