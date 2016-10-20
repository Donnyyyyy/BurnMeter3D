package ru.donny.burnmeter3D.graphics.gui;

public class KeywordListener {

	private static final int ANY_KEY_POINTER = -1;;

	private int timeoutMillis;
	private String keyword;
	protected int requiredSymbolPointer;
	protected long lastTyping;
	private Callback callback;

	public KeywordListener(int timeoutMillis, String keyword, Callback callback) {
		super();

		if (keyword == null)
			throw new IllegalArgumentException();

		this.timeoutMillis = timeoutMillis;
		this.keyword = keyword;
		this.callback = callback;

		// if @typingStart == -1 then keyword typing not yet started
		lastTyping = -1;
		setupRequiredCharPointer();
	}

	protected void setupRequiredCharPointer() {
		// if @keyword length == 0 then any input will cause the callback
		requiredSymbolPointer = (keyword.length() > 0) ? 0 : ANY_KEY_POINTER;
	}

	public void keyTyped(char key) {
		if (requiredSymbolPointer == ANY_KEY_POINTER) {
			callback.actionPerformed();
			return;
		}

		if ((System.currentTimeMillis() > lastTyping + timeoutMillis) && (lastTyping != -1)) {
			requiredSymbolPointer = 0;
			lastTyping = -1;
			return;
		}

		if (key == keyword.charAt(requiredSymbolPointer)) {
			requiredSymbolPointer++;
			lastTyping = System.currentTimeMillis();
		} else {
			requiredSymbolPointer = 0;
			lastTyping = -1;
		}

		if (requiredSymbolPointer >= keyword.length())
			callback.actionPerformed();
	}

	public int getTimeoutMillis() {
		return timeoutMillis;
	}

	public void setTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public interface Callback {

		public void actionPerformed();
	}
}
