package a3;

public class MyServer {

	public MyServer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Setting up the server");
		//new NetworkingServer(80, "UDP");
		NetworkingServer app =
				new NetworkingServer(80, "UDP");

	}

}
