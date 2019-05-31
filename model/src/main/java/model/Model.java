package model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import contract.*;
import entity.*;
import entity.breakable.Breakable;
import entity.movable.*;
import entity.movable.collectible.*;
import entity.movable.ennemy.*;
import entity.movable.heros.Heros;
import entity.penetrable.*;
import entity.unbreakable.*;
/**
 * The Class Model.
 *
 * @author Pierre GARRIDO
 */
public final class Model extends Observable implements IModel {
	
	private Entity[][] map;
	private int score;
	private int diamonds_remaining;
	private ArrayList<Movable> mouv;
	private ArrayList<Collectible> collec;
	private Heros heros;
	private Exit exit;
	private ArrayList<Ennemy> enemies;
	private ArrayList<Penetrable> penetrables;
	private ArrayList<Unbreakable> unbreakables;
	private ArrayList<Breakable> breakables;
	private boolean pause;

	/**
	 * Instantiates a new model.
	 */
	public Model(int numberMapP) {
		this.score = 0;
		this.mouv = new ArrayList<Movable>();
		this.collec = new ArrayList<Collectible>();
		this.enemies = new ArrayList<Ennemy>();
		this.penetrables = new ArrayList<Penetrable>();
		this.unbreakables = new ArrayList<Unbreakable>();
		this.breakables = new ArrayList<Breakable>();
		this.loadMap(numberMapP);
		this.diamonds_remaining = collec.size();
	}

	/**
     * Load the initial map.
     *
	 * @param mapNumber
	 *            the number of the map
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see contract.IModel#getMap(entity.Entity)
	 */
	private void loadMap(int mapNumber) {
		Entity[][] map;
		try {
			final DAOMap daoMap = new DAOMap(DBConnection.getInstance().getConnection());
			this.map = daoMap.load(mapNumber, mouv, collec, heros, enemies, penetrables, unbreakables, breakables, exit);
		} catch (final SQLException e) {
			e.printStackTrace();e.printStackTrace();
		} catch (final RuntimeException e2) {
			e2.printStackTrace();
		}
	}
	
	/**
     * Reset the model
     */
	private void resetModel() {
		
		this.mouv.removeAll(this.mouv);
		this.collec.removeAll(this.collec);
		this.heros = new Heros();
		this.enemies.removeAll(this.enemies);
		this.penetrables.removeAll(this.penetrables);
		this.unbreakables.removeAll(this.unbreakables);
		this.map = null;
	}

	/**
     * Gets the observable.
     *
     * @return the observable
     */
	/*
	 * (non-Javadoc)
	 *
	 * @see contract.IModel#getObservable()
	 */
	public Observable getObservable() {
		return this;
	}

	/**
     * Gets the map.
     *
     * @return the map
     */
	@Override
	public Entity[][] getMap() {
		return this.map;
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public boolean hasWon() {
		return false;
	}

	/**
     * Manage orders
     */
	@Override
	public void giveOrder(ControllerOrder order) {
		
		if(order == ControllerOrder.Pause)
		{
			if(pause)
				pause = false;
			else
				pause = true;
		}
		else
		{
			if(!pause)
			{
				if(order == ControllerOrder.MoveLeft)
				{
					if(map[heros.getX() - 1][heros.getY()].getCapacity() == Capacities.UNBREAKABLE)
					{
						heros.setDir(Direction.RIGHT);
					}
					else if(map[heros.getX() - 1][heros.getY()].getCapacity() == Capacities.PENETRABLE)
					{
						if(exit.getX() == (heros.getX() - 1) && exit.getY() == heros.getY())
						{
							Penetrable pen = new Background();
							pen.setXY(heros.getX(), heros.getY());
							map[heros.getX() - 1][heros.getY()] = heros;
							map[heros.getX()][heros.getY()] = pen;
						}
						else
						{
							int i = 0;
							for(i = 0;i < penetrables.size();i++)
							{
								if(penetrables.get(i).getX() == (heros.getX() - 1) && penetrables.get(i).getY() == heros.getY())
								{
									penetrables.remove(i);
								}
							}
							Penetrable pen = new Background();
							pen.setXY(heros.getX(), heros.getY());
							map[heros.getX() - 1][heros.getY()] = heros;
							map[heros.getX()][heros.getY()] = pen;
							penetrables.add(pen);
						}
						heros.setDir(Direction.RIGHT);
						heros.setX(heros.getX() - 1);
						this.testFallLeft();
					}
					else if(map[heros.getX() - 1][heros.getY()].getCapacity() == Capacities.COLLECTIBLE)
					{
						heros.setDir(Direction.RIGHT);
						int i = 0;
						for(i = 0;i < penetrables.size();i++)
						{
							if(penetrables.get(i).getX() == (heros.getX() - 1) && penetrables.get(i).getY() == heros.getY())
							{
								penetrables.remove(i);
							}
						}
						Penetrable pen = new Background();
						pen.setXY(heros.getX(), heros.getY());
						map[heros.getX() - 1][heros.getY()] = heros;
						map[heros.getX()][heros.getY()] = pen;
						penetrables.add(pen);
					}
				}
			}
		}
	}
	
	private void testFallLeft() {
		int i = 0;
		
		if(map[heros.getX() - 1][heros.getY() - 1].getCapacity() == Capacities.MOVABLE)
		{
			for(i = 0;i < mouv.size();i++)
			{
				if(mouv.get(i).getX() == (heros.getX() - 1) && mouv.get(i).getY() == (heros.getY() - 1))
				{
					if(mouv.get(i).isSubmittedToGravity())
					{
						Thread t = new Thread(new Fall(mouv.get(i)));
						t.run();
					}
				}
			}
		}
		else if(map[heros.getX() - 2][heros.getY()].getCapacity() == Capacities.MOVABLE && map[heros.getX() - 2][heros.getY()].getCapacity() == Capacities.PENETRABLE)
		{
			for(i = 0;i < mouv.size();i++)
			{
				if(mouv.get(i).getX() == (heros.getX() - 2) && mouv.get(i).getY() == heros.getY())
				{
					if(mouv.get(i).isSubmittedToGravity())
					{
						Thread t = new Thread(new Fall(mouv.get(i)));
						t.run();
					}
				}
			}
		}
		else if(map[heros.getX() - 1][heros.getY() - 1].getCapacity() == Capacities.COLLECTIBLE)
		{
			for(i = 0;i < mouv.size();i++)
			{
				if(mouv.get(i).getX() == (heros.getX() - 1) && mouv.get(i).getY() == (heros.getY() - 1))
				{
					if(mouv.get(i).isSubmittedToGravity())
					{
						Thread t = new Thread(new Fall(mouv.get(i)));
						t.run();
					}
				}
			}
		}
		else if(map[heros.getX() - 2][heros.getY()].getCapacity() == Capacities.COLLECTIBLE && map[heros.getX() - 2][heros.getY()].getCapacity() == Capacities.PENETRABLE)
		{
			for(i = 0;i < mouv.size();i++)
			{
				if(mouv.get(i).getX() == (heros.getX() - 2) && mouv.get(i).getY() == heros.getY())
				{
					if(mouv.get(i).isSubmittedToGravity())
					{
						Thread t = new Thread(new Fall(mouv.get(i)));
						t.run();
					}
				}
			}
		}

		
	}


	/**
     * Gets the score.
     *
     * @return the score
     */
	@Override
	public int getScore() {
		return this.score;
	}

	/**
     * Gets the number of remaining diamonds.
     *
     * @return the number of remaining diamond
     */
	@Override
	public int getDiamonds_remaining() {
		return this.diamonds_remaining;
	}

	@Override
	public void addObserver(Observer o) {
		super.addObserver(o);
		this.setChanged();
		this.notifyObservers();
	}
}
