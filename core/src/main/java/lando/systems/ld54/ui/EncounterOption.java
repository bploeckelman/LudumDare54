package lando.systems.ld54.ui;

public class EncounterOption {
    public String optionText;
    public OutcomeType outcomeType;
    public float outcomeValue;
    enum OutcomeType {
        DAMAGE,
        HEAL,
        CREDITS,
        ITEM,
        NOTHING
    }
    public String outcomeText;

    public EncounterOption(String optionText, OutcomeType outcomeType, float outcomeValue) {
        this.optionText = optionText;
        this.outcomeType = outcomeType;
        this.outcomeValue = outcomeValue;
    }
}
