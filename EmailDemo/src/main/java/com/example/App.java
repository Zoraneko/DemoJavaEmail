package com.example;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class App {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String fromEmail = dotenv.get("SENDER_EMAIL");
        String password = dotenv.get("SENDER_PASSWORD");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Email Demo App ===");
            System.out.println("1. Gửi cho 1 người");
            System.out.println("2. Gửi cho nhiều người (cách nhau bằng dấu phẩy)");
            System.out.print("Chọn chế độ: ");
            int choice = Integer.parseInt(scanner.nextLine());

            System.out.print("Chủ đề: ");
            String subject = scanner.nextLine();

            System.out.print("Nội dung: ");
            String content = scanner.nextLine();

            if (choice == 1) {
                System.out.print("Email người nhận: ");
                String toEmail = scanner.nextLine();
                sendEmail(fromEmail, password, new String[] { toEmail }, subject, content);
            } else if (choice == 2) {
                System.out.print("Danh sách email người nhận (cách nhau bằng dấu phẩy): ");
                String[] toEmails = scanner.nextLine().split("\\s*,\\s*");
                sendEmail(fromEmail, password, toEmails, subject, content);
            } else {
                System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    public static void sendEmail(String from, String password, String[] toList, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));

            // Kiểm tra lại email
            InternetAddress[] addresses = Arrays.stream(toList)
                    .map(email -> {
                        try {
                            return new InternetAddress(email);
                        } catch (AddressException e) {
                            System.out.println("Email không hợp lệ: " + email);
                            return null;
                        }
                    })
                    .filter(addr -> addr != null)
                    .toArray(InternetAddress[]::new);

            msg.setRecipients(Message.RecipientType.TO, addresses);
            msg.setSubject(subject);
            msg.setText(body);

            Transport.send(msg);

            System.out.println("Email đã được gửi thành công đến: " + String.join(", ", toList));
        } catch (MessagingException e) {
            System.out.println("Gửi email thất bại:");
            e.printStackTrace();
        }
    }
}
