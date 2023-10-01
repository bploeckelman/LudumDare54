package lando.systems.ld54.ui;

public class EncounterOptionOutcome {
    public OutcomeType type;
    public float weight;
    public float value;
    public String text;
    enum OutcomeType {
        DAMAGE,
        HEAL,
        CREDITS,
        ITEM,
        NOTHING
    }
    public EncounterOptionOutcome() {
        this.type = OutcomeType.NOTHING;
        this.value = 0;
        this.text = "";
        this.weight = .5f;
    }
}
