package org.zpi.conferoapi.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl {
    private final JavaMailSender javaMailSender;

    private static final String SUBJECT = "Confirm you E-Mail - MFA Application Registration";
    private static final String MESSAGE_CONTENT = """
            <html>
            <body> 
            <h2>Dear user,</h2>
             <br/> We're excited to have you get started.  
            Please click on below link to confirm your account.
             <br/>    %s 
            <br/> Regards,<br/> 
            MFA Registration team 
            </body> 
            </html>""";


    public void sendVerificationEmail(String recipientEmail, String link) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(SUBJECT);
        mailMessage.setText(String.format(MESSAGE_CONTENT, link));
        javaMailSender.send(mailMessage);
    }

}
