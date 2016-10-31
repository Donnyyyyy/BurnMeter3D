package ru.donny.burnmeter3D.engine.objects.burns;

import java.util.ArrayList;

import ru.donny.burnmeter3D.data.Burns;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.graphics.gui.widgets.DataInterface;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;

public interface BurnsStorage {

	public boolean removeLast();

	public void clear();

	public boolean add(BurnInstance burn);

	public Burns toBurns(ModelAbstraction modelAbstraction);

	public ArrayList<Triangle> getUniqueTriangles();

	public void setResultDisplayer(DataInterface dataInterface);

	public ArrayList<BurnInstance> asArray();
}
