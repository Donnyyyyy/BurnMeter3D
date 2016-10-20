package ru.donny.burnmeter3D.engine.objects.model;

public interface ModelChooser {

    String choose(Gender gender, boolean isChild);

    String getDefault();

    public enum Gender{
        Male, Female;
    }
}
