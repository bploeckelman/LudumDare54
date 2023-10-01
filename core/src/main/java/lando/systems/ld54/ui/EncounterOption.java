package lando.systems.ld54.ui;

public class EncounterOption {
    public String optionText;
    public OutcomeType outcomeType;
    public float outcomeValue;
    public String outcomeText;
    enum OutcomeType {
        DAMAGE,
        HEAL,
        CREDITS,
        ITEM,
        NOTHING
    }
    public EncounterOption() {
        this.optionText = "";
        this.outcomeType = OutcomeType.NOTHING;
        this.outcomeValue = 0;
        this.outcomeText = "";
    }

    public EncounterOption(String optionText, OutcomeType outcomeType, float outcomeValue, String outcomeText) {
        this.optionText = optionText;
        this.outcomeType = outcomeType;
        this.outcomeValue = outcomeValue;
        this.outcomeText = outcomeText;
    }
}
