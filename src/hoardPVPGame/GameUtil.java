package hoardPVPGame;

public class GameUtil {
	private static float ROOM_SIZE = 5f;
	private static int SKIN_NUM=4;
	
	public static float getRoomSize() {
		return ROOM_SIZE;
	}

	public static int getSkinNumber() {
		return SKIN_NUM;
	}
	
	public enum SKIN{
		GREEN_DRAGON, RED_DRAGON, BLACK_DRAGON, PURPLE_DRAGON,
		KNIGHT, BLACK_KNIGHT, WHITE_KNIGHT, GOLD_KNIGHT
	}
	
	public enum TRAP_TYPE{
		Spike, Pit, Swinging;
	}
	
}
