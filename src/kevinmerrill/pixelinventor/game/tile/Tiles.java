package kevinmerrill.pixelinventor.game.tile;

public enum Tiles {
	Air(0), Dirt(1), Grass(2), Stone(3), Sand(4), Snow(5), SnowyGrass(6), DeadGrass(7), Sandstone(8), Water(9), Wood(10), Leaves(11), Lilypad(12), PurpleLilypad(13),
	Broadleaf(14), Spruce(15), Broadleaf_2(16), Tallgrass(17), Tallgrass_Green(18), Cactus(19), Acacia(20), Rubber(21), Fiberglass(22);
	
	
	
	private int id;
	
	Tiles(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public Tile getTileState() {
		return Tile.getTileStateFromId(id);
	}
}
