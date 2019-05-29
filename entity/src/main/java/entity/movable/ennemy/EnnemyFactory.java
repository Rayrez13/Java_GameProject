package entity.movable.ennemy;

public abstract class EnnemyFactory {
	
	private static final Ennemy octopus = new Octopus();
	
	private static Ennemy[] entities = {octopus};
	
	public Ennemy createOctopus() {
		return octopus;
	}
	
	public static Ennemy getFromFileSymbol(char symbol) {
		for (Ennemy entity : entities) {
            if (entity.getBaseSprite().getIcon() == symbol) {
                return entity;
            }
        }
		return null;
	}

}
