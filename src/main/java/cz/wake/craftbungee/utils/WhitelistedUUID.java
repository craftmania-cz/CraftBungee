package cz.wake.craftbungee.utils;

import java.util.regex.Pattern;

public class WhitelistedUUID {

    // UUID
    private Pattern uuid;

    // Description
    private String description;

    public WhitelistedUUID(Pattern uuid, String description) {
        this.uuid = uuid;
        this.description = description;
    }

    public Pattern getUUID() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }
}
