package models;

import com.google.code.morphia.annotations.Embedded;
import play.data.validation.Constraints.Required;

@Embedded
public class Rooms {

    @Required
    public String name;
    public String description;
    @Required
    public String capacity;

}
