package kevinmerrill.pixelinventor.resources;

public class PMath {
	public static boolean areaContains(float x, float y, float width, float height, float px, float py) {
		return px >= x && py >= y && px <= x + width && py <= y + height;
	}
	public static float lerp(float a, float b, float f)
	{
	    return a + f * (b - a);
	}
}
