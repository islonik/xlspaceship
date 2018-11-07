package org.games.xlspaceship.web;

import org.games.xlspaceship.impl.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ComponentScan({"org.games.xlspaceship.impl", "org.games.xlspaceship.web"})
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableAspectJAutoProxy
public class Application implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private UserServices userServices;

    public static void main(String[] args) {
        Object[] sources = {Application.class};
        SpringApplication.run(sources, args);

        log.info("XL Spaceship Application has started.");
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] cmdArgs = args.getSourceArgs();
        setUpUser(cmdArgs);
    }

    private void setUpUser(String[] args) {
        String user = null;
        String fullName = null;

        if (args != null && args.length == 2) {
            user = args[0];
            fullName = args[1];

            userServices.setUserId(user);
            userServices.setFullName(fullName);
        } else {
            if (args != null && args.length == 1) {
                log.warn("XL Spaceship instance requires two incoming parameters.");
            }
            userServices.setUpAI();
        }
        log.info("XL Spaceship Application is going to be managed by user = '{}' where 'Full Name' = '{}'.",
                userServices.getUserId(),
                userServices.getFullName()
        );
    }



}
