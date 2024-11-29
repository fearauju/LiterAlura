package br.com.Challenger.LiterAlura.main;

import br.com.Challenger.LiterAlura.service.TerminalColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Main {

    private final MenuHandler menuHandler; // Classe responsável pelo menu principal
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public Main(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }

    public void displayMenu() {
        boolean isRunning = true;

        while (isRunning) {
            menuHandler.showMenu(); // Exibe o menu
            System.out.println();
            System.out.print(TerminalColors.colorize("Enter a menu option: ",TerminalColors.BLUE));
            System.out.println();
            int option = menuHandler.readOption(scanner); // Lê a opção do utilizador

            if (option == 0) {
                System.out.println(TerminalColors.colorize("Exiting the program...",TerminalColors.YELLOW));
                isRunning = false;
            } else {
                menuHandler.executeOption(option); // Executa a ação correspondente à opção
            }
        }
    }
}
