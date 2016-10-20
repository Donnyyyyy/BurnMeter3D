package ru.donny.burnmeter3D.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Parser {
	final private int BUFFER_SIZE = 20975152;
	private final int MAX_STRING_LEN = 50;
	private DataInputStream din;
	private byte[] buffer;
	private int bufferPointer, bytesRead;

	public Parser(InputStream in) {
		din = new DataInputStream(in);
		buffer = new byte[BUFFER_SIZE];
		bufferPointer = bytesRead = 0;
	}

	public String getLine() {
		byte[] ch = new byte[50];
		int point = 0;
		try {
			byte c = read();
			while (c == ' ' || c == '\n' || c == '\r')
				c = read();
			while (c != '\n') {
				ch[point++] = c;
				c = read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(ch, 0, point);
	}

	public String nextString() {
		byte[] ch = new byte[MAX_STRING_LEN];
		int point = 0;
		try {
			byte c = read();
			while (c == ' ' || c == '\n' || c == '\r')
				c = read();
			while (c != ' ' && c != '\n' && c != '\r') {
				ch[point++] = c;
				c = read();
			}
		} catch (Exception e) {
		}
		return new String(ch, 0, point);
	}

	public int nextInt() {
		int ret = 0;
		boolean neg;
		try {
			byte c = read();
			while (c <= ' ')
				c = read();
			neg = c == '-';
			if (neg)
				c = read();
			do {
				ret = ret * 10 + c - '0';
				c = read();
			} while (c > ' ');

			if (neg)
				return -ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * @return array of integer that are contained in the current line.
	 */
	public ArrayList<Integer> intArrayList() {
		ArrayList<Integer> out = new ArrayList<Integer>();

		try {
			do {
				out.add(nextInt());
			} while (((buffer[bufferPointer + 1] != '\n')) && (buffer[bufferPointer] != '\n'));
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return out;
	}

	public long nextLong() {
		long ret = 0;
		boolean neg;
		try {
			byte c = read();
			while (c <= ' ')
				c = read();
			neg = c == '-';
			if (neg)
				c = read();
			do {
				ret = ret * 10 + c - '0';
				c = read();
			} while (c > ' ');

			if (neg)
				return -ret;
		} catch (Exception e) {
		}
		return ret;
	}

	private void fillBuffer() {
		try {
			bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
		} catch (Exception e) {
		}
		if (bytesRead == -1)
			buffer[0] = -1;
	}

	private byte read() throws IOException {
		if (bufferPointer == bytesRead)
			fillBuffer();

		if (bufferPointer >= buffer.length)
			throw new IOException("File ended");

		return buffer[bufferPointer++];
	}

}