package org.dre0065;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@SpringBootApplication
public class App implements CommandLineRunner
{
    public static void main(String[] args) {SpringApplication.run(App.class, args);}

    @Override
    public void run(String... args) throws Exception {System.out.println("The application is running at: http://localhost:8080/");}
}