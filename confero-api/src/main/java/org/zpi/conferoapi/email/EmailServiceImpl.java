package org.zpi.conferoapi.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl {

    private final JavaMailSender javaMailSender;


    private static final String SUBJECT = "Confirm you E-Mail";
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


    public void sendVerificationEmail(String recipientEmail, String link) throws MessagingException {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage);
        helper.setTo(recipientEmail);
        helper.setSubject(SUBJECT);
        helper.setText(String.format(MESSAGE_CONTENT, link), true);
        javaMailSender.send(mailMessage);
    }

}
