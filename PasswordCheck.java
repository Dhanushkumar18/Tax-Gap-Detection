import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$slYQmyNdGzin7olVN3p5be0DlH.PKZbv5H8KnzzVgXXbVxzy990O2";
        String password = "password";
        boolean matches = encoder.matches(password, hash);
        System.out.println("Password 'password' matches hash: " + matches);
    }
}
