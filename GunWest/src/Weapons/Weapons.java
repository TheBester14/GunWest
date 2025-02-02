package Weapons;

public abstract class Weapons {
	
	//name /  id / damage / firespeed / reloadspeed
	protected int id;
	protected String name;
	protected int damage;
	protected long firespeed;
	protected long reloadspeed;
	
	public Weapons(int id,String name, int damage, long firespeed, long reload) {
		this.id = id;
		this.name = name;
		this.damage = damage;
		this.firespeed = damage;
		this.reloadspeed = reload;
	}
	void update() {
	}
	void draw() {
		
	}
}
