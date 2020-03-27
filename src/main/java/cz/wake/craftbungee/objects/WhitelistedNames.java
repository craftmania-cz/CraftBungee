package cz.wake.craftbungee.objects;

public class WhitelistedNames {

    // Nick
    private String name;

    // Description
    private String description;

    public WhitelistedNames(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
