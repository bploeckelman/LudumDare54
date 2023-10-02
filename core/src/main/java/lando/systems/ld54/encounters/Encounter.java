package lando.systems.ld54.encounters;

import lando.systems.ld54.objects.Sector;

public class Encounter {
    public transient Sector sector;

    public String title;
    public String text;
    public String type;
    public String imageKey;
    public EncounterOption[] options;
    public Encounter() {}
}
