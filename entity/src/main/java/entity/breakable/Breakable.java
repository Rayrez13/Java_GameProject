package entity.breakable;

import entity.Capacities;
import entity.Entity;

/**
 * The abstract class Breakable.
 *
 * @author Pierre GARRIDO
 */
public abstract class Breakable extends Entity {

	/**
	 * Set the capacity Breakable.
	 *
	 * @param symbol
	 * 			The symbol.
	 * @param fileName
	 * 			Name of a sprite file.
	 */
	public Breakable(char symbol, String fileName) {
		super(symbol, fileName);
		this.capacity = Capacities.BREAKABLE;
	}

	@Override
	public Capacities getCapacity() {
		return this.capacity;
	}
}
