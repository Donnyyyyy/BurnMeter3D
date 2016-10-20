package ru.donny.burnmeter3D.data.graphstorage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface GraphStorageProvider {

	public OutputStream getWritableStorage(String modelName) throws IOException;

	public InputStream getReadableStorage(String modelName) throws FileNotFoundException;
}
