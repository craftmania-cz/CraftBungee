package cz.wake.craftbungee.objects;

import java.util.regex.Pattern;

public class WhitelistedIP {

    // IP (String)
    private Pattern address;

    // Description
    private String description;

    public WhitelistedIP(Pattern address, String description){
        this.address = address;
        this.description = description;
    }

    public Pattern getAddress(){
        return address;
    }

    public String getDescription(){
        return description;
    }
}
