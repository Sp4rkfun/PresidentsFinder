import java.util.Scanner;

public class PresidentialSearchApp {

	public static void main(String[] args) {
		System.out.println("Initializing PageFinder...");
		PageFinder finder = new PageFinder();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to the Presidential Search Application!");
		System.out.println("Press enter to search. Type quit to exit.");
		while (true) {
			System.out.print("Search: ");
			String input = scanner.nextLine();
			if(input.equals("quit")) {
				break;
			}
			for (String term : input.trim().split("\\s+")) {
				System.out.println(term);
			}
			
		}
		scanner.close();
	}

}
