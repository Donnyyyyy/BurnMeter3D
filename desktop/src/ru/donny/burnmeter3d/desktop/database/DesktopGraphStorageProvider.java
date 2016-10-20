package ru.donny.burnmeter3d.desktop.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.donny.burnmeter3D.data.graphstorage.GraphStorageProvider;

public class DesktopGraphStorageProvider implements GraphStorageProvider {

	private String directory;

	public DesktopGraphStorageProvider(String path) {
		directory = path;
	}

	@Override
	public OutputStream getWritableStorage(String modelName) throws IOException {
		File storage = createFile(modelName);
		return new FileOutputStream(storage);
	}

	@Override
	public InputStream getReadableStorage(String modelName) throws FileNotFoundException {
		File storage = new File(directory + "/" + modelName);
		return new FileInputStream(storage);
	}

	private File createFile(String modelName) throws IOException {
		File storage = new File(directory);
		storage.mkdir();
		storage = new File(directory + "/" + modelName);
		try {
			storage.createNewFile();
		} catch (IOException e) {
			System.out.println("Can't create storage for " + modelName + " (" + e.getMessage() + ")");
			throw e;
		}

		return storage;
	}

}
