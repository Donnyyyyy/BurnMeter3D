package ru.donny.burnmeter3D.engine.objects.model;

import java.util.ArrayList;

public class BalanceTable extends ArrayList<HumanInfo> {

	private static final long serialVersionUID = -2723308567488407045L;

	public BalanceTable() {
	}

	/**
	 * @return <code>null</code> if square for this part isn't set or square in
	 *         range 0-100% in another cases.
	 */
	public Float getSquareCoefficient(BodyPart part) {
		return 0.5f;
	}

}
