package ch.mobi.ufi.document;

import ch.mobi.ufi.mailing.EmailSender;
import ch.mobi.ufi.mailing.Notifier;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"de.chandre.velocity2.spring"})
public class TestApplication {

    @Autowired
    VelocityEngine velocityEngine;

    @Bean
    public DefaultDocumentGenerator documentGenerator() {
        return new DefaultDocumentGenerator(velocityEngine);
    }

    @Bean
    public Notifier notifier() {
        return new EmailSender();
    }
}
