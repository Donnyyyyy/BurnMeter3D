package ru.donny.burnmeter3D.data;

import java.util.ArrayList;
import java.util.HashSet;

import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;

public class Burns extends ArrayList<Burn> {

	private static final long serialVersionUID = -8134350420588754470L;

	public void add(BurnInstance area) {
		HashSet<Integer> contained = new HashSet<Integer>();
		for (Burn i : this)
			contained.addAll(i.getContaied(area));

		if (contained.size() == area.getBurnTriangles().size())
			return;

		Burn addTo = null;
		for (Burn i : this)
			if (i.getType() == area.getType())
				addTo = i;

		if (addTo == null) {
			addTo = new Burn(area.getType());
			add(addTo);
		}

		for (Triangle i : area.getBurnTriangles())
			if (!contained.contains(i.getId()))
				addTo.add(i.getId());

		return;
	}

	public void add(int triangleId, int typeId) {
		if (contains(triangleId))
			return;

		for (Burn i : this)
			if (i.getType().getId() == typeId)
				i.add(triangleId);
	}

	public boolean contains(int triangleId) {
		for (Burn i : this)
			if (i.contains(triangleId))
				return true;

		return false;
	}

	public float getPercentage(Burn.BurnType type) {
		for (Burn i : this)
			if (type == i.getType())
				return i.getPercentage();

		return 0;
	}
}
