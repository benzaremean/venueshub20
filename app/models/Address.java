package models;

import com.google.code.morphia.annotations.Embedded;
import play.data.validation.Constraints.Required;

@Embedded
public class Address {

    @Required
    public String line1;

    public String line2;

    @Required
    public String town;

    @Required
    public String postCode;

    public float latitude;

    public float longitude;

}
