package ru.donny.burnmeter3d.android.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import ru.donny.burnmeter3D.data.graphstorage.GraphStorageProvider;

public class AndroidGraphStorageProvider implements GraphStorageProvider {

	Context appContext;

	public AndroidGraphStorageProvider(Context context) {
		appContext = context;
	}

	@Override
	public OutputStream getWritableStorage(String modelName) throws IOException {
		return appContext.openFileOutput(modelName, Context.MODE_PRIVATE);
	}

	@Override
	public InputStream getReadableStorage(String modelName) throws FileNotFoundException {
		return appContext.openFileInput(modelName);
	}
}
