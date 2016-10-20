package ru.donny.burnmeter3D.resources;

public abstract class TextResources {

    /**
     * @param resourceKey object from enum inside this class corresponding to the phrase.
     * @return text string.
     */
    public abstract String getString(StringResource resourceKey);

    public enum StringResource{
        child, adult, name, modify, notification, date, exit, medicalHistory,	burnsPercentage, therapyVolume, ml, back, height, rotate, select, save, calibrate, burnI, burnII, burnIII, fullName, remove, cantParseName, medicalHistoryIsNotSet, newPatient, openStorage;
    }
}
