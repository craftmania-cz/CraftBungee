package cz.wake.craftbungee.managers.events;

public class Event {

    private EventState eventState;
    private String name;
    private String category;
    private int reward;

    public Event() {}

    public Event(EventState eventState, String name, String category, int reward) {
        this.eventState = eventState;
        this.name = name;
        this.category = category;
        this.reward = reward;
    }

    public boolean isSelected() {
        return this.name != null;
    }

    public EventState getEventState() {
        return eventState;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getReward() {
        return reward;
    }

    public synchronized void setEventState(EventState eventState) {
        this.eventState = eventState;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventState=" + eventState +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", reward=" + reward +
                '}';
    }

    @Override
    protected Event clone() {
        return new Event(eventState, name, category, reward);
    }

    public static enum EventState {
        LOBBY,
        STARTING,
        INGAME,
        ENDING;
    }
}
