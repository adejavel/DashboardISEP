package com.rhododendron.dashboardisep;

import it.ozimov.springboot.mail.model.Email;
import javax.mail.internet.InternetAddress;
import static com.google.common.collect.Lists.newArrayList;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import org.springframework.beans.factory.annotation.Autowired;

class EmailTask implements Runnable{
    private String message;
    private String email;
    private it.ozimov.springboot.mail.service.EmailService emailService;

    public EmailTask(String message,String email,it.ozimov.springboot.mail.service.EmailService serv){
        this.message = message;
        this.email=email;
        this.emailService = serv;
    }

    @Override
    public void run() {
        try {
            Email email = DefaultEmail.builder()
                    .from(new InternetAddress("rhododendronandco@gmail.com", "DashboardISEP notification"))
                    .to(newArrayList(new InternetAddress(
                            this.email,this.email)))
                    .subject("Une tache est due prochainement")
                    .body(this.message)
                    .encoding("UTF-8").build();
            this.emailService.send(email);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}