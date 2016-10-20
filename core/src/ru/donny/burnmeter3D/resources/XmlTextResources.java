package ru.donny.burnmeter3D.resources;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;

public class XmlTextResources extends TextResources {

	protected boolean isInitialized;
	protected String xmlFileName;
	private HashMap<TextResources.StringResource, String> mStrings;

	private static final String ERROR_MSG = "error";
	private static final String RESOURCE_TAG = "string";
	private static final String NAME_ATRIBUTE = "name";

	public XmlTextResources(String xmlFileName) {
		this.xmlFileName = xmlFileName;
		safeInit();
	}

	protected void safeInit() throws IllegalStateException {
		try {
			init(xmlFileName);
			isInitialized = true;
		} catch (IllegalStateException e) {
			isInitialized = false;
		}
	}

	private void init(String xmlFileName) throws IllegalStateException {
		try {
			XmlReader packParser = new XmlReader();
			XmlReader.Element textPack = packParser.parse(Gdx.files.internal(xmlFileName));

			mStrings = new HashMap<TextResources.StringResource, String>();

			for (XmlReader.Element child : textPack.getChildrenByName(RESOURCE_TAG)) {
				TextResources.StringResource nameKey = TextResources.StringResource
						.valueOf(child.getAttribute(NAME_ATRIBUTE));
				mStrings.put(nameKey, child.getText());
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	@Override
	public String getString(StringResource resourceKey) {
		if (!isInitialized) {
			safeInit();

			if (!isInitialized)
				return ERROR_MSG;
		}

		String resource = mStrings.get(resourceKey);

		return resource == null ? resourceKey.toString() : resource;
	}
}
