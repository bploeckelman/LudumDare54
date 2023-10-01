package lando.systems.ld54.encounters;

public class EncounterOptionOutcome {
    public OutcomeType type;
    public float weight;
    public int value;
    public String text;
    public enum OutcomeType {
        FUEL,
        SCRAP,
        RADAR,
        BOMB,
        NOTHING
    }
    public EncounterOptionOutcome() {
        this.type = OutcomeType.NOTHING;
        this.value = 0;
        this.text = "";
        this.weight = .5f;
    }
}
