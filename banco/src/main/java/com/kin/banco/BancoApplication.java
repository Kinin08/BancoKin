package com.kin.banco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class BancoApplication {


	public static void main(String[] args) {
		SpringApplication.run(BancoApplication.class, args);

		Scanner scanner = new Scanner(System.in);

		System.out.print("Digite seu nome: ");

		String name = scanner.nextLine();

		System.out.println("Ola " + name);

		scanner.close();
	}

}
