package ru.donny.burnmeter3D.data.balancetable;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import ru.donny.burnmeter3D.engine.objects.model.BalanceTable;

public class BalanceTableProvider
		extends SynchronousAssetLoader<BalanceTable, BalanceTableProvider.BalanceTableParameters> {

	public BalanceTableProvider(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public BalanceTable load(AssetManager assetManager, String fileName, FileHandle file,
			BalanceTableParameters parameter) {
		Json json = new Json();
		return json.fromJson(BalanceTable.class, file);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BalanceTableParameters parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class BalanceTableParameters extends AssetLoaderParameters<BalanceTable> {
	}
}
