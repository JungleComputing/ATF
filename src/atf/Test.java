package atf;

import java.io.File;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		Manager mng = new Manager(new Task[2]);
		mng.execute(new File(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]),
				null);
		
	}

}
