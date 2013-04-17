package models;

/**
 * Created with IntelliJ IDEA.
 * User: IfieB
 * Date: 11/02/2013
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
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
