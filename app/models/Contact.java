package models;

import com.google.code.morphia.annotations.Embedded;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;

@Embedded
public class Contact {

    @Required
    public String contactName;
    @Required
    @Email
    public String email;
    @Required
    public String telephone;
    @Required
    public String website;

}
