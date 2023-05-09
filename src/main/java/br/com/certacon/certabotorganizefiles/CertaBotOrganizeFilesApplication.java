package br.com.certacon.certabotorganizefiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CertaBotOrganizeFilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertaBotOrganizeFilesApplication.class, args);
    }

}
