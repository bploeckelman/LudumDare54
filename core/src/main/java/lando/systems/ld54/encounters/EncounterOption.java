package lando.systems.ld54.encounters;

public class EncounterOption {
    public String text;
    public EncounterOptionOutcome[] possibleOutcomes;
    public EncounterOption() {
        this.text = "";
        this.possibleOutcomes = null;
    }
}
